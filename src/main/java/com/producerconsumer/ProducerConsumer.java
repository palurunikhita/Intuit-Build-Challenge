package com.producerconsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ProducerConsumer {

    // Producer thread that reads items from source and places them in shared queue
    static class Producer implements Runnable {
        private final BlockingQueue<Integer> queue;
        private final List<Integer> source;
        private final int producerId;
        private volatile boolean running = true;

        public Producer(BlockingQueue<Integer> queue, List<Integer> source, int producerId) {
            this.queue = queue;
            this.source = source;
            this.producerId = producerId;
        }

        @Override
        public void run() {
            try {
                for (Integer item : source) {
                    if (!running) break;

                    queue.put(item); // Blocks if queue is full
                    System.out.println("Producer-" + producerId + " produced: " + item + " | Queue size: " + queue.size());

                    // Simulate some processing time
                    Thread.sleep(100);
                }
                System.out.println("Producer-" + producerId + " finished producing");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Producer-" + producerId + " interrupted");
            }
        }

        public void stop() {
            running = false;
        }
    }

    // Consumer thread that reads items from shared queue and stores in destination
    static class Consumer implements Runnable {
        private final BlockingQueue<Integer> queue;
        private final List<Integer> destination;
        private final int consumerId;
        private volatile boolean running = true;
        private final int poisonPill;

        public Consumer(BlockingQueue<Integer> queue, List<Integer> destination,
                        int consumerId, int poisonPill) {
            this.queue = queue;
            this.destination = destination;
            this.consumerId = consumerId;
            this.poisonPill = poisonPill;
        }

        @Override
        public void run() {
            try {
                while (running) {
                    // Wait up to 2 seconds for an item
                    Integer item = queue.poll(2, TimeUnit.SECONDS);

                    if (item == null) {
                        continue; // Timeout, check running flag again
                    }

                    // Check for poison pill and signal to stop
                    if (item.equals(poisonPill)) {
                        System.out.println("Consumer-" + consumerId + " received poison pill, stopping");
                        break;
                    }

                    synchronized (destination) {
                        destination.add(item);
                    }

                    System.out.println("Consumer-" + consumerId + " consumed: " + item + " | Destination size: " + destination.size());

                    // Simulate some processing time
                    Thread.sleep(150);
                }
                System.out.println("Consumer-" + consumerId + " finished consuming");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Consumer-" + consumerId + " interrupted");
            }
        }

        public void stop() {
            running = false;
        }
    }

    // Demo: Using BlockingQueue
    public static void demoBlockingQueue() throws InterruptedException {
        System.out.println("\n=== BlockingQueue Producer-Consumer Demo ===\n");

        // Create source data
        List<Integer> source = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            source.add(i);
        }

        // Shared queue with capacity of 5
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(5);

        // Destination container (thread-safe)
        List<Integer> destination = new ArrayList<>();

        // Poison pill to signal consumer to stop
        final int POISON_PILL = -1;

        // Create and start producer
        Producer producer = new Producer(queue, source, 1);
        Thread producerThread = new Thread(producer);
        producerThread.start();

        // Create and start consumer
        Consumer consumer = new Consumer(queue, destination, 1, POISON_PILL);
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();

        // Wait for producer to finish
        producerThread.join();

        // Send poison pill to stop consumer
        queue.put(POISON_PILL);

        // Wait for consumer to finish
        consumerThread.join();

        System.out.println("\nTransfer Completed");
        System.out.println("Final destination: " + destination);
        System.out.println("Items transferred: " + destination.size());
    }

    public static void main(String[] args) throws InterruptedException {
        demoBlockingQueue();
    }
}