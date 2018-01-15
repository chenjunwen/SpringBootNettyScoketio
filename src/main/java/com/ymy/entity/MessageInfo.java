package com.ymy.entity;

import java.util.Set;

/**
 * @author chenjunwen
 */
public class MessageInfo {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 接收消息的用户
     */
    private Set<String> toUserNames;
    /**
     * 消息
     */
    private String message;
    /**
     * 房间号
     */
    private String roomNum;

    @Override
    public String toString() {
        return "MessageInfo{" +
                "userName='" + userName + '\'' +
                ", toUserNames=" + toUserNames +
                ", message='" + message + '\'' +
                ", roomNum='" + roomNum + '\'' +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Set<String> getToUserNames() {
        return toUserNames;
    }

    public void setToUserNames(Set<String> toUserNames) {
        this.toUserNames = toUserNames;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
