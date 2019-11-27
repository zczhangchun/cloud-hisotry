# 0.前言

### eureka注册中心图

![1](https://github.com/zczhangchun/cloud-hisotry/blob/master/image/1.png)



### 页面

![2](https://github.com/zczhangchun/cloud-hisotry/blob/master/image/2.png)



### 选择类型

![3](https://github.com/zczhangchun/cloud-hisotry/blob/master/image/3.png)



### 历史记录表

![4](https://github.com/zczhangchun/cloud-hisotry/blob/master/image/4.png)



### 物品表

![5](https://github.com/zczhangchun/cloud-hisotry/blob/master/image/5.png)

更新：将history-check删除，添加expire-monitor、history-page服务。具体看下面各服务介绍。


# 1.history-producer

模拟生产历史数据。将数据生产到kafka。




# 2.history-consumer

1.负责消费kafka的数据。使用批处理监听。

2.将redis中过期数据进行更新。【具体看后面的expire-monitor服务】

3.将查询不到的game数据重新缓存到redis中【具体后面的history-page服务】




# 3.history-server

历史记录的主服务



## 3.1获取指定用户、指定类型的历史记录

流程：

1. 查询Redis缓存
2. 如果未命中缓存，查询数据库。创建一个Redis分布式锁，只让一个线程进行查询，避免缓存穿透。
3. 查询完成后，将数据重新写入Redis，如果从数据库查询的数据为空，将空数据写入Redis，避免是恶意攻击。

踩坑：

1. 本来想使用布隆过滤器，但是发现业务中的历史记录更新的很频繁，布隆过滤器并不适合这个场景。

2. 本来想使用Redisson，但是发现，Redisson比较适合对同一个数据进行反复写操作的场景，而此场景只需要将数据库的数据写入Redis一次即可。使用了Redisson反而性能会下降。

   因为当A线程抢占到Redis分布式锁时，其他线程停滞。当A线程执行完时，会释放锁；其他线程就会抢占锁，之后判断Redis中有数据了，就释放锁。

   造成的问题：频繁的开锁，释放锁。其实并不需要这么麻烦。

   ```
   //开启分布式锁
   RLock lock = redissonClient.getLock("lock-" + UUID.randomUUID().toString());;
   try {
       lock.lock(9, TimeUnit.SECONDS);
       if (redisTemplate.hasKey(key)) {
           lock.unlock();
           getHistoryByUidAndItemType(uid, itemType);
       }else {
   
           //查数据库
   
   
       }
   }finally {
       lock.unlock();
   }
   ```



## 3.2 删除指定用户指定类型的历史记录

1.删除数据库数据

2.删除Redis中数据，使用渐进式对key进行删除。





## 3.3 删除指定物品的历史记录

1.删除数据库数据

2.删除redis中数据。




# 4.expire-monitor

此服务用来监听redis中的Game的过期数据，将过期数据发送到history-consumer服务，由kafka接收后，将数据库数据更新到Redis。

将Game数据存放到Redis中，存放24小时。

在创建一个Game数据的过期标记，存放16小时。

Game的过期标记会比Game提早过期，一旦过期，此服务就能监听到过期数据，将过期数据发送到history-consumer【kafka服务】。

history-consumer接收到过期数据后，查询数据库，将数据库的数据更新到Redis，还是存放Game数据和Game的过期标记。




# 5.history-page

调用history-server【历史记录服务】，查询出历史记录，并将每条history中game数据进行填充，渲染到页面。



在对每条history中的game数据进行填充时，game数据还是从Redis中查询。这里查询不到的Redis数据，不进行填充，直接返回一个空的Game数据。

查询不到的Redis数据，发送给history-consumer【kafka服务】，由history-consumer接收到并查询数据库，更新到Redis。







