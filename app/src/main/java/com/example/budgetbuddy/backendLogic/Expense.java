package com.example.budgetbuddy.backendLogic;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Expense implements Parcelable {
    private int expenseId;
    private int userId;
    private float amount;
    private String date;
    private String place;
    private String description;
    private String category;
    private boolean checked;

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

    public Expense(int expenseId, int userId, float amount, String date, String place, String description, String category) {
        this.expenseId = expenseId;
        this.userId = userId;
        this.amount = amount;
        this.date = date;
        this.place = place;
        this.description = description;
        this.category = category;
        checked = false;
    }

    public Expense(JSONObject o){
        try{
            expenseId = o.getInt("expenseId");
            userId = o.getInt("userId");
            amount = (float) o.getDouble("amount");
            date = o.getString("date");
            place = o.getString("place");
            description = o.getString("description");
            category = o.getString("category");
            checked = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Expense(Parcel in){
        this(
                in.readInt(),
                in.readInt(),
                in.readFloat(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString()
        );
        checked = false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(expenseId);
        parcel.writeInt(userId);
        parcel.writeFloat(amount);
        parcel.writeString(date);
        parcel.writeString(place);
        parcel.writeString(description);
        parcel.writeString(category);
    }

    public Map<String, String> getPostParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("userid", Integer.toString(userId));
        params.put("expenseid", Integer.toString(expenseId));
        params.put("date", date);
        params.put("category", category);
        params.put("place", place);
        params.put("amount", Float.toString(amount));
        params.put("description", description);
        return params;
    }

    public void addId(int id){
        this.expenseId = id;
    }

    public int getExpenseId(){
        return expenseId;
    }

    public int getUserId() {
        return userId;
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

    public void setChecked(){
        checked = true;
    }

    public void setUnchecked(){
        checked = false;
    }

    public boolean isChecked(){
        return checked;
    }
}
