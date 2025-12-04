package com.producerconsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// Demonstration of various Producer-Consumer scenarios
public class ProducerConsumerDemo {

    public static void main(String[] args) throws InterruptedException {
        // Demo 1: BlockingQueue
        demoBlockingQueue();

        // Demo 2: Wait/Notify
        demoWaitNotify();

        // Demo 3: Multiple Threads
        demoMultipleThreads();
    }

    private static void demoBlockingQueue() throws InterruptedException {
        System.out.println("\nDemo 1: BlockingQueue Implementation\n");

        List<Integer> source = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            source.add(i);
        }

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(5);
        List<Integer> destination = new ArrayList<>();
        final int POISON_PILL = -1;

        ProducerConsumer.Producer producer = new ProducerConsumer.Producer(queue, source, 1);
        ProducerConsumer.Consumer consumer = new ProducerConsumer.Consumer(queue, destination, 1, POISON_PILL);

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        queue.put(POISON_PILL);
        consumerThread.join();

        System.out.println("\nFinal destination: " + destination);
        System.out.println("Items transferred: " + destination.size());
    }

    private static void demoWaitNotify() throws InterruptedException {
        System.out.println("\nDemo 2: Wait/Notify Implementation\n");

        List<Integer> source = new ArrayList<>();
        for (int i = 11; i <= 20; i++) {
            source.add(i);
        }

        SharedBuffer buffer = new SharedBuffer(3);
        List<Integer> destination = new ArrayList<>();

        Thread producerThread = new Thread(new WaitNotifyProducer(buffer, source));
        Thread consumerThread = new Thread(new WaitNotifyConsumer(buffer, destination, source.size()));

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();

        System.out.println("\nFinal destination: " + destination);
        System.out.println("Items transferred: " + destination.size());
    }

    private static void demoMultipleThreads() throws InterruptedException {
        System.out.println("\nDemo 3: Multiple Producers and Consumers\n");

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);
        List<Integer> destination = new ArrayList<>();
        final int POISON_PILL = -1;

        List<Integer> source1 = new ArrayList<>();
        List<Integer> source2 = new ArrayList<>();
        for (int i = 1; i <= 5; i++) source1.add(i * 10);
        for (int i = 1; i <= 5; i++) source2.add(i * 100);

        Thread producer1 = new Thread(new ProducerConsumer.Producer(queue, source1, 1));
        Thread producer2 = new Thread(new ProducerConsumer.Producer(queue, source2, 2));
        Thread consumer1 = new Thread(new ProducerConsumer.Consumer(queue, destination, 1, POISON_PILL));
        Thread consumer2 = new Thread(new ProducerConsumer.Consumer(queue, destination, 2, POISON_PILL));

        producer1.start();
        producer2.start();
        consumer1.start();
        consumer2.start();

        producer1.join();
        producer2.join();

        queue.put(POISON_PILL);
        queue.put(POISON_PILL);

        consumer1.join();
        consumer2.join();

        System.out.println("\nFinal destination: " + destination);
        System.out.println("Items transferred: " + destination.size());
    }
}