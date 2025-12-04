package com.dataanalysis;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.DoubleSummaryStatistics;

// Dataset: Kaggle Superstore Sales Dataset from https://www.kaggle.com/datasets/vivek468/superstore-dataset-final
public class SuperstoreAnalysisDemo {

    private static final String CSV_FILE_PATH = "data/Superstore.csv";

    public static void main(String[] args) {
        try {
            System.out.println("SUPERSTORE SALES DATA ANALYSIS");
            System.out.println("Dataset: Kaggle Superstore Sales Dataset");
            System.out.println();

            // Load data
            System.out.println("Loading data from: " + CSV_FILE_PATH);
            List<SalesRecord> salesData = SalesDataParser.parseCSV(CSV_FILE_PATH);
            System.out.println("Loaded " + salesData.size() + " sales records\n");

            // Create analyzer
            SalesAnalyzer analyzer = new SalesAnalyzer(salesData);

            // Perform various analyses
            performBasicAnalysis(analyzer);
            performCategoryAnalysis(analyzer);
            performRegionalAnalysis(analyzer);
            performProductAnalysis(analyzer);
            performProfitabilityAnalysis(analyzer);
            performTemporalAnalysis(analyzer);
            performSegmentAnalysis(analyzer);

            System.out.println("Analysis Complete!");

        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            System.err.println("Make sure 'Sample - Superstore.csv' is in the data/ folder");
        }
    }

    
    // Analysis 1: Basic Revenue and Profit Metrics
    
    private static void performBasicAnalysis(SalesAnalyzer analyzer) {
        System.out.println("ANALYSIS 1: Basic Revenue and Profit Metrics");

        double totalRevenue = analyzer.calculateTotalRevenue();
        double totalProfit = analyzer.calculateTotalProfit();
        double avgOrderValue = analyzer.getAverageOrderValue();
        double profitMargin = (totalProfit / totalRevenue) * 100;

        System.out.printf("Total Revenue:        $%,.2f%n", totalRevenue);
        System.out.printf("Total Profit:         $%,.2f%n", totalProfit);
        System.out.printf("Average Order Value:  $%,.2f%n", avgOrderValue);
        System.out.printf("Profit Margin:        %.2f%%%n", profitMargin);
    }

    
    // Analysis 2: Category Performance
    
    private static void performCategoryAnalysis(SalesAnalyzer analyzer) {
        System.out.println("ANALYSIS 2: Sales and Profit by Category");

        Map<String, Double> salesByCategory = analyzer.getSalesByCategory();
        Map<String, Double> profitByCategory = analyzer.getProfitByCategory();
        Map<String, Double> profitMargins = analyzer.getProfitMarginByCategory();
        Map<String, Double> categoryDistribution = analyzer.getCategoryDistributionPercentage();

        System.out.println("\nCategory Breakdown:");
        System.out.printf("%-20s %15s %15s %15s %12s%n",
                "Category", "Sales", "Profit", "Margin %", "Share %");

        salesByCategory.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    String category = entry.getKey();
                    double sales = entry.getValue();
                    double profit = profitByCategory.get(category);
                    double margin = profitMargins.get(category);
                    double share = categoryDistribution.get(category);

                    System.out.printf("%-20s $%,13.2f $%,13.2f %14.2f%% %11.2f%%%n",
                            category, sales, profit, margin, share);
                });
    }

    
    // Analysis 3: Regional Performance
    
    private static void performRegionalAnalysis(SalesAnalyzer analyzer) {
        System.out.println("ANALYSIS 3: Regional Performance");

        Map<String, DoubleSummaryStatistics> regionStats = analyzer.getRegionSalesStatistics();
        Map<String, Long> orderCountByRegion = analyzer.getOrderCountByRegion();

        System.out.println("\nRegional Statistics:");
        System.out.printf("%-15s %12s %15s %15s %12s%n",
                "Region", "Orders", "Total Sales", "Avg Sale", "Max Sale");

        regionStats.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(
                        e2.getValue().getSum(),
                        e1.getValue().getSum()))
                .forEach(entry -> {
                    String region = entry.getKey();
                    DoubleSummaryStatistics stats = entry.getValue();
                    long orderCount = orderCountByRegion.get(region);

                    System.out.printf("%-15s %,12d $%,13.2f $%,13.2f $%,10.2f%n",
                            region, orderCount, stats.getSum(),
                            stats.getAverage(), stats.getMax());
                });

        // Top 5 states by sales
        System.out.println("\nTop 5 States by Sales:");
        analyzer.getTopStatesBySales(5).forEach(entry ->
                System.out.printf("%-30s $%,15.2f%n", entry.getKey(), entry.getValue()));
    }

    
    // Analysis 4: Top Products
    
    private static void performProductAnalysis(SalesAnalyzer analyzer) {
        System.out.println("ANALYSIS 4: Top Products");

        System.out.println("\nTop 10 Products by Sales:");
        analyzer.getTopProductsBySales(10).forEach(entry ->
                System.out.printf("%-50s $%,15.2f%n",
                        truncate(entry.getKey(), 50), entry.getValue()));

        System.out.println("\nTop 10 Products by Profit:");
        analyzer.getTopProductsByProfit(10).forEach(entry ->
                System.out.printf("%-50s $%,15.2f%n",
                        truncate(entry.getKey(), 50), entry.getValue()));

        // Sub-category analysis
        System.out.println("\nQuantity Sold by Sub-Category (Top 10):");
        analyzer.getQuantityBySubCategory().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(entry ->
                        System.out.printf("%-30s %,10d units%n", entry.getKey(), entry.getValue()));
    }

    
    // Analysis 5: Profitability Analysis
    
    private static void performProfitabilityAnalysis(SalesAnalyzer analyzer) {
        System.out.println("ANALYSIS 5: Profitability Analysis");

        List<SalesRecord> unprofitableProducts = analyzer.getUnprofitableProducts();

        System.out.println("\nProducts with Losses (Negative Profit):");
        System.out.println("Total Unprofitable Transactions: " + unprofitableProducts.size());
        System.out.printf("%-40s %-15s %15s %15s%n",
                "Product", "Category", "Sales", "Loss");

        unprofitableProducts.stream()
                .limit(10)
                .forEach(record ->
                        System.out.printf("%-40s %-15s $%,13.2f -$%,12.2f%n",
                                truncate(record.getProductName(), 40),
                                record.getCategory(),
                                record.getSales(),
                                Math.abs(record.getProfit())));

        double totalLoss = unprofitableProducts.stream()
                .mapToDouble(SalesRecord::getProfit)
                .sum();
        System.out.printf("%nTotal Loss from Unprofitable Products: $%,.2f%n", Math.abs(totalLoss));

        // High-value orders
        System.out.println("\nHigh-Value Orders (> $10,000):");
        List<SalesRecord> highValueOrders = analyzer.getHighValueOrders(10000);
        System.out.println("Count: " + highValueOrders.size());
        highValueOrders.stream()
                .limit(5)
                .forEach(record ->
                        System.out.printf("%-40s $%,15.2f (Profit: $%,.2f)%n",
                                truncate(record.getProductName(), 40),
                                record.getSales(),
                                record.getProfit()));
    }

    
    // Analysis 6: Temporal Trends
    
    private static void performTemporalAnalysis(SalesAnalyzer analyzer) {
        System.out.println("ANALYSIS 6: Temporal Trends");

        Map<Month, Double> monthlySales = analyzer.getMonthlySalesTrend();

        System.out.println("\nMonthly Sales Trend:");
        System.out.printf("%-15s %20s%n", "Month", "Total Sales");

        monthlySales.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        System.out.printf("%-15s $%,18.2f%n",
                                entry.getKey(), entry.getValue()));

        // Find peak month
        Map.Entry<Month, Double> peakMonth = monthlySales.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (peakMonth != null) {
            System.out.printf("%nPeak Sales Month: %s ($%,.2f)%n",
                    peakMonth.getKey(), peakMonth.getValue());
        }
    }

    
    // Analysis 7: Customer Segment & Shipping Analysis
    
    private static void performSegmentAnalysis(SalesAnalyzer analyzer) {
        System.out.println("ANALYSIS 7: Customer Segment & Shipping Analysis");

        Map<String, Double> salesBySegment = analyzer.getSalesBySegment();
        Map<String, Double> salesByShipMode = analyzer.getSalesByShipMode();
        Map<String, Double> avgDiscountByCategory = analyzer.getAverageDiscountByCategory();

        System.out.println("\nSales by Customer Segment:");
        salesBySegment.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry ->
                        System.out.printf("%-20s $%,15.2f%n", entry.getKey(), entry.getValue()));

        System.out.println("\nSales by Shipping Mode:");
        salesByShipMode.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry ->
                        System.out.printf("%-20s $%,15.2f%n", entry.getKey(), entry.getValue()));

        System.out.println("\nAverage Discount by Category:");
        avgDiscountByCategory.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry ->
                        System.out.printf("%-20s %.2f%%%n", entry.getKey(), entry.getValue() * 100));
    }

    
    // Helper method to truncate long strings
    private static String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}