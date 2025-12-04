package com.producerconsumer;

import java.util.ArrayList;
import java.util.List;

 // Alternative implementation using wait/notify for manual synchronization
 //Demonstrates low-level thread communication
public class SharedBuffer {
    private final List<Integer> buffer = new ArrayList<>();
    private final int capacity;

    public SharedBuffer(int capacity) {
        this.capacity = capacity;
    }

    // Producer adds item to buffer (blocks if full)
    public synchronized void produce(Integer item) throws InterruptedException {
        while (buffer.size() == capacity) {
            System.out.println("Buffer full, producer is waiting");
            wait(); // Wait until space available
        }

        buffer.add(item);
        System.out.println("Produced: " + item + " | Buffer size: " + buffer.size());
        notifyAll(); // Notify waiting consumers
    }

    // Consumer removes item from buffer (blocks if empty)
    public synchronized Integer consume() throws InterruptedException {
        while (buffer.isEmpty()) {
            System.out.println("Buffer empty, consumer is waiting");
            wait(); // Wait until items available
        }

        Integer item = buffer.remove(0);
        System.out.println("Consumed: " + item + " | Buffer size: " + buffer.size());
        notifyAll(); // Notify waiting producers
        return item;
    }

    public synchronized int size() {
        return buffer.size();
    }
}