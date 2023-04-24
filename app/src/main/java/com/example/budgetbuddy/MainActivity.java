package com.example.budgetbuddy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {



    private TextView lblBudgetAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblBudgetAmount = findViewById(R.id.lblBudgetAmount);

    }

    public void onBtnExpenses (View Caller) {
        Intent intent = new Intent(this, ExpensesViewActivity.class);
        startActivity(intent);
    }

    public void onBtnBudget (View Caller) {
        showDialog();

    }

    private void showDialog() {

        // AlertDialog.Builder object aanmaken
        AlertDialog.Builder builder = new AlertDialog.Builder(this);



        //Input aanmaken
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        input.setHint("Budget");


        //AlertDialog.Builder object Customizen
        builder.setTitle("Change Budget")
                .setView(input)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    String budget = input.getText().toString();
                    lblBudgetAmount.setText(budget);

                    Toast.makeText(MainActivity.this ,
                            "You changed your budget to €"+budget,
                            Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

        builder.show();
    }
}