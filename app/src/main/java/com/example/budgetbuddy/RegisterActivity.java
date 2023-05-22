package com.example.budgetbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText userField;
    private TextInputLayout userFieldLayout;
    private EditText passField;
    private TextInputLayout passFieldLayout;
    private EditText emailField;
    private TextInputLayout emailFieldLayout;
    private static final String POST_URL = "https://studev.groept.be/api/a22pt403/getUsernameIfExists/";
    private static final String POST_REGISTER_URL = "https://studev.groept.be/api/a22pt403/registerUser/";
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userField = (EditText) findViewById(R.id.inputUsernameFieldRegister);
        passField = (EditText) findViewById(R.id.inputPasswordFieldRegister);
        emailField = (EditText) findViewById((R.id.inputEmailField));
        userFieldLayout = findViewById(R.id.usernameFieldRegister);
        passFieldLayout = findViewById(R.id.passwordFieldRegister);
        emailFieldLayout = findViewById((R.id.emailField));
        setupTextChangedListeners();

    }

    private boolean isFieldEmpty(TextView textView) {
        return textView.getText().toString().trim().isEmpty();
    }

    private void setupTextChangedListeners() {
        userField.addTextChangedListener(getTextWatcher(userFieldLayout));
        passField.addTextChangedListener(getTextWatcher(passFieldLayout));
        emailField.addTextChangedListener(getTextWatcher(emailFieldLayout));
    }

    private TextWatcher getTextWatcher(TextInputLayout textInputLayout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        };
    }

    public void onRegisterButton_Clicked(View caller){
        boolean userFieldEmpty = isFieldEmpty(userField);
        boolean passFieldEmpty = isFieldEmpty(passField);
        boolean emailFieldEmpty = isFieldEmpty(emailField);
        if(!emailFieldEmpty && !userFieldEmpty && !passFieldEmpty){
            String email = emailField.getText().toString().trim();
            String username = userField.getText().toString().trim();
            //String regex = "^[a-zA-Z0-9_!#$%&'*+\\=?`{|}~^.-]+@[a-zA-Z0-9]+[.][a-zA-Z0-9]{2,4}$";
            String new_regex = "^[a-zA-Z0-9_!#$%&'*+\\=?`{|}~^.-]{1,64}@[a-zA-Z0-9-]{1,200}([.][a-zA-Z0-9-]{1,52})?([.][a-zA-Z0-9-]{2,4})?[.][a-zA-Z0-9]{2,4}$";
            //if(email.matches(regex)){
            if(email.matches(new_regex)){
                checkIfUsernameExists(username);
                //registerUser();
            }
            else{
                emailFieldLayout.setError("Invalid email address");
            }
        }
        else{
            if (userFieldEmpty){userFieldLayout.setError("Username cannot be empty");}
            if (passFieldEmpty){passFieldLayout.setError("Password cannot be empty");}
            if (emailFieldEmpty){emailFieldLayout.setError("Email cannot be empty");}
        }
    }

    private void checkIfUsernameExists(String username){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                POST_URL + username,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response.length() == 0){
                            registerUser();
                        }
                        else{
                            userFieldLayout.setError("Username already used");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                RegisterActivity.this,
                                "Unable to communicate with the server",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(queueRequest);
    }

    private void registerUser(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest(
                Request.Method.POST,
                POST_REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        toLogin();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                RegisterActivity.this,
                                "Unable to communicate with the server" + error,
                                Toast.LENGTH_LONG).show();
                    }
                }
        )   { //NOTE THIS PART: here we are passing the POST parameters to the webservice
            @Override
            protected Map<String, String> getParams() {
                /* Map<String, String> with key value pairs as data load */
                Map<String, String> registerMap = new HashMap<>();
                registerMap.put("username", userField.getText().toString().trim());
                registerMap.put("password", hashPassword(passField.getText().toString().trim()));
                registerMap.put("email", emailField.getText().toString().trim());
                return registerMap;
            }
        };
        requestQueue.add(submitRequest);
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

    public void toLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void onLoginButton_Clicked(View caller) {
        toLogin();
    }
}