package com.hummer.test.eventbus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.hummer.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EventTest extends BaseTest {
    @Autowired
    private EventBus eventBus;

    @Subscribe
    public void subscribe(E e) {
        System.out.println("sss" + e);
    }

    @Subscribe
    public void subscribe2(E e) {
        System.out.println("sss" + e);
    }

    @Test
    public void send() {
        E e = new E();
        e.setI(456);
        e.setN("sss");
        eventBus.post(e);
    }

    public static class E {
        private Integer i;
        private String n;

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }

        public String getN() {
            return n;
        }

        public void setN(String n) {
            this.n = n;
        }

        @Override
        public String toString() {
            return "E{" +
                    "i=" + i +
                    ", n='" + n + '\'' +
                    '}';
        }
    }
}
