package com.dataanalysis;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

// Performs data analysis on Superstore sales data using Java Streams API
public class SalesAnalyzer {
    private final List<SalesRecord> salesData;

    public SalesAnalyzer(List<SalesRecord> salesData) {
        this.salesData = salesData;
    }

    // 1. Calculating total sales revenue
    public double calculateTotalRevenue() {
        return salesData.stream()
                .mapToDouble(SalesRecord::getSales)
                .sum();
    }

    
    // 2. Calculating total profit
    public double calculateTotalProfit() {
        return salesData.stream()
                .mapToDouble(SalesRecord::getProfit)
                .sum();
    }

    
    // 3. Group sales by category
    public Map<String, Double> getSalesByCategory() {
        return salesData.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getCategory,
                        Collectors.summingDouble(SalesRecord::getSales)
                ));
    }

    
    // 4. Group profit by category
    public Map<String, Double> getProfitByCategory() {
        return salesData.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getCategory,
                        Collectors.summingDouble(SalesRecord::getProfit)
                ));
    }

    
    // 5. Finding top N products by sales
    public List<Map.Entry<String, Double>> getTopProductsBySales(int n) {
        return salesData.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getProductName,
                        Collectors.summingDouble(SalesRecord::getSales)
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    
    // 6. Finding top N most profitable products
    public List<Map.Entry<String, Double>> getTopProductsByProfit(int n) {
        return salesData.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getProductName,
                        Collectors.summingDouble(SalesRecord::getProfit)
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    
    // 7. Calculating average order value
    public double getAverageOrderValue() {
        return salesData.stream()
                .mapToDouble(SalesRecord::getSales)
                .average()
                .orElse(0.0);
    }

    
    // 8. Sales statistics by region
    public Map<String, DoubleSummaryStatistics> getRegionSalesStatistics() {
        return salesData.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getRegion,
                        Collectors.summarizingDouble(SalesRecord::getSales)
                ));
    }

    
    // 9. Monthly sales trend
    public Map<Month, Double> getMonthlySalesTrend() {
        return salesData.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getOrderDate().getMonth(),
                        Collectors.summingDouble(SalesRecord::getSales)
                ));
    }

    
    // 10. Sales by customer segment
    public Map<String, Double> getSalesBySegment() {
        return salesData.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getSegment,
                        Collectors.summingDouble(SalesRecord::getSales)
                ));
    }

    
    // 11. Finding products with negative profit (losses)
    public List<SalesRecord> getUnprofitableProducts() {
        return salesData.stream()
                .filter(record -> record.getProfit() < 0)
                .sorted(Comparator.comparingDouble(SalesRecord::getProfit))
                .collect(Collectors.toList());
    }

    
    // 12. Calculating discount analysis
    public Map<String, Double> getAverageDiscountByCategory() {
        return salesData.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getCategory,
                        Collectors.averagingDouble(SalesRecord::getDiscount)
                ));
    }

    
    // 13. Total quantity sold by sub-category
    public Map<String, Integer> getQuantityBySubCategory() {
        return salesData.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getSubCategory,
                        Collectors.summingInt(SalesRecord::getQuantity)
                ));
    }

    
    // 14. Finding high-value orders above a threshold
    public List<SalesRecord> getHighValueOrders(double threshold) {
        return salesData.stream()
                .filter(record -> record.getSales() > threshold)
                .sorted(Comparator.comparingDouble(SalesRecord::getSales).reversed())
                .collect(Collectors.toList());
    }

    
    // 15. Profit margin by category (profit/sales %)
    public Map<String, Double> getProfitMarginByCategory() {
        Map<String, Double> salesByCategory = getSalesByCategory();
        Map<String, Double> profitByCategory = getProfitByCategory();

        return salesByCategory.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (profitByCategory.get(entry.getKey()) / entry.getValue())// 100
                ));
    }

    
    // 16. Top N states by sales
    public List<Map.Entry<String, Double>> getTopStatesBySales(int n) {
        return salesData.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getState,
                        Collectors.summingDouble(SalesRecord::getSales)
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    
    // 17. Sales by ship mode
    public Map<String, Double> getSalesByShipMode() {
        return salesData.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getShipMode,
                        Collectors.summingDouble(SalesRecord::getSales)
                ));
    }

    
    // 18. Finding orders in date range
    public List<SalesRecord> getOrdersInDateRange(LocalDate startDate, LocalDate endDate) {
        return salesData.stream()
                .filter(record -> !record.getOrderDate().isBefore(startDate) &&
                        !record.getOrderDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    
    // 19. Category distribution percentage
    public Map<String, Double> getCategoryDistributionPercentage() {
        double totalSales = calculateTotalRevenue();

        return salesData.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getCategory,
                        Collectors.summingDouble(SalesRecord::getSales)
                ))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (entry.getValue() / totalSales)// 100
                ));
    }

    
    // 20. Counting orders by region
    public Map<String, Long> getOrderCountByRegion() {
        return salesData.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getRegion,
                        Collectors.counting()
                ));
    }
}