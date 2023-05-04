package com.example.budgetbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.budgetbuddy.backendLogic.Expense;
import com.example.budgetbuddy.backendLogic.ExpenseAdapter;
import com.example.budgetbuddy.backendLogic.RemoveExpenseAdapter;

import java.util.ArrayList;

public class RemoveExpensesActivity extends AppCompatActivity {
    private ArrayList<Expense> expenses;
    private RecyclerView expenseList;

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
}