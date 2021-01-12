package ai.qiwu.com.xiaoduhome.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 苗权威
 * @dateTime 19-11-18 下午5:34
 */
@ServerEndpoint("/webSocketceshi/{username}")
@Component
@Slf4j
public class WebSocketLog {
    private static Map<String, List<WebSocketLog>> clients = new ConcurrentHashMap<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    @OnOpen
    public void opOpen(@PathParam("username") String username, Session session) {
        this.session = session;
        List<WebSocketLog> logClients = clients.computeIfAbsent(username, k -> new ArrayList<>());
        logClients.add(this);
    }

    @OnClose
    public void onClose(@PathParam("username") String username) {
        List<WebSocketLog> logClients = clients.get(username);
        logClients.remove(this);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
//        for (WebSocketLog item: webSocketLogs) {
//            try {
//                item.sendMessage(message);
//            } catch (Exception e) {
//                log.error("websocket onMessage出现错误:{}",e);
//            }
//        }
        log.info("onMessage() called:"+message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("websocket出现错误"+error.toString());
    }

    /**
     * 实现服务器主动推送
     */
    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static void sendInfo(String message, String userName) {
        try {
            for (WebSocketLog log: clients.get(userName)) {
                log.sendMessage(message);
            }
        } catch (IOException e) {
            log.error("log sendInfo 出现错误:{}",e);
        }
    }
}
