--
-- Created by IntelliJ IDEA.
-- User: chenjunwen
-- Date: 2018/1/5
-- Time: 下午5:15
-- 公测开盘抢房逻辑
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
local function judgeBuildingOutOfPrint(buildingId, stateName)
    local isOutOfPrint = true
    local houseIds = redis.call("ZRANGE","Ss.House.Building."..buildingId, 0, -1)
    for i = 1, #houseIds do
        local houseId = houseIds[i]
        local state = redis.call("HGET", houseId, stateName)
        if tonumber(state) == 0 then
            isOutOfPrint = false
            break
        end
    end
    return isOutOfPrint
end

-- 判断时间在正式开盘内（1）还是公测开盘内（0）不再开盘时间内（-1）
local function getOpenState(time)
    local batch = hGetAllAsTable("Hs.Batch."..batchId);
    -- 正式开盘判断
    local formalSaleStarTime = tonumber(batch["formalSaleStarTime"])
    local formalSaleEndTime = tonumber(batch["formalSaleEndTime"])
    if (formalSaleStarTime <= time) and (formalSaleEndTime >= time) then
        return 1
    end

    -- 公测开盘判断
    local openBetaStarTime = tonumber(batch["openBetaStarTime"])
    local openBetaEndTime = tonumber(batch["openBetaEndTime"])
    if (openBetaStarTime <= time) and (openBetaEndTime >= time) then
        return 0
    end

    -- 未开盘
    return -1
end


-- 秒杀成功，更新楼栋和房间的状态
local function seckillSuccess(buildingId, houseKey, userSeckillKey, stateName)
    -- 购房成功，改变该房间的状态,保存用户所购房间
    redis.call("HINCRBY", houseKey, stateName, -1)
    redis.call("ZADD", userSeckillKey, time, houseId)
    -- 推送已成交的房间id
    redis.call("PUBLISH", "SeckillHouseChannel", houseId)

    -- 查看楼栋状态，如果已售完则改变整个楼栋的状态
    local isOutOfPrint = judgeBuildingOutOfPrint(buildingId)
    if isOutOfPrint then
        redis.call("HINCRBY", "Hs.Building."..buildingId, stateName, -1)
    end
end


-- 主函数
local function main ()
    local openState = getOpenState(tonumber(time))
    -- 未开盘
    if openState == -1 then
        return -3
    end
    -- 判断是正式开盘还是公测开盘，修改相应的状态
    local stateName = "state"
    if openState == 0 then
        stateName = "betaState"
    end

    -- 判断用户是否在抢房批次中
    local exists = redis.call("SISMEMBER", "Se.Customer.Batch."..batchId, customerId)
    if exists == 0 then
        return -2
    end

    -- 判断用户购房数量
    local userSeckillKey = "Ss.House.Seckill."..batchId.."."..customerId
    -- 公测使用的key
    if  openState == 0 then
        userSeckillKey = "Ss.House.Seckill.Beta."..batchId.."."..customerId
    end

    local houseNum = redis.call("ZCARD", userSeckillKey)
    if houseNum >= tonumber(maxHouseNum) then
        return 0 -- 购房数量已上线
    end

    -- 判断房间是否已售
    local houseKey = "Hs.House."..houseId
    local house = hGetAllAsTable(houseKey)
    local state = house[stateName]
    if state==nil then
        return -4
    end


    -- 房间已售完(-1)
    local batchCustomerId = house["batchCustomerId"]
    if (tonumber(state) < 0) or (batchCustomerId ~= nil) then
        return -1
    end

    -- 购房成功
    local buildingId = house["buildingId"]
    seckillSuccess(buildingId, houseKey, userSeckillKey, stateName);
    return 1
end
return main()

