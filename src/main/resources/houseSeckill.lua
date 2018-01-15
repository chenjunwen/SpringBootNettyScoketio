--
-- Created by IntelliJ IDEA.
-- User: chenjunwen
-- Date: 2018/1/5
-- Time: 下午5:15
-- 正式开盘抢房逻辑
--

local customerId = KEYS[1]
local houseId = KEYS[2]
local maxHouseNum = KEYS[3]
local batchId = KEYS[4]
local time = KEYS[5]


-- 获取hash对应的table
local function hGetAllAsTable(hashKey)
    local flat_map = redis.call('HGETALL', hashKey)
    local result = {}
    for i = 1, #flat_map, 2 do
        result[flat_map[i]] = flat_map[i + 1]
    end
    return result
end

-- 判断整个楼栋房间是否已售完
local function judgeBuildingOutOfPrint(buildingId)
    local isOutOfPrint = true
    local houseIds = redis.call("ZRANGE","Ss.House.Building."..buildingId, 0, -1)
    for i = 1, #houseIds do
        local houseId = houseIds[i]
        local state = redis.call("HGET", houseId, "state")
        if tonumber(state) == 0 then
            isOutOfPrint = false
            break
        end
    end
    return isOutOfPrint
end



-- 主函数
local function main (customerId, houseId, maxHouseNum, batchId, time)
    -- 判断用户是否在抢房批次中
    local exists = redis.call("SISMEMBER", "Se.Customer.Batch."..batchId, customerId)
    if exists == 0 then
        return -2
    end

    -- 判断用户购房数量
    local userSeckillKey = "Ss.House.Seckill."..batchId.."."..customerId
    local houseNum = redis.call("ZCARD", userSeckillKey)
    if houseNum >= tonumber(maxHouseNum) then
        return 0 -- 购房数量已上线
    end

    -- 判断房间是否已售
    local houseKey = "Hs.House."..houseId
    local house = hGetAllAsTable(houseKey)
    local state = house["state"]
    local batchCustomerId = house["batchCustomerId"]
    if (tonumber(state) < 0) or (batchCustomerId ~= nil) then
        return -1 -- 房间已售完
    end

    -- 购房成功，改变该房间的状态,保存用户所购房间
    redis.call("HINCRBY", houseKey, "state", -1)
    redis.call("ZADD", userSeckillKey, time, houseId)

    -- 查看楼栋状态，如果已售完则改变整个楼栋的状态
    local buildingId = house["buildingId"]
    local isOutOfPrint = judgeBuildingOutOfPrint(buildingId)
    if isOutOfPrint then
        redis.call("HINCRBY", "Hs.Building."..buildingId, "state", -1)
    end
    return 1
end

return main(customerId, houseId, maxHouseNum, batchId, time)


