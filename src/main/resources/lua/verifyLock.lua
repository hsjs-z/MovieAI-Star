-- 原子操作：验证锁归属（不删除，只检查）
-- 用于 seatYes 前置校验 + 业务幂等判断
-- KEYS[1] = Redis 锁的 key
-- ARGV[1] = 当前操作的 userId（String）
-- 返回值：
--   1  = 验证通过（锁存在 + 归属匹配）
--   0  = 锁不存在
--  -1  = 归属不匹配

local value = redis.call('GET', KEYS[1])

if not value then
    return 0
end

local colonPos = string.find(value, ':')
local lockOwner = string.sub(value, 1, colonPos - 1)

if lockOwner ~= ARGV[1] then
    return -1
end

return 1