package com.ymy.scoket;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.ymy.entity.MessageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class NettySocketioTest {
    @Autowired
    private SocketIOServer server;

    private Set<String> userSet = new LinkedHashSet<>();


    /**
     * 客户端连接触发
     * @param client
     */
    @OnConnect
    private void onConnect(SocketIOClient client){
        HandshakeData handshakeData = client.getHandshakeData();
        String userName = handshakeData.getSingleUrlParam("userName");
        String roomNum = handshakeData.getSingleUrlParam("roomNum");

       // server.addNamespace(roomNum);
        String message = String.format("用户%s已上线", userName);
        userSet.add(userName);
        JSONObject data = new JSONObject();
        data.put("msg", message);
        data.put("userNames", userSet);
        server.getBroadcastOperations().sendEvent("systemEvent", data);

    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client){
        HandshakeData handshakeData = client.getHandshakeData();
        String userName = handshakeData.getSingleUrlParam("userName");
        //String roomNum = handshakeData.getSingleUrlParam("roomNum");
        //server.removeNamespace(userName);
        userSet.remove(userName);
        String message = String.format("用户%s已下线", userName);
        JSONObject data = new JSONObject();
        data.put("msg", message);
        data.put("userNames", userSet);
        server.getBroadcastOperations().sendEvent("systemEvent", data);
    }

    /**
     * 当接收到消息后
     */
    @OnEvent("receiveMessageEvent")
    private void onEvent(SocketIOClient client, AckRequest request, MessageInfo data){
        JSONObject msgJson = new JSONObject();
        msgJson.put("time", System.currentTimeMillis());
        msgJson.put("msg", data.getMessage());
        msgJson.put("userName", data.getUserName());
        Set<String> userNames = data.getToUserNames();

        // 如果没有指定则发送给所有的人
        if (userNames.isEmpty()){
            server.getBroadcastOperations().sendEvent("sendMessage", msgJson);
        }else{
            for (String userName : userNames){
                SocketIONamespace namespace = server.getNamespace(userName);
                namespace.getBroadcastOperations().sendEvent("sendMessage", msgJson);
                namespace.getBroadcastOperations().sendEvent("message", data.getMessage());

            }
        }


    }



}
