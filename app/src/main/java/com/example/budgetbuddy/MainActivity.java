package com.example.budgetbuddy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.icu.util.CurrencyAmount;
import android.os.Bundle;
import android.text.InputType;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.StackedValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String QUEUE_URL = "https://studev.groept.be/ap" +
            "" +
            "i/a22pt403/getAll";
    private TextView lblBudgetAmount;
    private ArrayList<Expense> expenses;
    //BarChart variables:
    private BarChart barChart;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblBudgetAmount = findViewById(R.id.lblBudgetAmount);

        barChart = findViewById(R.id.barChart);
        setupBarChart();

        expenses = new ArrayList<Expense>();
        Intent intent = getIntent();
        int out = intent.getExtras() != null ? getExpenseFromIntent(intent) : requestExpenseListQueue();


    }

    private int getExpenseFromIntent(Intent intent) {
        expenses = intent.getParcelableArrayListExtra("expenses");
        return 0;
    }

    private int requestExpenseListQueue() {
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

    private void processJSONResponse(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                Expense expense = new Expense(response.getJSONObject(i));
                expenses.add(expense);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void onBtnExpenses(View Caller) {
        Intent intent = new Intent(this, ExpensesViewActivity.class);
        intent.putParcelableArrayListExtra("expenses", expenses);
        startActivity(intent);
    }

    public void onBtnBudget(View Caller) {
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
                    lblBudgetAmount.setText(budget + " EUR");

                    Toast.makeText(MainActivity.this,
                            "You changed your budget to " + budget + " EUR",
                            Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

        builder.show();
    }

    public void setupBarChart() {

        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        //barChart.setDrawValueAboveBar(false);
        barChart.getLegend().setEnabled(false);
        barChart.setNoDataText("You have not added any expenses yet");


        //TODO: ook rekening houden mocht de user nog geen expenses hebben ingegeven in de database!!
        // bv in dat geval expensesMonthX = 0f

        String month1 = "February"; //aanpassen m.b.v. database
        String month2 = "March";
        String month3 = "April";
        String month4 = "May";

        float expensesMonth1 = 1500f; //values aanpassen m.b.v. database
        float expensesMonth2 = 1000f;
        float expensesMonth3 = 2000f;
        float expensesMonth4 = 1550f;


        ArrayList<BarEntry> expensesPerMonthEntries = new ArrayList<>();
        expensesPerMonthEntries.add(new BarEntry(0f, expensesMonth1));
        expensesPerMonthEntries.add(new BarEntry(1f, expensesMonth2));
        expensesPerMonthEntries.add(new BarEntry(2f, expensesMonth3));
        expensesPerMonthEntries.add(new BarEntry(3f, expensesMonth4));

        BarDataSet barDataSet = new BarDataSet(expensesPerMonthEntries, "Expenses last 4 months");
        barDataSet.setValueTextSize(12f);
        barDataSet.setValueTypeface(Typeface.SANS_SERIF);
        barDataSet.setValueFormatter(new LargeValueFormatter(" EUR"));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.7f);

        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(4);
        xAxis.setTypeface(Typeface.SANS_SERIF);
        xAxis.setTextSize(10f);

        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]
                {month1, month2, month3, month4}));

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawLabels(false);
        // I make my y-axis invisble this way.
        // Because when I disable my y-axis the bars are not to scale anymore.

        leftAxis.setEnabled(true);
        leftAxis.setValueFormatter(new LargeValueFormatter(" EUR"));
        leftAxis.setDrawGridLines(false);
        // leftAxis.setGranularity(500f);
        leftAxis.setAxisMinimum(0f); // start from zero


        barChart.getAxisRight().setEnabled(false);
    }

    }