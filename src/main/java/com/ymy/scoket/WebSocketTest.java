package com.ymy.scoket;

import com.ymy.common.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/websocket")
@Component
public class WebSocketTest {
    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    // concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<WebSocketTest> webSocketSet = new CopyOnWriteArraySet<WebSocketTest>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    @Autowired
    RedisUtils redis;

    /**
     * 连接建立成功调用的方法
     * @param session
     */
    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    @OnClose
    public void onClose(){
        webSocketSet.remove(this);
        subOnlineCount();
        System.out.println("有连接关闭！当前在线人数："+getOnlineCount());
    }


    @OnError
    public void onError(Throwable error){
        System.out.println("socket连接异常");
        error.printStackTrace();
    }


    /**
     * 收到客户端消息后调用的方法
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session){
        System.out.println("来自客户端的消息:" + message);
        // 群发消息
        for (WebSocketTest item : webSocketSet) {
            item.sendMessage(message);
        }
    }

    /**
     * 根据自己的需要发送信息
     * @param message
     */
    public void sendMessage(String message){
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            System.out.println("socket发送信息异常");
            e.printStackTrace();
        }
    }

    /**
     * 获取当前连接数
     * @return
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    /**
     * 增加连接数
     */
    public static synchronized void addOnlineCount() {
        WebSocketTest.onlineCount++;
    }

    /**
     * 减少连接数
     */
    public static synchronized void subOnlineCount() {
        WebSocketTest.onlineCount--;
    }

}
