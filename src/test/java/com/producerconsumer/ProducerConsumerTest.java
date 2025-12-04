package com.producerconsumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

 // Comprehensive unit tests for Producer-Consumer implementation
 // Tests thread synchronization, concurrent programming, and data integrity
class ProducerConsumerTest {

    @Test
    @DisplayName("Test basic producer-consumer with BlockingQueue")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testBasicProducerConsumer() throws InterruptedException {
        // Arrange
        List<Integer> source = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            source.add(i);
        }

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(5);
        List<Integer> destination = Collections.synchronizedList(new ArrayList<>());
        final int POISON_PILL = -1;

        // Act
        ProducerConsumer.Producer producer = new ProducerConsumer.Producer(queue, source, 1);
        ProducerConsumer.Consumer consumer = new ProducerConsumer.Consumer(queue, destination, 1, POISON_PILL);

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        queue.put(POISON_PILL);
        consumerThread.join(5000);

        // Assert
        assertEquals(source.size(), destination.size(), "All items should be transferred");
        assertTrue(destination.containsAll(source), "Destination should contain all source items");
    }

    @Test
    @DisplayName("Test producer-consumer maintains data integrity")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void testDataIntegrity() throws InterruptedException {
        // Arrange
        List<Integer> source = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            source.add(i);
        }

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);
        List<Integer> destination = Collections.synchronizedList(new ArrayList<>());
        final int POISON_PILL = -1;

        // Act
        ProducerConsumer.Producer producer = new ProducerConsumer.Producer(queue, source, 1);
        ProducerConsumer.Consumer consumer = new ProducerConsumer.Consumer(queue, destination, 1, POISON_PILL);

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        queue.put(POISON_PILL);
        consumerThread.join(10000);

        // Assert
        assertEquals(source.size(), destination.size(), "No data loss should occur");

        // Check sum to verify all values transferred correctly
        int sourceSum = source.stream().mapToInt(Integer::intValue).sum();
        int destSum = destination.stream().mapToInt(Integer::intValue).sum();
        assertEquals(sourceSum, destSum, "Sum of values should match");
    }

    @Test
    @DisplayName("Test multiple producers with single consumer")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void testMultipleProducers() throws InterruptedException {
        // Arrange
        List<Integer> source1 = new ArrayList<>();
        List<Integer> source2 = new ArrayList<>();
        for (int i = 1; i <= 10; i++) source1.add(i);
        for (int i = 11; i <= 20; i++) source2.add(i);

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(5);
        List<Integer> destination = Collections.synchronizedList(new ArrayList<>());
        final int POISON_PILL = -1;

        // Act
        ProducerConsumer.Producer producer1 = new ProducerConsumer.Producer(queue, source1, 1);
        ProducerConsumer.Producer producer2 = new ProducerConsumer.Producer(queue, source2, 2);
        ProducerConsumer.Consumer consumer = new ProducerConsumer.Consumer(queue, destination, 1, POISON_PILL);

        Thread p1 = new Thread(producer1);
        Thread p2 = new Thread(producer2);
        Thread c1 = new Thread(consumer);

        p1.start();
        p2.start();
        c1.start();

        p1.join();
        p2.join();
        queue.put(POISON_PILL);
        c1.join(10000);

        // Assert
        assertEquals(20, destination.size(), "All items from both producers should be consumed");
        assertTrue(destination.containsAll(source1), "All items from producer1 should be present");
        assertTrue(destination.containsAll(source2), "All items from producer2 should be present");
    }

    @Test
    @DisplayName("Test single producer with multiple consumers")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void testMultipleConsumers() throws InterruptedException {
        // Arrange
        List<Integer> source = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            source.add(i);
        }

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);
        List<Integer> destination = Collections.synchronizedList(new ArrayList<>());
        final int POISON_PILL = -1;

        // Act
        ProducerConsumer.Producer producer = new ProducerConsumer.Producer(queue, source, 1);
        ProducerConsumer.Consumer consumer1 = new ProducerConsumer.Consumer(queue, destination, 1, POISON_PILL);
        ProducerConsumer.Consumer consumer2 = new ProducerConsumer.Consumer(queue, destination, 2, POISON_PILL);

        Thread p1 = new Thread(producer);
        Thread c1 = new Thread(consumer1);
        Thread c2 = new Thread(consumer2);

        p1.start();
        c1.start();
        c2.start();

        p1.join();
        queue.put(POISON_PILL);
        queue.put(POISON_PILL);
        c1.join(10000);
        c2.join(10000);

        // Assert
        assertEquals(source.size(), destination.size(), "All items should be consumed");
        assertTrue(destination.containsAll(source), "All source items should be in destination");
    }

    @Test
    @DisplayName("Test blocking behavior when queue is full")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testBlockingOnFullQueue() throws InterruptedException {
        // Arrange
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(2); // Small capacity
        List<Integer> source = new ArrayList<>();
        for (int i = 1; i <= 5; i++) source.add(i);

        List<Integer> destination = Collections.synchronizedList(new ArrayList<>());
        final int POISON_PILL = -1;

        // Act
        ProducerConsumer.Producer producer = new ProducerConsumer.Producer(queue, source, 1);
        Thread producerThread = new Thread(producer);
        producerThread.start();

        // Give producer time to fill queue
        Thread.sleep(500);

        // Assert - queue should be full or nearly full
        assertTrue(queue.size() >= 2, "Queue should fill up due to blocking");

        // Start consumer to drain queue
        ProducerConsumer.Consumer consumer = new ProducerConsumer.Consumer(queue, destination, 1, POISON_PILL);
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();

        producerThread.join();
        queue.put(POISON_PILL);
        consumerThread.join(5000);

        assertEquals(source.size(), destination.size(), "All items should eventually be transferred");
    }

    @Test
    @DisplayName("Test thread interruption handling")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testThreadInterruption() throws InterruptedException {
        // Arrange
        List<Integer> source = new ArrayList<>();
        for (int i = 1; i <= 100; i++) source.add(i);

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(5);
        List<Integer> destination = Collections.synchronizedList(new ArrayList<>());
        final int POISON_PILL = -1;

        ProducerConsumer.Producer producer = new ProducerConsumer.Producer(queue, source, 1);
        ProducerConsumer.Consumer consumer = new ProducerConsumer.Consumer(queue, destination, 1, POISON_PILL);

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        // Act
        producerThread.start();
        consumerThread.start();

        Thread.sleep(500);

        producer.stop(); // Stop producer
        producerThread.join(2000);

        queue.put(POISON_PILL); // Stop consumer
        consumerThread.join(2000);

        // Assert
        assertFalse(producerThread.isAlive(), "Producer should have stopped");
        assertFalse(consumerThread.isAlive(), "Consumer should have stopped");
    }

    @Test
    @DisplayName("Test FIFO order preservation")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testFIFOOrder() throws InterruptedException {
        // Arrange
        List<Integer> source = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            source.add(i);
        }

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(20);
        List<Integer> destination = Collections.synchronizedList(new ArrayList<>());
        final int POISON_PILL = -1;

        // Act - Producer finishes before consumer starts
        ProducerConsumer.Producer producer = new ProducerConsumer.Producer(queue, source, 1);
        Thread producerThread = new Thread(producer);
        producerThread.start();
        producerThread.join(); // Waits for all production to complete

        ProducerConsumer.Consumer consumer = new ProducerConsumer.Consumer(queue, destination, 1, POISON_PILL);
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();

        queue.put(POISON_PILL);
        consumerThread.join(5000);

        // Assert
        assertEquals(source, destination, "Items should be consumed in FIFO order");
    }

    @Test
    @DisplayName("Test empty source handling")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testEmptySource() throws InterruptedException {
        // Arrange
        List<Integer> source = new ArrayList<>(); // Empty source
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(5);
        List<Integer> destination = Collections.synchronizedList(new ArrayList<>());
        final int POISON_PILL = -1;

        // Act
        ProducerConsumer.Producer producer = new ProducerConsumer.Producer(queue, source, 1);
        ProducerConsumer.Consumer consumer = new ProducerConsumer.Consumer(queue, destination, 1, POISON_PILL);

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        queue.put(POISON_PILL);
        consumerThread.join(2000);

        // Assert
        assertTrue(destination.isEmpty(), "Destination should be empty");
    }

    @Test
    @DisplayName("Test concurrent access to destination list")
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void testConcurrentDestinationAccess() throws InterruptedException {
        // Arrange
        List<Integer> source1 = new ArrayList<>();
        List<Integer> source2 = new ArrayList<>();
        for (int i = 1; i <= 25; i++) source1.add(i);
        for (int i = 26; i <= 50; i++) source2.add(i);

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);
        List<Integer> destination = Collections.synchronizedList(new ArrayList<>());
        final int POISON_PILL = -1;

        // Act - Multiple consumers writing to same destination
        Thread p1 = new Thread(new ProducerConsumer.Producer(queue, source1, 1));
        Thread p2 = new Thread(new ProducerConsumer.Producer(queue, source2, 2));
        Thread c1 = new Thread(new ProducerConsumer.Consumer(queue, destination, 1, POISON_PILL));
        Thread c2 = new Thread(new ProducerConsumer.Consumer(queue, destination, 2, POISON_PILL));

        p1.start();
        p2.start();
        c1.start();
        c2.start();

        p1.join();
        p2.join();
        queue.put(POISON_PILL);
        queue.put(POISON_PILL);
        c1.join(10000);
        c2.join(10000);

        // Assert
        assertEquals(50, destination.size(), "All 50 items should be in destination");

        // Verify no duplicates (which would indicate race condition)
        long uniqueCount = destination.stream().distinct().count();
        assertEquals(50, uniqueCount, "No duplicates should exist");
    }
}