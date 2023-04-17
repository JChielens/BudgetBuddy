package com.example.budgetbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class ExpensesViewActivity extends AppCompatActivity {
    private RecyclerView expenseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_view);
        expenseList = findViewById(R.id.orderQueueView);

    }
}