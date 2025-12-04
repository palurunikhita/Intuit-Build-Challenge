package com.producerconsumer;

import java.util.List;

// Producer using wait/notify mechanism
public class WaitNotifyProducer implements Runnable {
    private final SharedBuffer buffer;
    private final List<Integer> source;

    public WaitNotifyProducer(SharedBuffer buffer, List<Integer> source) {
        this.buffer = buffer;
        this.source = source;
    }

    @Override
    public void run() {
        try {
            for (Integer item : source) {
                buffer.produce(item);
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}