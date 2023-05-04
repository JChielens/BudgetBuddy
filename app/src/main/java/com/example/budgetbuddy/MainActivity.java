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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.budgetbuddy.backendLogic.Expense;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    private String QUEUE_URL = "https://studev.groept.be/ap" +
            "" +
            "i/a22pt403/getAll";
    private TextView lblBudgetAmount;
    private ArrayList<Expense> expenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblBudgetAmount = findViewById(R.id.lblBudgetAmount);
        expenses = new ArrayList<Expense>();
        Intent intent = getIntent();
        int out = intent.getExtras() != null ? getExpenseFromIntent(intent) : requestExpenseListQueue();
    }

    private int getExpenseFromIntent(Intent intent){
        expenses = intent.getParcelableArrayListExtra("expenses");
        return 0;
    }

    private int requestExpenseListQueue(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                QUEUE_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processJSONResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                MainActivity.this,
                                "Unable to communicate with the server",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(queueRequest);
        return 0;
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

    public void onBtnExpenses (View Caller) {
        Intent intent = new Intent(this, ExpensesViewActivity.class);
        intent.putParcelableArrayListExtra("expenses", expenses);
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