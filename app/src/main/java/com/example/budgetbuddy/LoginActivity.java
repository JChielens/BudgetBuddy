package com.example.budgetbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.budgetbuddy.backendLogic.Expense;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private EditText userField;
    private EditText passField;
    private ArrayList<Expense> expenses;
    private float budget;
    private static final String POST_URL = "https://studev.groept.be/api/a22pt403/getHashedPasswordFromUsername/";
    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt403/getAllExpensesFromUser/";
    private static final String GET_BUDGET_URL = "https://studev.groept.be/api/a22pt403/getBudget/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userField = (EditText) findViewById(R.id.usernameField);
        passField = (EditText) findViewById(R.id.passwordField1);
        expenses = new ArrayList<>();
    }

    public void onRegisterButton_Clicked(View caller){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void onLoginButton_Clicked(View caller){
        String hash = hashPassword(passField.getText().toString().trim());
        checkIfPasswordCorrect(hash);
    }

    private String hashPassword(String password){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] result = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(result);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private void checkIfPasswordCorrect(String hash){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                POST_URL + userField.getText().toString().trim(),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response.length() == 1){
                            try {
                                JSONObject user = response.getJSONObject(0);
                                String hashDatabase = user.getString("password");
                                if(hashDatabase.equals(hash)){
                                    getExpenses(user.getInt("id"));
                                }
                                else {
                                    Toast.makeText(
                                            LoginActivity.this,
                                            "Password incorrect",
                                            Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                LoginActivity.this,
                                "Unable to communicate with the server",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(queueRequest);
    }

    private void goToMain(int userId){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("budget", budget);
        intent.putParcelableArrayListExtra("expenses", expenses);
        startActivity(intent);
    }

    private void getBudget(int userId){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                GET_BUDGET_URL + userId,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            budget = (float) response.getJSONObject(0).getDouble("budget");
                            goToMain(userId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                LoginActivity.this,
                                "Unable to communicate with the server",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(queueRequest);
    }

    private void getExpenses(int userId){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                QUEUE_URL + userId,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processJSONResponse(response);
                        getBudget(userId);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                LoginActivity.this,
                                "Unable to communicate with the server",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(queueRequest);
    }

    private void processJSONResponse(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                Expense expense = new Expense(response.getJSONObject(i));
                expenses.add(expense);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}