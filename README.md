# Intuit-Build-Challenge

## Assignment 1: Producer-Consumer Pattern

### Objective
Implement producer-consumer pattern with thread synchronization demonstrating:
- Thread Synchronization
- Concurrent Programming  
- Blocking Queues
- Wait/Notify Mechanism

### Setup

1. **Clone Repository**
```bash
git clone https://github.com/palurunikhita/Intuit-Build-Challenge.git
```

2. **Build Project**
```bash
mvn clean install
```

### Run Demo

**IntelliJ:**
- Right-click `ProducerConsumerDemo.java` → Run

**Maven:**
```bash
mvn exec:java -Dexec.mainClass="com.producerconsumer.ProducerConsumerDemo"
```

### Sample Output

```
Demo 1: BlockingQueue Implementation

Producer-1 produced: 1 | Queue size: 1
Consumer-1 consumed: 1 | Destination size: 1
...
Producer-1 finished producing
Consumer-1 consumed: 8 | Destination size: 8
...
Consumer-1 finished consuming

Final destination: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
Items transferred: 10

Demo 2: Wait/Notify Implementation

Buffer empty, consumer is waiting
Produced: 11 | Buffer size: 1
Consumed: 11 | Buffer size: 0
...
Buffer full, producer is waiting
Consumed: 17 | Buffer size: 2
...

Final destination: [11, 12, 13, 14, 15, 16, 17, 18, 19, 20]
Items transferred: 10

Demo 3: Multiple Producers and Consumers

Producer-1 produced: 10 | Queue size: 1
Consumer-1 consumed: 10 | Destination size: 1
...
Producer-2 finished producing
Producer-1 finished producing
Consumer-2 consumed: 500 | Destination size: 9
...

Final destination: [10, 100, 20, 200, 30, 300, 400, 40, 500, 50]
Items transferred: 10
```

### Run Tests

```bash
# All tests
mvn test

# Specific tests
mvn test -Dtest=ProducerConsumerTest
```

**IntelliJ:**
- Right-click `test/java/com/producerconsumer` → Run 'Tests in...'

---

## Assignment 2: Sales Data Analysis

### Objective
Perform data analysis on CSV sales data demonstrating:
- Functional Programming
- Stream Operations
- Data Aggregation
- Lambda Expressions

### Dataset
**Source:** Kaggle Superstore Sales Dataset  
**URL:** https://www.kaggle.com/datasets/vivek468/superstore-dataset-final  

### Setup

1. **Build Project** (if not already done)
```bash
mvn clean install
```

### Run Analysis

**IntelliJ:**
- Right-click `SuperstoreAnalysisDemo.java` → Run

**Maven:**
```bash
mvn exec:java -Dexec.mainClass="com.dataanalysis.SuperstoreAnalysisDemo"
```

### Sample Output

```
SUPERSTORE SALES DATA ANALYSIS
Dataset: Kaggle Superstore Sales Dataset

Loading data from: data/Superstore.csv
Header: Row ID,Order ID,Order Date,Ship Date,Ship Mode,Customer ID,Customer Name,Segment,Country,City,State,Postal Code,Region,Product ID,Category,Sub-Category,Product Name,Sales,Quantity,Discount,Profit
Successfully parsed 9994 records
Loaded 9994 sales records

ANALYSIS 1: Basic Revenue and Profit Metrics

ANALYSIS 2: Sales and Profit by Category

ANALYSIS 3: Regional Performance

ANALYSIS 4: Top Products

ANALYSIS 5: Profitability Analysis

ANALYSIS 6: Temporal Trends

ANALYSIS 7: Customer Segment & Shipping Analysis

Analysis Complete!
```

### Run Tests

```bash
# All tests
mvn test

# Specific tests
mvn test -Dtest=SalesAnalyzerTest
```

**IntelliJ:**
- Right-click `test/java/com/dataanalysis` → Run 'Tests in...'

---

## Project Structure

```
Intuit-Build-Challenge/
├── src/
│   ├── main/java/
│   │   ├── com/producerconsumer/      # Assignment 1
│   │   │   ├── ProducerConsumer.java
│   │   │   ├── SharedBuffer.java
│   │   │   ├── WaitNotifyProducer.java
│   │   │   ├── WaitNotifyConsumer.java
│   │   │   └── ProducerConsumerDemo.java
│   │   │
│   │   └── com/dataanalysis/          # Assignment 2
│   │       ├── SalesRecord.java
│   │       ├── SalesDataParser.java
│   │       ├── SalesAnalyzer.java
│   │       └── SuperstoreAnalysisDemo.java
│   │
│   └── test/java/
│       ├── com/producerconsumer/      # Assignment 1 Tests
│       │   ├── ProducerConsumerTest.java
│       │   └── SharedBufferTest.java
│       │
│       └── com/dataanalysis/          # Assignment 2 Tests
│           ├── SalesAnalyzerTest.java
│           └── SalesDataParserTest.java
│
├── data/
│   └── Sample - Superstore.csv        # Download required
│
├── pom.xml
└── README.md
```

---

## Requirements

- Java 11+
- Maven 3.6+
- JUnit 5

---

## Verify Installation

```bash
java -version
mvn -version
```

---

## Quick Commands

```bash
# Build project
mvn clean install

# Run all tests
mvn test

# Run Assignment 1 demo
mvn exec:java -Dexec.mainClass="com.producerconsumer.ProducerConsumerDemo"

# Run Assignment 2 analysis
mvn exec:java -Dexec.mainClass="com.dataanalysis.SuperstoreAnalysisDemo"
```

---

## Author
**Nikhita Paluru**  
