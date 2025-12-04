package com.dataanalysis;

import java.time.LocalDate;
import java.util.Objects;

// Represents a sales record DTO from Superstore dataset
public class SalesRecord {
    private final String orderId;
    private final LocalDate orderDate;
    private final String shipMode;
    private final String segment;
    private final String country;
    private final String city;
    private final String state;
    private final String region;
    private final String category;
    private final String subCategory;
    private final String productName;
    private final double sales;
    private final int quantity;
    private final double discount;
    private final double profit;

    public SalesRecord(String orderId, LocalDate orderDate, String shipMode,
                       String segment, String country, String city, String state,
                       String region, String category, String subCategory,
                       String productName, double sales, int quantity,
                       double discount, double profit) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.shipMode = shipMode;
        this.segment = segment;
        this.country = country;
        this.city = city;
        this.state = state;
        this.region = region;
        this.category = category;
        this.subCategory = subCategory;
        this.productName = productName;
        this.sales = sales;
        this.quantity = quantity;
        this.discount = discount;
        this.profit = profit;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public LocalDate getOrderDate() { return orderDate; }
    public String getShipMode() { return shipMode; }
    public String getSegment() { return segment; }
    public String getCountry() { return country; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getRegion() { return region; }
    public String getCategory() { return category; }
    public String getSubCategory() { return subCategory; }
    public String getProductName() { return productName; }
    public double getSales() { return sales; }
    public int getQuantity() { return quantity; }
    public double getDiscount() { return discount; }
    public double getProfit() { return profit; }

    @Override
    public String toString() {
        return String.format("SalesRecord{orderId='%s', date=%s, category='%s', " +
                        "product='%s', sales=%.2f, quantity=%d, profit=%.2f, region='%s'}",
                orderId, orderDate, category, productName, sales, quantity, profit, region);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalesRecord that = (SalesRecord) o;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}