package com.wrteam.quiz.model;



public class LuckyItem {
    public String topText,no_ofQuestion,id,Level,no_ofCategory,plan,cateAmount;
    public String secondaryText;
    public int secondaryTextOrientation;
    public String icon;
    public int color;
    public boolean isPurchased;

    public String getTopText() {
        return topText;
    }

    public void setTopText(String topText) {
        this.topText = topText;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getCateAmount() {
        return cateAmount;
    }

    public void setCateAmount(String cateAmount) {
        this.cateAmount = cateAmount;
    }

    public String getNo_ofQuestion() {
        return no_ofQuestion;
    }

    public void setNo_ofQuestion(String no_ofQuestion) {
        this.no_ofQuestion = no_ofQuestion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public String getNo_ofCategory() {
        return no_ofCategory;
    }

    public void setNo_ofCategory(String no_ofCategory) {
        this.no_ofCategory = no_ofCategory;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }

    public int getSecondaryTextOrientation() {
        return secondaryTextOrientation;
    }

    public void setSecondaryTextOrientation(int secondaryTextOrientation) {
        this.secondaryTextOrientation = secondaryTextOrientation;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


}
