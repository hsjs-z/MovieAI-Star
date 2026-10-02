-- 原子操作：验证锁归属 + 释放锁
-- KEYS[1] = Redis 锁的 key（如 "seat:1:5:5"）
-- ARGV[1] = 当前操作的 userId（String）
-- 返回值：
--   1  = 释放成功（锁存在 + 归属匹配 + 已删除）
--   0  = 锁不存在（可能已过期或被删）
--  -1  = 归属不匹配（不是这个用户的锁，不能删）

local value = redis.call('GET', KEYS[1])

if not value then
    return 0
end

local colonPos = string.find(value, ':')
local lockOwner = string.sub(value, 1, colonPos - 1)

if lockOwner ~= ARGV[1] then
    return -1
end

redis.call('DEL', KEYS[1])
return 1