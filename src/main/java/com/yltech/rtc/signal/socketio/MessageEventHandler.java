package com.yltech.rtc.signal.socketio;


import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Collection;


@Slf4j
@Component
public class MessageEventHandler {

    private final static Integer ROOM_USER_LIMIT = 3;


    private final SocketIOServer server;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");

    @Autowired
    public MessageEventHandler(SocketIOServer server) {
        this.server = server;
    }

    @OnConnect
    public void connect(SocketIOClient client) {
        HandshakeData hd = client.getHandshakeData();
        log.info("connect...");
        client.sendEvent("connected", "hello notify");
    }

//    @OnDisconnect
//    public void disconnect(SocketIOClient client) {
//        Session.user_socket_Map.remove(client.get("userId"));
//    }

    @OnEvent(value = "join")
    public void join(SocketIOClient client, AckRequest ackRequest, String roomId)  {

        log.info("join room: {}", roomId);

        client.joinRoom(roomId);
        Collection<SocketIOClient> clients = server.getRoomOperations(roomId).getClients();
        if (clients.size() < ROOM_USER_LIMIT) {
            client.sendEvent("joined", roomId, "");
            if (clients.size() > 1) {
                for (SocketIOClient c : clients) {
                    if (c != client) {
                        c.sendEvent("otherjoin", roomId);
                    }
                }
            }
        }
        else {
            client.leaveRoom(roomId);
            client.sendEvent("full", roomId, "");
        }

        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData("OK");
        }
    }

    @OnEvent(value = "leave")
    public void leave(SocketIOClient client, AckRequest ackRequest, String roomId) {

        log.info("leave room: {}", roomId);

        Collection<SocketIOClient> clients = server.getRoomOperations(roomId).getClients();
        for (SocketIOClient c : clients) {
            if (c != client) {
                c.sendEvent("bye", roomId, "");
            }
        }

        client.sendEvent("leaved", roomId, "");
    }

    @OnEvent(value = "message")
    public void message(SocketIOClient client, AckRequest ackRequest, String roomId, Object data) {

        log.info("message: {}", JSON.toJSONString(data, true));

        Collection<SocketIOClient> clients = server.getRoomOperations(roomId).getClients();
        for (SocketIOClient c : clients) {
            if (c != client) {
                c.sendEvent("message", roomId, data);
            }
        }
    }

    @OnEvent(value = "client_info")
    public void msg(SocketIOClient client, AckRequest ackRequest, String roomId, String data) {

        log.info("msg: {}", data);


    }

    public void sendMessageToAllClient(String userName) {
        Collection<SocketIOClient> clients = server.getAllClients();
        for (SocketIOClient client : clients) {

        }
    }
}
