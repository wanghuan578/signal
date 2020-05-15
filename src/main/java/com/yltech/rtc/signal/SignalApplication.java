package com.yltech.rtc.signal;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
@SpringBootApplication
public class SignalApplication {



    public static void main(String[] args) {
        SpringApplication.run(SignalApplication.class, args);
    }

    @RequestMapping(path = {"/"})
    public String HelloSpring (){
        log.info("tls -    ");
        return "spring boot tls";
    }



}
