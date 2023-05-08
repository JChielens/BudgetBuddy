package com.example.budgetbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.budgetbuddy.backendLogic.Expense;
import com.example.budgetbuddy.backendLogic.ExpenseAdapter;
import com.example.budgetbuddy.backendLogic.RemoveExpenseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RemoveExpensesActivity extends AppCompatActivity {
    private ArrayList<Expense> expenses;
    private RecyclerView expenseList;
    private CheckBox checked;
    private static final String POST_URL = "https://studev.groept.be/api/a22pt403/deleteRow/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_expenses);
        expenseList = findViewById(R.id.expenseRemoveQueueView);
        expenseList.setLayoutManager( new LinearLayoutManager(this));
        expenses = getIntent().getParcelableArrayListExtra("expenses");
        RemoveExpenseAdapter adapter = new RemoveExpenseAdapter(expenses);
        expenseList.setAdapter(adapter);
    }

    public void onBtnRemove_Clicked(View caller){
        Iterator<Expense> it = expenses.iterator();
        while(it.hasNext()){
            Expense e = it.next();
            if(e.isChecked()){
                removeExpenseFromDatabase(e);
                it.remove();
            }
        }

        Intent intent = new Intent(this, ExpensesViewActivity.class);
        intent.putParcelableArrayListExtra("expenses", expenses);
        startActivity(intent);
    }

    private void removeExpenseFromDatabase(Expense e){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest(
                Request.Method.POST,
                POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                RemoveExpensesActivity.this,
                                "Unable to remove the expense" + error,
                                Toast.LENGTH_LONG).show();
                    }
                }
        )   { //NOTE THIS PART: here we are passing the POST parameters to the webservice
            @Override
            protected Map<String, String> getParams() {
                /* Map<String, String> with key value pairs as data load */
                Map<String, String> idMap = new HashMap<>();
                idMap.put("id", Integer.toString(e.getId()));
                return idMap;
            }
        };
        requestQueue.add(submitRequest);
    }
}