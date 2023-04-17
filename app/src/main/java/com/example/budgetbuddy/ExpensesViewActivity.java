package com.example.budgetbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.budgetbuddy.backendLogic.Expense;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.budgetbuddy.backendLogic.ExpenseAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ExpensesViewActivity extends AppCompatActivity {
    private List<Expense> expenses = new ArrayList<>();
    private String QUEUE_URL = "https://studev.groept.be/api/a22pt403/getAll";
    private RecyclerView expenseList;
    private Button addExpenseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_view);
        expenseList = findViewById(R.id.orderQueueView);
        addExpenseButton = (Button) findViewById(R.id.addExpenseButton);
        ExpenseAdapter adapter = new ExpenseAdapter(expenses);
        expenseList.setAdapter(adapter);
        expenseList.setLayoutManager( new LinearLayoutManager(this));
        requestExpenseListQueue();
    }

    private void requestExpenseListQueue(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                QUEUE_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processJSONResponse(response);
                        expenseList.getAdapter().notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                ExpensesViewActivity.this,
                                "Unable to communicate with the server",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(queueRequest);
    }

    private void processJSONResponse(JSONArray response){
        for(int i = 0; i < response.length(); i ++){
            try {
                Expense expense = new Expense(response.getJSONObject(i));
                expenses.add(expense);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void onAddExpenseButton_clicked(View caller){
        Intent intent = new Intent(this, AddExpenseActivity.class);
        startActivity(intent);
    }
}