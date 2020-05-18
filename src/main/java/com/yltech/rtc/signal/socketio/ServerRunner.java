package com.yltech.rtc.signal.socketio;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class ServerRunner implements CommandLineRunner {

    private final static Integer ROOM_USER_LIMIT = 3;
    private final SocketIOServer server;

    @Autowired
    public ServerRunner(SocketIOServer server) {
        this.server = server;
    }

    @Override
    public void run(String... args) throws Exception {

        server.addConnectListener(new ConnectListener() {
            public void onConnect(SocketIOClient client) {
                log.info("android connect ------------------------ ");
                client.sendEvent("connected", "hello notify");
                log.info("hello notify");
            }
        });

        server.addEventListener("join", Map.class, new DataListener<Map>(){
            public void onData(SocketIOClient client, Map map, AckRequest ackRequest) throws ClassNotFoundException {

                String roomId = (String) map.get("roomId");
                String userId = (String) map.get("userId");

                log.info("user: [{}], join room: [{}]", userId, roomId);

                client.joinRoom(roomId);

                Collection<SocketIOClient> clients = server.getRoomOperations(roomId).getClients();
                if (clients.size() < ROOM_USER_LIMIT) {
                    client.sendEvent("joined", roomId, userId);
                    if (clients.size() > 1) {
                        for (SocketIOClient c : clients) {
                            if (c != client) {
                                c.sendEvent("otherjoin", roomId, userId);
                            }
                        }
                    }
                }
                else {
                    client.leaveRoom(roomId);
                    client.sendEvent("full", roomId, userId);
                }

                if (ackRequest.isAckRequested()) {
                    ackRequest.sendAckData("OK");
                }
            }
        });

        server.addEventListener("leave", Map.class, new DataListener<Map>(){
            public void onData(SocketIOClient client, Map map, AckRequest ackRequest) throws ClassNotFoundException {

                String roomId = (String) map.get("roomId");
                String userId = (String) map.get("userId");

                log.info("leave room: {}", roomId);

                Collection<SocketIOClient> clients = server.getRoomOperations(roomId).getClients();
                for (SocketIOClient c : clients) {
                    if (c != client) {
                        c.sendEvent("bye", roomId, userId);
                    }
                }

                client.sendEvent("leaved", roomId, userId);
            }
        });

        server.addEventListener("message", Map.class, new DataListener<Map>(){

            public void onData(SocketIOClient client, Map msg, AckRequest ackRequest) throws ClassNotFoundException {

                Map map = (LinkedHashMap) msg;
                log.info("exchange msg: {}", JSON.toJSONString(map, true));

                String roomId = (String) map.get("roomId");
                String userId = (String) map.get("userId");
                LinkedHashMap data = (LinkedHashMap) map.get("data");

                Collection<SocketIOClient> clients = server.getRoomOperations(roomId).getClients();
                for (SocketIOClient c : clients) {
                    if (c != client) {
                        c.sendEvent("message", roomId, userId, data);
                    }
                }
            }
        });

        server.start();
    }


}
