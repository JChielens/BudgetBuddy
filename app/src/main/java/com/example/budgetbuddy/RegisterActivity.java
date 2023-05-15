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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
    private EditText passField;
    private EditText emailField;
    private static final String POST_URL = "https://studev.groept.be/api/a22pt403/getUsernameIfExists/";
    private static final String POST_REGISTER_URL = "https://studev.groept.be/api/a22pt403/registerUser/";
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userField = (EditText) findViewById(R.id.usernameField);
        passField = (EditText) findViewById(R.id.passwordField1);
        emailField = (EditText) findViewById((R.id.emailField));
    }

    public void onRegisterButton_Clicked(View caller){
        // TODO: register user
        String email = emailField.getText().toString();
        String username = userField.getText().toString();
        String regex = "^[a-zA-Z0-9_!#$%&'*+\\=?`{|}~^.-]+@[a-zA-Z0-9]+[.][a-zA-Z0-9]{2,4}$";
        if(email.matches(regex)){
            checkIfUsernameExists(username);
            //registerUser();
        }
        else{
            Toast.makeText(RegisterActivity.this,
                    "Invalid email address",
                    Toast.LENGTH_LONG).show();
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
                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Username already used",
                                    Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}