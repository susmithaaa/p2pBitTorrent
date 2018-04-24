package com;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: @DilipKunderu
 */
public class Client implements Runnable {
    private ExecutorService outThreadPool;
    private Map<Integer, RemotePeerInfo> peersToConnectTo;
    private Thread runningThread;

    Client(Map<Integer, RemotePeerInfo> peersToConnectTo) {
        this.peersToConnectTo = peersToConnectTo;
        this.outThreadPool = Executors.newFixedThreadPool(this.peersToConnectTo.size());
    }

    @Override
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        for (Map.Entry<Integer,RemotePeerInfo> e : this.peersToConnectTo.entrySet()) {
            System.out.println("spawning client request");
            RemotePeerInfo remote = e.getValue();
            try {
                this.outThreadPool.execute(
                        new OutgoingRequestsHandler(remote)
                );
            } catch (Exception ex) {
                throw new RuntimeException("Thread pool size exceeded", ex);
            }
        }
        this.outThreadPool.shutdown();
        System.out.println("client thread spawning new handlers for each outgoing request stopped");
    }
}
