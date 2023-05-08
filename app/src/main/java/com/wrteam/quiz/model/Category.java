package com.wrteam.quiz.model;

import java.util.ArrayList;

public class Category {
    private String id, name, image, maxLevel, noOfCate, message, date, ttlQues,video_id,plan,cateAmount;
    public ArrayList<SubCategory> subCategoryList;
    public boolean adsShow,isPurchased;

    public String getVideo_id() {
        return video_id;
    }

    public String getCateAmount() {
        return cateAmount;
    }

    public void setCateAmount(String cateAmount) {
        this.cateAmount = cateAmount;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public Category() {
    }

    public Category(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public Category(boolean adsShow) {
        this.adsShow = adsShow;
    }

    public boolean isAdsShow() {
        return adsShow;
    }

    public void setAdsShow(boolean adsShow) {
        this.adsShow = adsShow;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getImage() {
        return image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNoOfCate() {
        return noOfCate;
    }

    public void setNoOfCate(String noOfCate) {
        this.noOfCate = noOfCate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(String maxLevel) {
        this.maxLevel = maxLevel;
    }

    public String getTtlQues() {
        return ttlQues;
    }

    public void setTtlQues(String ttlQues) {
        this.ttlQues = ttlQues;
    }

    public ArrayList<SubCategory> getSubCategoryList() {
        return subCategoryList;
    }

    public void setSubCategoryList(ArrayList<SubCategory> subCategoryList) {
        this.subCategoryList = subCategoryList;
    }
}
