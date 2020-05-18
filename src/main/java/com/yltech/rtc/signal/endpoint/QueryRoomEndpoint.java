package com.yltech.rtc.signal.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class QueryRoomEndpoint {

    @RequestMapping(path = {"/"})
    public String HelloSpring (){
        log.info("tls -    ");
        return "spring boot tls";
    }
}
