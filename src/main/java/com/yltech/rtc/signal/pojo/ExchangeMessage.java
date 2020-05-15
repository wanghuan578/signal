package com.yltech.rtc.signal.pojo;

import lombok.Data;

@Data
public class ExchangeMessage {
    private String userId;
    private String roomId;
    private Object data;
}
