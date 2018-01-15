package com.ymy.scoket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymy.entity.Cmd;
import com.ymy.entity.CmdData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author chenjunwen
 */
@ServerEndpoint("/subWebSocket")
@Component
public class SubPubWebSocket {
    private static Set<Session> sessions = new CopyOnWriteArraySet<>();
    private static Map<String,Set<Session>> sessionMap = new HashMap<String,Set<Session>>();
    private static Map<String, Set<Cmd>> wbsCmdMap = new HashMap<>();

    /**
     * 连接成功触发
     * @param session
     */
    @OnOpen
    public void onOpen(Session session){
        sessions.add(session);
    }



    /**
     * 接收到客户端消息后触发
     * @param message
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        String errStr = "";
        try {
            JSONObject data = JSON.parseObject(message);
            JSONArray cmds = data.getJSONArray("cmds");
            String batchId = data.getString("batchId");
            if(StringUtils.isBlank(batchId)){
                errStr = "批次id不能为空";
            }
            // 保存连接的sql
            Set<Session> sessions = sessionMap.get(batchId);
            if (sessions == null){
                sessions = new CopyOnWriteArraySet<>();
            }
            sessions.add(session);
            sessionMap.put(batchId, sessions);
            String sessionId = session.getId();
            if (cmds != null){
                StringBuffer errBuf = new StringBuffer();
                Set<Cmd> cmdSet = new CopyOnWriteArraySet<>();
                cmds.forEach(cmd->{
                    String cmdStr = cmd.toString();
                    boolean b = Cmd.hasCmd(cmdStr);
                    if (b){
                        Cmd e = Cmd.getEnum(cmdStr);
                        cmdSet.add(e);
                    }else{
                        errBuf.append(cmd+",");
                    }
                });

                // 如果报错的话就推错误消息
                if (errBuf.length() != 0){
                    errBuf.deleteCharAt(errBuf.length()-1);
                    errStr = "不存在" + errBuf + "内容，请重新订阅示例：{cmds: "+JSON.toJSONString(Cmd.getValues())+",batchId: *}";
                }
                // 存储客户端订阅的内容
                wbsCmdMap.put(sessionId, cmdSet);
            }
        }catch (Exception e){
            errStr = "订阅方式错误\n 示例：{cmds: "+JSON.toJSONString(Cmd.getValues())+",batchId: *}";
        }
        sendErrMsg(errStr, session);
    }

    /**
     * 发送错误数据
     * @param err
     */
    void sendErrMsg(String err, Session session){
        if(err.trim().length() != 0){
            try {
                CmdData data = new CmdData(err);
                session.getBasicRemote().sendText(JSON.toJSONString(data));
            } catch (Exception e) {
                System.out.println("发送数据异常");
            }
        }
    }




    /**
     * 根据批次发送数据
     * @param message
     * @param cmd
     */
    void sendMessage(String message, Cmd cmd){
        JSONObject obj = JSON.parseObject(message);
        String batchId = obj.getString("batchId");
        Set<Session> sessions = sessionMap.get(batchId);
        if (sessions == null) { return; }
        CmdData cmdData = new CmdData(cmd.value(), obj);
        Iterator<Session> it = sessions.iterator();
        while (it.hasNext()){
            Session session = it.next();
            String sessionId = session.getId();
            Set<Cmd> cmds = wbsCmdMap.get(sessionId);
            if (cmds == null) {continue;}
            // 判断该用户是否订阅该内容
            boolean contains = cmds.contains(cmd);
            if(contains){
                try {
                    if (session.isOpen()){
                        session.getBasicRemote().sendText(JSON.toJSONString(cmdData));
                    }else {
                        it.remove();
                    }
                } catch (Exception e) {
                    System.out.println("发送数据失败");
                }
            }
        }
    }

    /**
     * 连接关闭时调用
     */
    @OnClose
    public void onClose(Session session, @PathParam("batchId") String batchId){
        sessions.remove(session);
        Set<Session> batchIdSessions = sessionMap.get(batchId);
        if (batchIdSessions != null){
            batchIdSessions.remove(session);
        }
        // sessionMap.replace(batchId, batchIdSessions);
        wbsCmdMap.remove(session.getId());
    }

    /**
     * 连接错误
     */
    @OnError
    public void onError(Session session, Throwable error){
        new RuntimeException("webSocket连出错");

    }








}
