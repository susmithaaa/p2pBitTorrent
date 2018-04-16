package com;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: @DilipKunderu
 */
public class Client implements Runnable {
    private ExecutorService outThreadPool;
    private List<RemotePeerInfo> peersToConnectTo;
    private Thread runningThread;

    Client(List<RemotePeerInfo> peersToConnectTo) {
        this.peersToConnectTo = peersToConnectTo;
        this.outThreadPool = Executors.newFixedThreadPool(this.peersToConnectTo.size());
    }

    @Override
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        for (RemotePeerInfo remote : this.peersToConnectTo) {
            try {
                this.outThreadPool.execute(
                        new OutgoingRequestsHandler(remote)
                );
            } catch (Exception e) {
                throw new RuntimeException("Threadpool size exceeded", e);
            }
        }
    }
}
