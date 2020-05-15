package com.yltech.rtc.signal;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class SignalApplication {

    private final static Integer ROOM_USER_LIMIT = 3;

    public static void main(String[] args) {
        SpringApplication.run(SignalApplication.class, args);
        try {
            init();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(path = {"/*"})
    public String HelloSpring (){
        //log.info("tls -    ");
        return "spring boot tls";
    }


    private static void init() throws FileNotFoundException {
        Configuration config = new Configuration();

        //InputStream key = this.getClass().getClassLoader().getResourceAsStream("3910169__keepfun.cn.pfx");
        InputStream key = new FileInputStream(new File("D:\\证书\\tomcat\\3910169__keepfun.cn.pfx"));
        config.setKeyStore(key);
        config.setKeyStorePassword("CFj17tUl");

        config.setHostname("localhost");
        config.setPort(9999);
        SocketIOServer server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener() {
            // 添加客户端连接监听器
            public void onConnect(SocketIOClient client) {
                log.info("onConnect ------------------------ ");
                client.sendEvent("connected", "hello");
            }
        });

        //监听客户端事件，client_info为事件名称，-自定义事件
        server.addEventListener("client_info", String.class, new DataListener<String>(){
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws ClassNotFoundException {
                //客户端推送advert_info事件时，onData接受数据，这里是string类型的json数据，还可以为Byte[],object其他类型
                String sa = client.getRemoteAddress().toString();
                String clientIp = sa.substring(1,sa.indexOf(":"));//获取客户端连接的ip
                Map params = client.getHandshakeData().getUrlParams();//获取客户端url参数
                log.info(clientIp+"：客户端：************"+data);
            }
        });

        //监听客户端事件，client_info为事件名称，-自定义事件
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

        //添加客户端断开连接事件
        server.addDisconnectListener(new DisconnectListener(){
            public void onDisconnect(SocketIOClient client) {
                String sa = client.getRemoteAddress().toString();
                String clientIp = sa.substring(1,sa.indexOf(":"));//获取设备ip
                log.info(clientIp+"-------------------------"+"客户端已断开连接");
                //给客户端发送消息
                client.sendEvent("advert_info",clientIp+"客户端你好，我是服务端，期待下次和你见面");
            }
        });
        server.start();


    }

}
