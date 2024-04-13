package com.example.quychmeal.Models;

public class FoodIngredient {
    private Integer ingredientId;
    private String name;
    private String quantity;

    public FoodIngredient() {
    }

    public FoodIngredient(Integer ingredientId, String quantity, String name) {
        this.ingredientId = ingredientId;
        this.quantity = quantity;
        this.name = name;
    }

    public Integer getIngredientId() {
        return ingredientId;
    }
    public String getName() {
        return name;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
