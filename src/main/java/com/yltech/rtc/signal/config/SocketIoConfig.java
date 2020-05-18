package com.yltech.rtc.signal.config;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.InputStream;

@Slf4j
@Configuration
public class SocketIoConfig {

    @Value("${socket.io.port}")
    private Integer port;

    @Value("${ssl.key-store-name}")
    private String keyStoreName;

    @Value("${ssl.key-store-password}")
    private String keyStorePwd;

    @Bean
    public SocketIOServer socketIOServer() {

        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        InputStream key = this.getClass().getClassLoader().getResourceAsStream(keyStoreName);
        config.setKeyStore(key);
        config.setKeyStorePassword(keyStorePwd);
        //config.setHostname("localhost");
        config.setPort(port);

        return new SocketIOServer(config);
    }
}
