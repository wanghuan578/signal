package com.yltech.rtc.signal.config;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.InputStream;

//@Configuration
public class ServerConfig {

    @Value("${socketio.host}")
    private String host;
    @Value("${socketio.port}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {

        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setPingInterval(5000);
        config.setPingTimeout(3000);
        config.setWorkerThreads(100);

        InputStream key = getClass().getClassLoader().getResourceAsStream("3910169__keepfun.cn.pfx");

        //String keyStoreLocation = "3910169__keepfun.cn.pfx";
//        InputStream key = null;
//        try {
//            key = new FileInputStream(keyStoreLocation);
//        } catch (Exception e) {
//            //log.error("Error in loading jks file from: {}", keyStoreLocation);
//            e.printStackTrace();
//            return null;
//        }

//        config.setKeyStore(key);
//        config.setKeyStorePassword("CFj17tUl");

        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);

        //设置最大每帧处理数据的长度，防止他人利用大数据来攻击服务器
        config.setMaxFramePayloadLength(1024 * 1024);
        //设置http交互最大内容长度
        config.setMaxHttpContentLength(1024 * 1024);



        return new SocketIOServer(config);
    }
}
