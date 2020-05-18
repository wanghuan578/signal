package com.yltech.rtc.signal.pojo;

import lombok.Data;

import java.util.LinkedList;

@Data
public class RoomInfo {
    private LinkedList<SessionInfo> sessionInfoList;
    public RoomInfo() {
        sessionInfoList = new LinkedList();
    }
}
