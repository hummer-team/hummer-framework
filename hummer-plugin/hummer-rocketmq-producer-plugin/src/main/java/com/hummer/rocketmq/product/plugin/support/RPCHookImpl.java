package com.hummer.rocketmq.product.plugin.support;

import org.apache.rocketmq.remoting.RPCHook;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;

public class RPCHookImpl implements RPCHook {
    public static final RPCHook INSTANCE = new RPCHookImpl();

    @Override
    public void doBeforeRequest(String remoteAddr, RemotingCommand request) {

    }

    @Override
    public void doAfterResponse(String remoteAddr, RemotingCommand request, RemotingCommand response) {

    }
}
