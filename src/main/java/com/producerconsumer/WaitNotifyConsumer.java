package com.producerconsumer;

import java.util.List;

// Consumer using wait/notify mechanism
public class WaitNotifyConsumer implements Runnable {
    private final SharedBuffer buffer;
    private final List<Integer> destination;
    private final int itemCount;

    public WaitNotifyConsumer(SharedBuffer buffer, List<Integer> destination, int itemCount) {
        this.buffer = buffer;
        this.destination = destination;
        this.itemCount = itemCount;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < itemCount; i++) {
                Integer item = buffer.consume();
                destination.add(item);
                Thread.sleep(80);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}