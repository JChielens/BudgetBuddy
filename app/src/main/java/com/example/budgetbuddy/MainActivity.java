package com.example.budgetbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button expenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expenses = findViewById(R.id.btnExpenses);
    }

    public void onBtnExpenses (View Caller) {
        Intent intent = new Intent(this, ExpensesViewActivity.class);
        startActivity(intent);
    }
}