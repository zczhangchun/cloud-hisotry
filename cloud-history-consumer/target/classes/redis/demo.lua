local key1 = KEYS[1]
local value = ARGV[1]
local key2 = KEYS[2]
local time = tonumber(ARGV[2])
redis.call('hset',key1,key2,value)
redis.call('expire',key1,time)