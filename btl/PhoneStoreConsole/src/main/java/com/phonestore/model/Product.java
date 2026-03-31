package com.phonestore.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Product {
    private int id;
    private int brandId;
    private String name;
    private BigDecimal price;
    private int stock;
    private String specs;
    private boolean isActive;
    private Timestamp createdAt;

    // Optional: for joining brand name
    private String brandName;

    public Product() {}

    public Product(int id, int brandId, String name, BigDecimal price, int stock, String specs, boolean isActive, Timestamp createdAt) {
        this.id = id;
        this.brandId = brandId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.specs = specs;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBrandId() { return brandId; }
    public void setBrandId(int brandId) { this.brandId = brandId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getSpecs() { return specs; }
    public void setSpecs(String specs) { this.specs = specs; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    
    @Override
    public String toString() {
        return String.format("[%d] %s (Hãng: %s) - %s VNĐ - Tồn kho: %d", id, name, brandName != null ? brandName : brandId, price.toString(), stock);
    }
}
