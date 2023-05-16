package com.example.budgetbuddy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.budgetbuddy.backendLogic.Expense;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView lblBudgetAmount;
    private TextView lblCurrentExpensesAmount;

    private ArrayList<Expense> expenses;
    private int userId;
    private float budget;
    private float currentExpenses;

    //Chart variables:
    private BarChart barChart;
    private PieChart pieChart;
    private HashMap<String, Float> expCategoryToAmount;
    private String[][] lastFourMonths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lblBudgetAmount = findViewById(R.id.lblBudgetAmount);
        lblCurrentExpensesAmount = findViewById(R.id.lblCurrentExpensesAmount);
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);

        currentExpenses = 0;
        expCategoryToAmount = new HashMap<String, Float>();
        expenses = new ArrayList<Expense>();

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId",1);
        expenses = intent.getParcelableArrayListExtra("expenses");
        budget = intent.getFloatExtra("budget",0);
        currentExpenses = getExpensesByMonthAndYear(LocalDate.now().getMonthValue(), LocalDate.now().getYear());

        lblBudgetAmount.setText(budget + " EUR");
        lblCurrentExpensesAmount.setText(currentExpenses + " EUR");
        setupVisuals();
        }

    private void setupVisuals(){
        setupHashmap();
        lastFourMonths = setupMonthsArray();

        setupBarChart();
        barChart.invalidate(); //refresh

        setupPieChart();
        pieChart.invalidate();
    }

    private float getExpensesByMonthAndYear(int monthToMatch, int yearToMatch){
        return (float) expenses.stream()
                .filter(e -> e.getDate().startsWith(yearToMatch + "-" + ((monthToMatch >= 10) ? monthToMatch : "0" + monthToMatch)))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public void onBtnExpenses(View Caller) {
        Intent intent = new Intent(this, ExpensesViewActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("budget", budget);
        intent.putParcelableArrayListExtra("expenses", expenses);
        startActivity(intent);
    }

    public void onBtnBudget(View Caller) {
        Intent intent = new Intent(this, BudgetActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("budget", budget);
        intent.putParcelableArrayListExtra("expenses", expenses);
        startActivity(intent);
    }

    public void setupBarChart() {
        initializeBarChartAppearance();
        setYAxisProperties();
        addDataToBarChart();
        setXAxisProperties();
        barChart.getAxisRight().setEnabled(false);
    }

    private String[][] setupMonthsArray() {
        LocalDate now = LocalDate.now();
        String[][] array = new String[4][2];
        for(int i = 0; i < 4; i++){
            if(now.getMonthValue() > i){
                array[i][0] = Month.of(now.getMonthValue() - i).name().toLowerCase();
                array[i][1] = Float.toString(getExpensesByMonthAndYear(now.getMonthValue() - i, LocalDate.now().getYear()));
            }
            else{
                array[i][0] = Month.of(now.getMonthValue() - i + 12).name().toLowerCase();
                array[i][1] = Float.toString(getExpensesByMonthAndYear(now.getMonthValue() - i + 12, LocalDate.now().getYear()) - 1);
            }
        }
        return array;

    }

    private void addDataToBarChart() {
        ArrayList<BarEntry> expensesPerMonthEntries = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            expensesPerMonthEntries.add(new BarEntry(i, Float.parseFloat(lastFourMonths[3-i][1])));
        }

        BarDataSet barDataSet = new BarDataSet(expensesPerMonthEntries, "Expenses last 4 months");
        barDataSet.setValueTextSize(12f);
        barDataSet.setValueTypeface(Typeface.SANS_SERIF);
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setValueFormatter(new LargeValueFormatter(" EUR"));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.7f);

        barChart.setData(barData);
    }

    private void setupHashmap() {
        expCategoryToAmount.clear();
        for(Expense e : expenses){
            int month = Integer.parseInt(e.getDate().substring(5,7));
            int year = Integer.parseInt(e.getDate().substring(0,4));
            if(month == LocalDate.now().getMonthValue() && year == LocalDate.now().getYear()){
                if(expCategoryToAmount.containsKey(e.getCategory())){
                    expCategoryToAmount.put(e.getCategory(), expCategoryToAmount.get(e.getCategory()) + e.getAmount());
                }
                else{
                    expCategoryToAmount.put(e.getCategory(),e.getAmount());
                }
            }
        }
        calculateUnused();
    }

    private void initializeBarChartAppearance() {
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.getLegend().setEnabled(false);
        barChart.setNoDataText("You have not added any expenses yet");
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setPinchZoom(true);
    }

    private void setYAxisProperties() {
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawLabels(false);
        leftAxis.setAxisLineColor(Color.TRANSPARENT);
        // makes Y-axis invisible - The y-axis doesn't get deleted because it changes proportions
        leftAxis.setValueFormatter(new LargeValueFormatter(" EUR"));
        leftAxis.setDrawGridLines(false);
        // leftAxis.setGranularity(500f);
        leftAxis.setAxisMinimum(0f); // start from zero
    }

    private void setXAxisProperties() {
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(4);
        xAxis.setTypeface(Typeface.SANS_SERIF);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(10f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]
                {lastFourMonths[3][0], lastFourMonths[2][0], lastFourMonths[1][0], lastFourMonths[0][0]}));

    }


    private void calculateUnused() {
        if(budget > currentExpenses){
            expCategoryToAmount.put("Unused", budget - currentExpenses);
        }
    }

    private void setupPieChart() {
        initializeChartAppearance();
        addDataToPieChart();
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

    private void addDataToPieChart() {
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