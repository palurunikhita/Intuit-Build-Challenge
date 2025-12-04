package com.dataanalysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SalesAnalyzer
 * Tests stream operations, functional programming, data aggregation, and lambda expressions
 */
class SalesAnalyzerTest {

    private List<SalesRecord> testData;
    private SalesAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        // Create test dataset
        testData = createTestData();
        analyzer = new SalesAnalyzer(testData);
    }

    /**
     * Create sample test data
     */
    private List<SalesRecord> createTestData() {
        List<SalesRecord> data = new ArrayList<>();

        data.add(new SalesRecord(
                "ORDER-001", LocalDate.of(2024, 1, 15), "Standard Class",
                "Consumer", "United States", "New York", "NY", "East",
                "Technology", "Phones", "iPhone 13", 1200.00, 2, 0.1, 240.00
        ));

        data.add(new SalesRecord(
                "ORDER-002", LocalDate.of(2024, 2, 20), "Second Class",
                "Corporate", "United States", "Los Angeles", "CA", "West",
                "Furniture", "Chairs", "Office Chair", 450.00, 3, 0.0, 135.00
        ));

        data.add(new SalesRecord(
                "ORDER-003", LocalDate.of(2024, 3, 10), "First Class",
                "Consumer", "United States", "Chicago", "IL", "Central",
                "Office Supplies", "Binders", "3-Ring Binder", 25.00, 10, 0.2, 5.00
        ));

        data.add(new SalesRecord(
                "ORDER-004", LocalDate.of(2024, 1, 25), "Standard Class",
                "Home Office", "United States", "Houston", "TX", "South",
                "Technology", "Laptops", "Dell Laptop", 1500.00, 1, 0.05, 300.00
        ));

        data.add(new SalesRecord(
                "ORDER-005", LocalDate.of(2024, 2, 14), "Same Day",
                "Consumer", "United States", "Miami", "FL", "South",
                "Furniture", "Tables", "Conference Table", 800.00, 1, 0.15, -50.00
        ));

        data.add(new SalesRecord(
                "ORDER-006", LocalDate.of(2024, 3, 5), "Standard Class",
                "Corporate", "United States", "Seattle", "WA", "West",
                "Office Supplies", "Paper", "Copy Paper", 30.00, 20, 0.0, 6.00
        ));

        return data;
    }

    @Test
    @DisplayName("Test total revenue calculation using mapToDouble and sum")
    void testCalculateTotalRevenue() {
        // Expected: 1200 + 450 + 25 + 1500 + 800 + 30 = 4005
        double totalRevenue = analyzer.calculateTotalRevenue();
        assertEquals(4005.00, totalRevenue, 0.01,
                "Total revenue should sum all sales amounts");
    }

    @Test
    @DisplayName("Test total profit calculation using stream aggregation")
    void testCalculateTotalProfit() {
        // Expected: 240 + 135 + 5 + 300 - 50 + 6 = 636
        double totalProfit = analyzer.calculateTotalProfit();
        assertEquals(636.00, totalProfit, 0.01,
                "Total profit should sum all profit values including losses");
    }

    @Test
    @DisplayName("Test grouping by category using Collectors.groupingBy")
    void testGetSalesByCategory() {
        Map<String, Double> salesByCategory = analyzer.getSalesByCategory();

        assertEquals(3, salesByCategory.size(), "Should have 3 categories");
        assertEquals(2700.00, salesByCategory.get("Technology"), 0.01);
        assertEquals(1250.00, salesByCategory.get("Furniture"), 0.01);
        assertEquals(55.00, salesByCategory.get("Office Supplies"), 0.01);
    }

    @Test
    @DisplayName("Test profit aggregation by category")
    void testGetProfitByCategory() {
        Map<String, Double> profitByCategory = analyzer.getProfitByCategory();

        assertEquals(540.00, profitByCategory.get("Technology"), 0.01);
        assertEquals(85.00, profitByCategory.get("Furniture"), 0.01);
        assertEquals(11.00, profitByCategory.get("Office Supplies"), 0.01);
    }

    @Test
    @DisplayName("Test sorting and limiting stream with top products")
    void testGetTopProductsBySales() {
        List<Map.Entry<String, Double>> topProducts = analyzer.getTopProductsBySales(3);

        assertEquals(3, topProducts.size());
        assertEquals("Dell Laptop", topProducts.get(0).getKey());
        assertEquals(1500.00, topProducts.get(0).getValue(), 0.01);
        assertEquals("iPhone 13", topProducts.get(1).getKey());
        assertEquals(1200.00, topProducts.get(1).getValue(), 0.01);
    }

    @Test
    @DisplayName("Test average calculation using stream average")
    void testGetAverageOrderValue() {
        double avgOrderValue = analyzer.getAverageOrderValue();
        double expected = 4005.00 / 6;

        assertEquals(expected, avgOrderValue, 0.01,
                "Average order value should be total sales / number of orders");
    }

    @Test
    @DisplayName("Test DoubleSummaryStatistics for region analysis")
    void testGetRegionSalesStatistics() {
        Map<String, DoubleSummaryStatistics> regionStats = analyzer.getRegionSalesStatistics();

        assertEquals(4, regionStats.size(), "Should have 4 regions");

        DoubleSummaryStatistics eastStats = regionStats.get("East");
        assertNotNull(eastStats);
        assertEquals(1, eastStats.getCount());
        assertEquals(1200.00, eastStats.getSum(), 0.01);

        DoubleSummaryStatistics westStats = regionStats.get("West");
        assertEquals(2, westStats.getCount());
        assertEquals(480.00, westStats.getSum(), 0.01);
    }

    @Test
    @DisplayName("Test grouping by Month using temporal grouping")
    void testGetMonthlySalesTrend() {
        Map<Month, Double> monthlySales = analyzer.getMonthlySalesTrend();

        assertEquals(3, monthlySales.size(), "Should have sales in 3 months");
        assertEquals(2700.00, monthlySales.get(Month.JANUARY), 0.01);
        assertEquals(1250.00, monthlySales.get(Month.FEBRUARY), 0.01);
        assertEquals(55.00, monthlySales.get(Month.MARCH), 0.01);
    }

    @Test
    @DisplayName("Test filtering with predicates - unprofitable products")
    void testGetUnprofitableProducts() {
        List<SalesRecord> unprofitable = analyzer.getUnprofitableProducts();

        assertEquals(1, unprofitable.size(), "Should find 1 unprofitable product");
        assertEquals("Conference Table", unprofitable.get(0).getProductName());
        assertTrue(unprofitable.get(0).getProfit() < 0, "Profit should be negative");
    }

    @Test
    @DisplayName("Test average discount calculation with averagingDouble")
    void testGetAverageDiscountByCategory() {
        Map<String, Double> avgDiscount = analyzer.getAverageDiscountByCategory();

        assertEquals(3, avgDiscount.size());
        assertEquals(0.075, avgDiscount.get("Technology"), 0.001);
        assertEquals(0.075, avgDiscount.get("Furniture"), 0.001);
        assertEquals(0.10, avgDiscount.get("Office Supplies"), 0.001);
    }

    @Test
    @DisplayName("Test quantity aggregation using summingInt")
    void testGetQuantityBySubCategory() {
        Map<String, Integer> quantityBySubCategory = analyzer.getQuantityBySubCategory();

        assertEquals(6, quantityBySubCategory.size());
        assertEquals(2, quantityBySubCategory.get("Phones"));
        assertEquals(10, quantityBySubCategory.get("Binders"));
        assertEquals(20, quantityBySubCategory.get("Paper"));
    }

    @Test
    @DisplayName("Test filtering high-value orders with custom threshold")
    void testGetHighValueOrders() {
        List<SalesRecord> highValueOrders = analyzer.getHighValueOrders(500.0);

        assertEquals(3, highValueOrders.size(), "Should find 3 orders above $500");

        // Verify sorting (highest first)
        assertTrue(highValueOrders.get(0).getSales() >= highValueOrders.get(1).getSales());
        assertTrue(highValueOrders.get(1).getSales() >= highValueOrders.get(2).getSales());
    }

    @Test
    @DisplayName("Test profit margin calculation with map transformations")
    void testGetProfitMarginByCategory() {
        Map<String, Double> profitMargins = analyzer.getProfitMarginByCategory();

        assertEquals(3, profitMargins.size());
        assertEquals(20.0, profitMargins.get("Technology"), 0.1);
        assertEquals(6.8, profitMargins.get("Furniture"), 0.1);
        assertEquals(20.0, profitMargins.get("Office Supplies"), 0.1);
    }

    @Test
    @DisplayName("Test customer segment grouping")
    void testGetSalesBySegment() {
        Map<String, Double> salesBySegment = analyzer.getSalesBySegment();

        assertEquals(3, salesBySegment.size());
        assertEquals(2025.00, salesBySegment.get("Consumer"), 0.01);
        assertEquals(480.00, salesBySegment.get("Corporate"), 0.01);
        assertEquals(1500.00, salesBySegment.get("Home Office"), 0.01);
    }

    @Test
    @DisplayName("Test top states sorting and limiting")
    void testGetTopStatesBySales() {
        List<Map.Entry<String, Double>> topStates = analyzer.getTopStatesBySales(3);

        assertFalse(topStates.isEmpty());

        // Verify descending order
        for (int i = 0; i < topStates.size() - 1; i++) {
            assertTrue(topStates.get(i).getValue() >= topStates.get(i + 1).getValue(),
                    "States should be sorted by sales in descending order");
        }
    }

    @Test
    @DisplayName("Test shipping mode grouping")
    void testGetSalesByShipMode() {
        Map<String, Double> salesByShipMode = analyzer.getSalesByShipMode();

        assertEquals(4, salesByShipMode.size());
        assertTrue(salesByShipMode.containsKey("Standard Class"));
        assertTrue(salesByShipMode.containsKey("First Class"));
    }

    @Test
    @DisplayName("Test date range filtering with predicates")
    void testGetOrdersInDateRange() {
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 28);

        List<SalesRecord> ordersInRange = analyzer.getOrdersInDateRange(startDate, endDate);

        assertEquals(2, ordersInRange.size(), "Should find 2 orders in February");

        ordersInRange.forEach(record -> {
            assertFalse(record.getOrderDate().isBefore(startDate));
            assertFalse(record.getOrderDate().isAfter(endDate));
        });
    }

    @Test
    @DisplayName("Test category distribution percentage calculation")
    void testGetCategoryDistributionPercentage() {
        Map<String, Double> distribution = analyzer.getCategoryDistributionPercentage();

        assertEquals(3, distribution.size());

        // Sum of all percentages should be 100%
        double totalPercentage = distribution.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        assertEquals(100.0, totalPercentage, 0.1);
    }

    @Test
    @DisplayName("Test order count by region using counting collector")
    void testGetOrderCountByRegion() {
        Map<String, Long> orderCounts = analyzer.getOrderCountByRegion();

        assertEquals(4, orderCounts.size());
        assertEquals(1L, orderCounts.get("East"));
        assertEquals(2L, orderCounts.get("West"));
        assertEquals(2L, orderCounts.get("South"));
        assertEquals(1L, orderCounts.get("Central"));
    }

    @Test
    @DisplayName("Test empty dataset handling")
    void testEmptyDataset() {
        SalesAnalyzer emptyAnalyzer = new SalesAnalyzer(new ArrayList<>());

        assertEquals(0.0, emptyAnalyzer.calculateTotalRevenue());
        assertEquals(0.0, emptyAnalyzer.calculateTotalProfit());
        assertTrue(emptyAnalyzer.getSalesByCategory().isEmpty());
    }

    @Test
    @DisplayName("Test lambda expressions in stream pipeline")
    void testLambdaExpressions() {
        // Test various lambda expressions used in stream operations
        long technologyCount = testData.stream()
                .filter(record -> record.getCategory().equals("Technology"))
                .count();
        assertEquals(2, technologyCount);

        double maxSale = testData.stream()
                .mapToDouble(SalesRecord::getSales)
                .max()
                .orElse(0.0);
        assertEquals(1500.00, maxSale, 0.01);

        List<String> regions = testData.stream()
                .map(SalesRecord::getRegion)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        assertEquals(4, regions.size());
    }

    @Test
    @DisplayName("Test complex stream pipeline with multiple operations")
    void testComplexStreamPipeline() {
        // Complex pipeline: filter -> map -> group -> sort
        Map<String, Double> result = testData.stream()
                .filter(r -> r.getProfit() > 0)  // Filter profitable
                .collect(Collectors.groupingBy(
                        SalesRecord::getCategory,
                        Collectors.summingDouble(SalesRecord::getProfit)
                ));

        assertTrue(result.get("Technology") > 0);
        assertTrue(result.get("Office Supplies") > 0);
    }

    @Test
    @DisplayName("Test method reference usage")
    void testMethodReferences() {
        // Test that method references work correctly
        double totalSales = testData.stream()
                .mapToDouble(SalesRecord::getSales)  // Method reference
                .sum();
        assertTrue(totalSales > 0);

        List<String> productNames = testData.stream()
                .map(SalesRecord::getProductName)  // Method reference
                .collect(Collectors.toList());
        assertEquals(6, productNames.size());
    }
}