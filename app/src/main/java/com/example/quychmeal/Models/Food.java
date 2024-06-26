package com.example.quychmeal.Models;
import java.io.Serializable;
import java.util.List;

public class Food implements Serializable{
    private int id;
    private int categoryId;
    private int cookTime;
    private String createdBy;
    private String description;
    private String image;
    private List<FoodIngredient> ingredients;
    private int level;
    private String method;
    private String name;
    private int prepTime;
    private int serving;
    private String video;

    public Food() {
    }
    public Food(int id, int categoryId, int cookTime, String createdBy, String description, String image, List<FoodIngredient> ingredients, int level, String method, String name, int prepTime, int serving, String video) {
        this.id = id;
        this.categoryId = categoryId;
        this.cookTime = cookTime;
        this.createdBy = createdBy;
        this.description = description;
        this.image = image;
        this.ingredients = ingredients;
        this.level = level;
        this.method = method;
        this.name = name;
        this.prepTime = prepTime;
        this.serving = serving;
        this.video = video;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<FoodIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<FoodIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public int getServing() {
        return serving;
    }

    public void setServing(int serving) {
        this.serving = serving;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}

