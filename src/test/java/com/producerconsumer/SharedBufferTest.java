package com.producerconsumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests for SharedBuffer (wait/notify implementation)
class SharedBufferTest {

    @Test
    @DisplayName("Test SharedBuffer basic operations")
    void testBasicOperations() throws InterruptedException {
        // Arrange
        SharedBuffer buffer = new SharedBuffer(5);

        // Act & Assert
        buffer.produce(1);
        buffer.produce(2);
        assertEquals(2, buffer.size());

        int item1 = buffer.consume();
        assertEquals(1, item1);
        assertEquals(1, buffer.size());

        int item2 = buffer.consume();
        assertEquals(2, item2);
        assertEquals(0, buffer.size());
    }

    @Test
    @DisplayName("Test SharedBuffer with producer-consumer threads")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void testWithThreads() throws InterruptedException {
        // Arrange
        List<Integer> source = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            source.add(i);
        }

        SharedBuffer buffer = new SharedBuffer(5);
        List<Integer> destination = new ArrayList<>();

        // Act
        Thread producer = new Thread(new WaitNotifyProducer(buffer, source));
        Thread consumer = new Thread(new WaitNotifyConsumer(buffer, destination, source.size()));

        producer.start();
        consumer.start();

        producer.join();
        consumer.join(10000);

        // Assert
        assertEquals(source.size(), destination.size(), "All items should be transferred");
        assertEquals(source, destination, "Items should be in same order");
    }

    @Test
    @DisplayName("Test SharedBuffer blocks when full")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testBufferBlocksWhenFull() throws InterruptedException {
        // Arrange
        SharedBuffer buffer = new SharedBuffer(2);
        List<Boolean> producerBlocked = new ArrayList<>();

        // Act
        Thread producer = new Thread(() -> {
            try {
                buffer.produce(1);
                buffer.produce(2);
                buffer.produce(3); // This should block
                producerBlocked.add(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        Thread.sleep(300); // Let producer fill buffer

        // Assert
        assertEquals(2, buffer.size(), "Buffer should be full");
        assertTrue(producerBlocked.isEmpty(), "Producer should be blocked");

        // Unblock producer by consuming
        buffer.consume();
        Thread.sleep(200);

        assertFalse(producerBlocked.isEmpty(), "Producer should have unblocked");
        producer.join(2000);
    }

    @Test
    @DisplayName("Test SharedBuffer blocks when empty")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testBufferBlocksWhenEmpty() throws InterruptedException {
        // Arrange
        SharedBuffer buffer = new SharedBuffer(5);
        List<Integer> consumed = new ArrayList<>();

        // Act
        Thread consumer = new Thread(() -> {
            try {
                Integer item = buffer.consume(); // Should block on empty buffer
                consumed.add(item);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        consumer.start();
        Thread.sleep(300); // Consumer should be waiting

        // Assert
        assertEquals(0, buffer.size(), "Buffer should be empty");
        assertTrue(consumed.isEmpty(), "Consumer should be blocked");

        // Unblock consumer by producing
        buffer.produce(42);
        consumer.join(2000);

        assertEquals(1, consumed.size(), "Consumer should have consumed one item");
        assertEquals(42, consumed.get(0), "Consumed item should be 42");
    }

    @Test
    @DisplayName("Test multiple producers and consumers with SharedBuffer")
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void testMultipleThreads() throws InterruptedException {
        // Arrange
        List<Integer> source1 = new ArrayList<>();
        List<Integer> source2 = new ArrayList<>();
        for (int i = 1; i <= 10; i++) source1.add(i);
        for (int i = 11; i <= 20; i++) source2.add(i);

        SharedBuffer buffer = new SharedBuffer(5);
        List<Integer> destination = new ArrayList<>();

        // Act
        Thread producer1 = new Thread(new WaitNotifyProducer(buffer, source1));
        Thread producer2 = new Thread(new WaitNotifyProducer(buffer, source2));
        Thread consumer = new Thread(new WaitNotifyConsumer(buffer, destination, 20));

        producer1.start();
        producer2.start();
        consumer.start();

        producer1.join();
        producer2.join();
        consumer.join(15000);

        // Assert
        assertEquals(20, destination.size(), "All items should be consumed");
        assertTrue(destination.containsAll(source1), "All items from source1 present");
        assertTrue(destination.containsAll(source2), "All items from source2 present");
    }
}