package com.phonestore.model;

import java.math.BigDecimal;
import java.util.List;

public class Dashboard {
    private BigDecimal totalRevenue;
    private int completedOrders;
    private int pendingOrders;
    private List<String> topSellingProducts;

    public Dashboard(BigDecimal totalRevenue, int completedOrders, int pendingOrders, List<String> topSellingProducts) {
        this.totalRevenue = totalRevenue;
        this.completedOrders = completedOrders;
        this.pendingOrders = pendingOrders;
        this.topSellingProducts = topSellingProducts;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(int completedOrders) {
        this.completedOrders = completedOrders;
    }

    public int getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(int pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public List<String> getTopSellingProducts() {
        return topSellingProducts;
    }

    public void setTopSellingProducts(List<String> topSellingProducts) {
        this.topSellingProducts = topSellingProducts;
    }
}
