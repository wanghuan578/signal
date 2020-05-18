package com.yltech.rtc.signal.socketio;

import com.yltech.rtc.signal.pojo.RoomInfo;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Component
public class RoomManager {

    private Map<String, RoomInfo> room_map = null;

    public RoomManager() {
        room_map = new ConcurrentHashMap<>();
    }

}
