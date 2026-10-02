-- 批量原子加锁（一次调用全部成功，或全部回滚）
-- KEYS[1..N] = 所有要锁的 key（如 "seat:1:3:5", "seat:1:3:6"）
-- ARGV[1] = userId（作为锁的 value 前缀）
-- ARGV[2] = TTL 秒数（如 300 = 5分钟）
-- 返回值：
--   正数 N = 全部加锁成功
--   负数 index = 第 index 个 key 加锁失败，前面已加的全部已回滚

local userId = ARGV[1]
local ttl = tonumber(ARGV[2])
local locked = {}

for i, key in ipairs(KEYS) do
    local ok = redis.call('SETNX', key, userId .. ':' .. i)
    if ok == 0 then
        for j = 1, i - 1 do
            redis.call('DEL', locked[j])
        end
        return -i
    end
    redis.call('EXPIRE', key, ttl)
    locked[#locked + 1] = key
end

return #KEYS