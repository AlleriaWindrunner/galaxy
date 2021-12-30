--RedisCumulative
--KEY[1] day KEY ; ARG[1] mode : 0 day, 1 month, 2 day and month
--KEY[2] month KEY ; ARG[2] nil
--hash key KEY[2]..last  , hash value ARG[2]..last 

if #KEYS == 0 then
	return nil
end

local argvLength = #ARGV

if argvLength < 3 or argvLength%2 == 0 then
	return nil
end

local dayKey = KEYS[1]
local monthKey = KEYS[2]
local mode = ARGV[1]

for k,v in ipairs(ARGV) do
	if k >= 2 and k%2 == 0 then
		-- day
		if tonumber(mode) == 0 or tonumber(mode) == 2 then
			redis.call('HINCRBY',dayKey,ARGV[k],ARGV[k+1])
			--redis.logging(redis.LOG_WARNING,"mode is"..ARGV[1]..", key is "..KEYS[1]..", hash key is "..KEYS[k]..", hash value is "..ARGV[k])
		end
		-- month
		if tonumber(mode) == 1 or tonumber(mode) == 2 then
			redis.call('HINCRBY',monthKey,ARGV[k],ARGV[k+2])
			--redis.logging(redis.LOG_WARNING,"mode is"..ARGV[1]..", key is "..KEYS[2]..", hash key is "..KEYS[k]..", hash value is "..ARGV[k])
		end
	end
end

return 0