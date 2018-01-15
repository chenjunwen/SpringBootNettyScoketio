--
-- Created by joven.
-- Date: 2017/12/25

local roomKey = KEYS[0]
local userId = KEYS[1]



local function main(roomKey, userId)
    -- 判断用户是否已经下单成功
    local userExists = redis.call("HEXISTS", "Hs.Success.User", userId)
    if (userExists == 1) then
        return "你已下单成功，无需再次下单"
    end

    -- 减库存
    local total = redis.call("GET", "St.Num");
    if (tonumber(total) <= 0) then
        return "房间已售完"
    end

    -- 保存成功的用户信息
    redis.call("DECR", "St.Num")
    redis.call("HSET", "Hs.Success.User", userId, "A"..total)
    return "下单成功,剩余房间"..total;

end

return main(roomKey, userId)

