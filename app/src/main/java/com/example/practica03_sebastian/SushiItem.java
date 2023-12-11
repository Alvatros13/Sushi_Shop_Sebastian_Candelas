package com.example.practica03_sebastian;

public class SushiItem {
    private String name;
    private String price;

    public SushiItem() {
    }

    public SushiItem(String name, String precio, String bits) {
        this.name = name;
        this.price = precio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String precio) {
        this.price = precio;
    }

}
