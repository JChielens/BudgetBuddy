package com.example.budgetbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.budgetbuddy.backendLogic.Expense;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private static final String LOGIN_URL = "https://studev.groept.be/api/a22pt403/getUserInfo/";
    private static final String EXPENSES_URL = "https://studev.groept.be/api/a22pt403/getAllExpensesFromUser/";
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private TextInputEditText userField;
    private TextInputEditText passField;
    private ArrayList<Expense> expenses;
    private int userId;
    private float budget;
    private TextInputLayout userFieldLayout;
    private TextInputLayout passFieldLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userField = (TextInputEditText) findViewById(R.id.inputUsernameField);
        passField = (TextInputEditText) findViewById(R.id.inputPasswordField);
        expenses = new ArrayList<>();

        userFieldLayout = findViewById(R.id.usernameField);
        passFieldLayout = findViewById(R.id.passwordField);
        setupTextChangedListeners();
    }

    private void setupTextChangedListeners() {
        userField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userFieldLayout.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        passField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passFieldLayout.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });
    }

    public void onRegisterButton_Clicked(View caller){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void onLoginButton_Clicked(View caller){
        boolean userFieldEmpty = userField.getText().toString().trim().isEmpty();
        boolean passFieldEmpty = passField.getText().toString().trim().isEmpty();
        if(!userFieldEmpty && !passFieldEmpty){
            String hash = hashPassword(passField.getText().toString().trim());
            loginUser(hash);
        }
        else{
            if (userFieldEmpty) {userFieldLayout.setError("Username cannot be empty");}
            if (passFieldEmpty) {passFieldLayout.setError("Password cannot be empty");}
        }
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

    private void loginUser(String hashedPassword){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                LOGIN_URL + LocalDate.now().getMonthValue() + "/" + LocalDate.now().getYear() + "/" + userField.getText().toString().trim(),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response.length() == 1){
                            try {
                                JSONObject user = response.getJSONObject(0);
                                checkLogin(user, hashedPassword);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            userFieldLayout.setError("Username unknown");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                LoginActivity.this,
                                "Unable to communicate with the server check",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(queueRequest);
    }

    private void checkLogin(JSONObject user, String hashedPassword){
        try {
            String hashDatabase = user.getString("password");
            if(hashDatabase.equals(hashedPassword) &&
                    user.getString("username").equals(userField.getText().toString().trim())){
                budget = (float) ((user.getString("budget").equals("null")) ? 0 : user.getDouble("budget"));
                userId = user.getInt("id");
                getExpenses();
            }
            else {
                passFieldLayout.setError("Password incorrect");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void goToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("budget", budget);
        intent.putParcelableArrayListExtra("expenses", expenses);
        startActivity(intent);
    }

    private void getExpenses(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                EXPENSES_URL + userId,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processJSONResponse(response);
                        goToMain();
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