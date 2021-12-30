--KEYS[1] hashstoryKey
--ARGV[1] idName
--ARGV[2] maxValue
--ARGV[3] delta

local storyKey = KEYS[1]
local idName = ARGV[1]
local maxValue = ARGV[2]
local delta = ARGV[3]
local minValue = ARGV[4]

local rdsVal = redis.call('HGET',storyKey,idName)

if (not rdsVal) or (tonumber(maxValue) ~= -1 and tonumber(maxValue) <= tonumber(rdsVal)) then
    redis.call('HSET',storyKey,idName,minValue)
end

redis.call('HINCRBY',storyKey,idName,delta)
return redis.call('HGET',storyKey,idName)

