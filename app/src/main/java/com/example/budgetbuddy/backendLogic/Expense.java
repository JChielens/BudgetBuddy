package com.example.budgetbuddy.backendLogic;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Expense implements Parcelable {
    private int id;
    private float amount;
    private String date;
    private String place;
    private String description;
    private String category;

    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

    public Expense(int id, float amount, String date, String place, String description, String category) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.place = place;
        this.description = description;
        this.category = category;
    }

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

    public Expense(Parcel in){
        this(
                in.readInt(),
                in.readFloat(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString()
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeFloat(amount);
        parcel.writeString(date);
        parcel.writeString(place);
        parcel.writeString(description);
        parcel.writeString(category);
    }

    public Map<String, String> getPostParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("date", date);
        params.put("category", category);
        params.put("place", place);
        params.put("amount", Float.toString(amount));
        params.put("description", description);
        return params;
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
