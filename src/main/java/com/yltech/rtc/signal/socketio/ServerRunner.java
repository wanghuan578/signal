package com.yltech.rtc.signal.socketio;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.yltech.rtc.signal.pojo.ExchangeMessage;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;

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
            }
        });

        server.addEventListener("join", String.class, new DataListener<String>(){
            public void onData(SocketIOClient client, String roomId, AckRequest ackRequest) throws ClassNotFoundException {

                log.info("------------- join room: {}", roomId);

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
        });

        server.addEventListener("leave", String.class, new DataListener<String>(){
            public void onData(SocketIOClient client, String roomId, AckRequest ackRequest) throws ClassNotFoundException {

                log.info("leave room: {}", roomId);

                Collection<SocketIOClient> clients = server.getRoomOperations(roomId).getClients();
                for (SocketIOClient c : clients) {
                    if (c != client) {
                        c.sendEvent("bye", roomId, "");
                    }
                }

                client.sendEvent("leaved", roomId, "");
            }
        });

        server.addEventListener("message", ExchangeMessage.class, new DataListener<ExchangeMessage>(){
            public void onData(SocketIOClient client, ExchangeMessage msg, AckRequest ackRequest) throws ClassNotFoundException {

                log.info("exchange msg: {}", JSON.toJSONString(msg, true));

                Collection<SocketIOClient> clients = server.getRoomOperations(msg.getRoomId()).getClients();
                for (SocketIOClient c : clients) {
                    if (c != client) {
                        c.sendEvent("bye", msg.getRoomId(), msg.getUserId());
                    }
                }

                client.sendEvent("leaved", msg.getRoomId(), msg.getUserId());
            }
        });

//        server.addDisconnectListener(new DisconnectListener(){
//            public void onDisconnect(SocketIOClient client) {
//                String sa = client.getRemoteAddress().toString();
//                String clientIp = sa.substring(1,sa.indexOf(":"));//获取设备ip
//                log.info(clientIp+"-------------------------"+"客户端已断开连接");
//                //给客户端发送消息
//                client.sendEvent("advert_info",clientIp+"客户端你好，我是服务端，期待下次和你见面");
//            }
//        });

        server.start();
    }


}
