# 0.前言

整个项目还没有未完成，对已完成的服务进行说明。

spring-cloud的各组件不多加说明。



# 1.history-producer

模拟生产历史数据。将数据生产到kafka。



# 2.history-consumer

负责消费kafka的数据。使用批处理监听。

## 2.1整体流程

1. 取到数据 

2. 将数据存入数据库，数据库的表字段有添加一个“status”，用来记录是否有将数据正常存入Redis。【事务1】

3. 将数据存入Redis，将数据插入另一个key中；并将数据库中的该条数据的“status”变成true，表示已将数据存入Redis【事务2】
4. ack提交

## 2.2分析

存入Redis的数据为Hash，格式：

<用户ID-类型-分区<物品ID，相关信息>>

为避免BigKey，当一个Hash下的条数超过4000，就存入新的分区。



如果在进行第二步，将数据存入数据库时，出现异常，那么会回滚。

如果在进行第三步时，将数据存入Redis未成功，那么数据库的“状态”字段为false，说明数据没有被存入Redis。

如果在进行第三步时，将数据存入Redis成功后，出异常，此数数据库中的“status”字段还未来得及修改。

所以需要一个定时任务来定期校验“status”的值是否正确，即当数据存入Redis时，“status”应该为true；数据没有存入Redis，“status”应该为false。



# 3.history-check

用来定期校验数据库中“status”字段是否正确。

流程：

1. 创建一个定时任务
2. 任务中先去查询数据库中所有“status”为false的字段（会为“status”添加一个索引，避免全表扫描）
3. 将所有“status”为false的数据写回到Redis中
4. 之后更新这些数据的“status”，改为true。



# 4.history-server

历史记录的主服务



## 4.1获取指定用户、指定类型的历史记录

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

   
