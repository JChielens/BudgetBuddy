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
    private ArrayList<Expense> expenses;
    private String QUEUE_URL = "https://studev.groept.be/api/a22pt403/getAll";
    private RecyclerView expenseList;
    private Button addExpenseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_view);
        expenseList = findViewById(R.id.orderQueueView);
        addExpenseButton = (Button) findViewById(R.id.addExpenseButton);
        expenseList.setLayoutManager( new LinearLayoutManager(this));
        expenses = getIntent().getParcelableArrayListExtra("expenses");
        ExpenseAdapter adapter = new ExpenseAdapter(expenses);
        expenseList.setAdapter(adapter);
    }

    public void onAddExpenseButton_clicked(View caller){
        Intent intent = new Intent(this, AddExpenseActivity.class);
        startActivity(intent);
    }

    public void onBackButton_clicked(View caller){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putParcelableArrayListExtra("expenses", expenses);
        startActivity(intent);
    }
}