package com.example.budgetbuddy.backendLogic;

import org.json.JSONException;
import org.json.JSONObject;

public class Expense {
    private int id;
    private float amount;
    private String date;
    private String place;
    private String description;
    private String category;
    public Expense(JSONObject o){
        try{
            id = o.getInt("id");
            amount = (float) o.getDouble("amount");
            date = o.getString("date");
            place = o.getString("place");
            description = o.getString("description");
            category = o.getString("category");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId(){
        return id;
    }

    public float getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getPlace() {
        return place;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}
