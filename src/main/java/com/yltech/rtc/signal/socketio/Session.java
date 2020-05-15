package com.yltech.rtc.signal.socketio;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    public static Map<String, SocketIOClient> user_socket_Map = new ConcurrentHashMap<>();

}
