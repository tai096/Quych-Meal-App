package com.example.quychmeal.Models;

public class FoodIngredient {
    private int ingredientId;
    private String quantity;

    public FoodIngredient() {
    }

    public FoodIngredient(int ingredientId, String quantity) {
        this.ingredientId = ingredientId;
        this.quantity = quantity;
    }

    public int getIngredientId() {
        return ingredientId;
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
