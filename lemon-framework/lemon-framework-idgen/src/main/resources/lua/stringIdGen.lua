--KEYS[1] stringStoryKey
--ARGV[1] maxValue
--ARGV[2] delta
--ARGV[3] minValue

local storyKey = KEYS[1]
local maxValue = ARGV[1]
local delta = ARGV[2]
local minValue = ARGV[3]

local rdsVal = redis.call('GET',storyKey)
if (not rdsVal) or (tonumber(maxValue) ~= -1 and tonumber(maxValue) <= tonumber(rdsVal)) then
    redis.call('SET',storyKey,minValue)
end

redis.call('INCRBY',storyKey,delta)
return redis.call('GET',storyKey)