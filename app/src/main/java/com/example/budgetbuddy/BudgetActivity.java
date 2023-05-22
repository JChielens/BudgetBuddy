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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.budgetbuddy.backendLogic.Expense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BudgetActivity extends AppCompatActivity {
    private static final String POST_BUDGET_URL = "https://studev.groept.be/api/a22pt403/insertUpdateBudget/";
    private ArrayList<Expense> expenses;
    private int userId;
    private float budget;
    private EditText budgetField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId",1);
        expenses = intent.getParcelableArrayListExtra("expenses");
        budget = intent.getFloatExtra("budget",0);

        budgetField = (EditText) findViewById(R.id.budgetField);
    }

    public void onSubmitBudgetButton_Clicked(View caller) {
        if(!budgetField.getText().toString().trim().isEmpty()){
            budget = Float.parseFloat(budgetField.getText().toString().trim());
            postBudgetToDatabase();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("budget", budget);
            intent.putParcelableArrayListExtra("expenses", expenses);
            startActivity(intent);
        }
        else{
            Toast.makeText(
                    BudgetActivity.this,
                    "Budget empty",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void onBackButton_clicked(View caller){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("budget", budget);
        intent.putParcelableArrayListExtra("expenses", expenses);
        startActivity(intent);
    }

    private void postBudgetToDatabase(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest(
                Request.Method.POST,
                POST_BUDGET_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                BudgetActivity.this,
                                "Unable to communicate with the server" + error,
                                Toast.LENGTH_LONG).show();
                    }
                }
        )   { //NOTE THIS PART: here we are passing the POST parameters to the webservice
            @Override
            protected Map<String, String> getParams() {
                /* Map<String, String> with key value pairs as data load */
                Map<String, String> budgetMap = new HashMap<>();
                budgetMap.put("userid", Integer.toString(userId));
                budgetMap.put("month", Integer.toString(Calendar.getInstance().get(Calendar.MONTH)+1));
                budgetMap.put("year", Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
                budgetMap.put("budget", Float.toString(budget));
                return budgetMap;
            }
        };
        requestQueue.add(submitRequest);
    }
}