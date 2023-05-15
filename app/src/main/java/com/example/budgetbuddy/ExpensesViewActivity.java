package com.example.budgetbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.budgetbuddy.backendLogic.Expense;

import com.example.budgetbuddy.backendLogic.ExpenseAdapter;

import java.util.ArrayList;

public class ExpensesViewActivity extends AppCompatActivity {
    private ArrayList<Expense> expenses;
    private RecyclerView expenseList;
    private int userId;
    private float budget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_view);
        expenseList = findViewById(R.id.expenseOverviewQueueView);
        expenseList.setLayoutManager( new LinearLayoutManager(this));
        Intent intent = getIntent();
        expenses = intent.getParcelableArrayListExtra("expenses");
        userId = intent.getIntExtra("userId", 1);
        budget = intent.getFloatExtra("budget", 0);
        ExpenseAdapter adapter = new ExpenseAdapter(expenses);
        expenseList.setAdapter(adapter);
    }

    public void onAddExpenseButton_clicked(View caller){
        Intent intent = new Intent(this, AddExpenseActivity.class);
        intent.putParcelableArrayListExtra("expenses", expenses);
        intent.putExtra("userId", userId);
        intent.putExtra("budget", budget);
        startActivity(intent);
    }

    public void onRemoveExpenseButton_clicked(View caller) {
        Intent intent = new Intent(this, RemoveExpensesActivity.class);
        intent.putParcelableArrayListExtra("expenses", expenses);
        intent.putExtra("userId", userId);
        intent.putExtra("budget", budget);
        startActivity(intent);
    }

    public void onBackButton_clicked(View caller){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putParcelableArrayListExtra("expenses", expenses);
        intent.putExtra("userId", userId);
        intent.putExtra("budget", budget);
        startActivity(intent);
    }
}