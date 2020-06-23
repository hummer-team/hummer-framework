package com.hummer.user.plugin.user;

import lombok.Data;

import java.util.List;

@Data
public class TicketContext {
    private String  clientIp;
    private List<String> functionCodes;
    private String terminalType;
    private String ticket;
    private boolean base64;
}
