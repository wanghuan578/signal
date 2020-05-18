package com.yltech.rtc.signal.pojo;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.Data;

@Data
public class SessionInfo {
    private String userId;
    private String roomId;
    private SocketIOClient ioClient;
}
