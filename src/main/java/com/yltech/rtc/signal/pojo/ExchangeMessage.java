package com.yltech.rtc.signal.pojo;

import lombok.*;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeMessage implements Serializable {
    private String userId;
    private String roomId;
    private Object data;
}
