package com.example.budgetbuddy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.StackedValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String QUEUE_URL = "https://studev.groept.be/api/a22pt403/getAllExpensesFromUser/";
    private TextView lblBudgetAmount;
    private TextView lblCurrentExpensesAmount;
    private ArrayList<Expense> expenses;
    //BarChart variables:
    private BarChart barChart;
    private int userId;

    private PieChart pieChart;
    private HashMap<String, Float> expCategoryToAmount;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblBudgetAmount = findViewById(R.id.lblBudgetAmount);
        lblCurrentExpensesAmount = findViewById(R.id.lblCurrentExpensesAmount);

        expCategoryToAmount = new HashMap<String, Float>();
        setupHashmap();

        barChart = findViewById(R.id.barChart);
        setupBarChart();
        barChart.invalidate(); //refresh (tip: doen na aanpassen v/d data)

        pieChart = findViewById(R.id.pieChart);
        setupPieChart();
        pieChart.invalidate();

        expenses = new ArrayList<Expense>();
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId",1);
        Toast.makeText(
                MainActivity.this,
                "User id: " + userId,
                Toast.LENGTH_LONG).show();
        int out = intent.getParcelableArrayListExtra("expenses") != null ? getExpenseFromIntent(intent) : requestExpenseListQueue();


    }

    private int getExpenseFromIntent(Intent intent) {
        expenses = intent.getParcelableArrayListExtra("expenses");
        return 0;
    }

    private int requestExpenseListQueue() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                QUEUE_URL + userId,
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
        intent.putExtra("userId", userId);
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
                //TODO: ook aanpassen dat de budget i/d database wordt aangepast!
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
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setPinchZoom(true); //TODO: testen op Android gsm


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
        barDataSet.setValueTextColor(Color.WHITE);
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
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(10f);

        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]
                {month1, month2, month3, month4}));

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawLabels(false);
        leftAxis.setAxisLineColor(Color.TRANSPARENT);
        // I make my y-axis invisble this way.
        // Because when I disable my y-axis the bars are not to scale anymore.

        leftAxis.setEnabled(true);
        leftAxis.setValueFormatter(new LargeValueFormatter(" EUR"));
        leftAxis.setDrawGridLines(false);
        // leftAxis.setGranularity(500f);
        leftAxis.setAxisMinimum(0f); // start from zero


        barChart.getAxisRight().setEnabled(false);
    }

    private void setupHashmap() {

        expCategoryToAmount.put("Food", 150f); // $150 for food
        expCategoryToAmount.put("Clothing", 100f); // $100 for clothing
        expCategoryToAmount.put("Transportation", 50f); // $50 for transportation
        expCategoryToAmount.put("Utilities", 100f); // $100 for utilities
        expCategoryToAmount.put("Recreation and Entertainment", 50f); // $50 for recreation and entertainment
        expCategoryToAmount.put("Medical", 50f); // $50 for medical
        expCategoryToAmount.put("Insurance", 100f); // $100 for insurance
        expCategoryToAmount.put("Saving", 100f); // $100 for saving
        expCategoryToAmount.put("Investing", 100f); // $100 for investing

        //TODO: if expenses>budget => deze "calculateUnused()" functie niet oproepen!!!
        calculateUnused();

    }

    private void calculateUnused() {
        float sumOfExpensesAmounts = 0f;

        for (float amount : expCategoryToAmount.values()) {
            sumOfExpensesAmounts += amount;
        }

        //TODO: deze lijn code vervangen door
        // ofwel opvragen van database
        // Ofwel budget opvragen van database bij onCreat and opslaan als "field" in de klasse.
        float budget = 900f;

        float unused = budget - sumOfExpensesAmounts;

        expCategoryToAmount.put("Unused", unused);
    }

    private void setupPieChart() {
        initializeChartAppearance();
        addDataToChart();
    }

    private void initializeChartAppearance() {
        pieChart.setNoDataText("You have not added any expenses yet");
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setUsePercentValues(true);
        pieChart.setRotationEnabled(false);
        pieChart.getLegend().setEnabled(false);
        MarkerView mv = new CustomMarkerView(this, R.layout.marker_view);
        pieChart.setMarker(mv);
    }

    private void addDataToChart() {
        ArrayList<PieEntry> expensesPerCategory = new ArrayList<>();
        for (Map.Entry<String, Float> entry : expCategoryToAmount.entrySet()) {
            expensesPerCategory.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        ArrayList<Integer> customColors = getCustomColors();

        PieDataSet pieDataSet = new PieDataSet(expensesPerCategory, "Expenses per Expense Category");
        pieDataSet.setColors(customColors);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieData.setValueTextSize(12f);

        pieChart.setData(pieData);
    }


    private ArrayList<Integer> getCustomColors() {
        //initializing colors for the entries
        //TODO: degelijke kleurencombinatie gebruiken
        ArrayList<Integer> customColors = new ArrayList<>();
        customColors.add(Color.parseColor("#619ED6"));
        customColors.add(Color.parseColor("#6BA547"));
        customColors.add(Color.parseColor("#F7D027"));
        customColors.add(Color.parseColor("#E48F1B"));
        customColors.add(Color.parseColor("#B77EA3"));
        customColors.add(Color.parseColor("#E64345"));
        customColors.add(Color.parseColor("#60CEED"));
        customColors.add(Color.parseColor("#9CF168"));
        customColors.add(Color.parseColor("#F7EA4A"));
        customColors.add(Color.parseColor("#FBC543"));
        //extra/reserve colors
//        customColors.add(Color.parseColor("#A9A9A9")); //grey for 'unused'
//        customColors.add(Color.parseColor("#FFC9ED"));
//        customColors.add(Color.parseColor("#E6696E"));

        return customColors;
    }

    }