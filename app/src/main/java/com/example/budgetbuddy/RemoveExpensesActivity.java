package com.example.budgetbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.example.budgetbuddy.backendLogic.Expense;
import com.example.budgetbuddy.backendLogic.ExpenseAdapter;
import com.example.budgetbuddy.backendLogic.RemoveExpenseAdapter;

import java.util.ArrayList;
import java.util.Iterator;

public class RemoveExpensesActivity extends AppCompatActivity {
    private ArrayList<Expense> expenses;
    private RecyclerView expenseList;
    private CheckBox checked;

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
                it.remove();
            }
        }
        Intent intent = new Intent(this, ExpensesViewActivity.class);
        intent.putParcelableArrayListExtra("expenses", expenses);
        startActivity(intent);
    }
}