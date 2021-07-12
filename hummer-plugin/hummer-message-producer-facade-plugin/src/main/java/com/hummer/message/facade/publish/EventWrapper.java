package com.hummer.message.facade.publish;

import com.google.common.eventbus.EventBus;
import com.hummer.message.facade.event.ProducerEvent;
import com.hummer.message.facade.event.handler.SendFailedHandler;
import com.hummer.message.facade.event.handler.SendAfterLoggerHandler;

public class EventWrapper {
    private static volatile EventBus asyncEventBus;

    static {
        registerSubscribe();
    }

    private EventWrapper() {

    }

    private static void registerSubscribe() {
        ensure();
        asyncEventBus.register(new SendFailedHandler());
        asyncEventBus.register(new SendAfterLoggerHandler());
    }

    public static <T extends ProducerEvent> void post(T event) {
        ensure();
        asyncEventBus.post(event);
    }

    private static void ensure() {
        if (asyncEventBus == null) {
            synchronized (EventBus.class) {
                if (asyncEventBus == null) {
                    asyncEventBus = new EventBus(/**Executors.newFixedThreadPool(1)**/);
                }
            }
        }
    }
}
