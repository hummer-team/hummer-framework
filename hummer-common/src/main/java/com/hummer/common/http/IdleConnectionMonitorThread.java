package com.hummer.common.http;

import org.apache.http.conn.HttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

/**
 * close idle connection
 *
 * @author bingy
 */
public class IdleConnectionMonitorThread extends Thread {
    private final HttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
        super();
        this.connMgr = connMgr;
    }


    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(500);
                    connMgr.closeExpiredConnections();
                    connMgr.closeIdleConnections(10, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            // terminate
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
