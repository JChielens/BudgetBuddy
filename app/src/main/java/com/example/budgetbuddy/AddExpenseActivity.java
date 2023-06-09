package com.example.budgetbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class AddExpenseActivity extends AppCompatActivity {
    private static final String POST_URL = "https://studev.groept.be/api/a22pt403/addRow/";
    private EditText txtDescription;
    private TextInputLayout txtDescriptionLayout;
    private EditText txtAmount;
    private TextInputLayout txtAmountLayout;
    private Spinner categorySpinner;
    private EditText txtPlace;
    private TextInputLayout txtPlaceLayout;
    private TextView lblSelectedDate;
    private ArrayList<Expense> expenses;
    private int userId;
    private float budget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        txtDescription = findViewById(R.id.inputTxtDescription);
        txtAmount = findViewById(R.id.inputTxtAmount);
        categorySpinner = findViewById(R.id.spinner);
        txtPlace = findViewById(R.id.inputTxtPlace);
        lblSelectedDate = findViewById(R.id.lblSelectedDate);
        txtDescriptionLayout = findViewById(R.id.txtDescription);
        txtAmountLayout =findViewById(R.id.txtAmount);
        txtPlaceLayout =findViewById(R.id.txtPlace);
        setupTextChangedListeners();

        Intent intent = getIntent();
        expenses = intent.getParcelableArrayListExtra("expenses");
        userId = intent.getIntExtra("userId", 1);
        budget = intent.getFloatExtra("budget", 0);
    }

    private boolean isFieldEmpty(TextView textView) {
        return textView.getText().toString().trim().isEmpty();
    }

    private void setupTextChangedListeners() {
        txtDescription.addTextChangedListener(getTextWatcher(txtDescriptionLayout));
        txtAmount.addTextChangedListener(getTextWatcher(txtAmountLayout));
        txtPlace.addTextChangedListener(getTextWatcher(txtPlaceLayout));
    }

    private TextWatcher getTextWatcher(TextInputLayout textInputLayout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        };
    }

    public void onlblSelectedDate(View Caller) {
        //here we get the current date:
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                lblSelectedDate.setError(null);
                monthOfYear++;
                // want alle zaken van Calender class starten maand op index 0
                if (monthOfYear >= 10 && dayOfMonth >= 10) {
                    lblSelectedDate.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                }
                else if (dayOfMonth >= 10){
                    lblSelectedDate.setText(year + "-" + 0 + monthOfYear + "-" + dayOfMonth);
                }
                else if (monthOfYear >= 10){
                    lblSelectedDate.setText(year + "-" + monthOfYear + "-" + 0 + dayOfMonth);
                }
                else {
                    lblSelectedDate.setText(year + "-" + 0 + monthOfYear + "-" + 0 + dayOfMonth);
                }
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public void onBtnSubmit_Clicked(View Caller){
        // Get the highest index currently present, empty optional if no expenses yet
        OptionalInt highestIndex = expenses.stream()
                .mapToInt(Expense::getExpenseId)
                .max();
        // Check that all fields are filled in
        if(!isFieldEmpty(txtAmount) && !isFieldEmpty(lblSelectedDate) &&
                !isFieldEmpty(txtPlace) && !isFieldEmpty(txtDescription)){
            // Create Expense object and add it to the ArrayList and database
            Expense expense = new Expense((highestIndex.isPresent()) ? highestIndex.getAsInt() + 1 : 1,
                    userId, Float.parseFloat(txtAmount.getText().toString().trim()),
                    lblSelectedDate.getText().toString().trim(), txtPlace.getText().toString().trim(),
                    txtDescription.getText().toString().trim(), categorySpinner.getSelectedItem().toString());
            expenses.add(expense);
            expenses.sort((c1,c2) -> c1.getDate().compareTo(c2.getDate()));
            postExpense(expense);
            goToExpenseView();
        }
        else{
            setEmptyFieldsErrorMessages();
        }
    }

    private void setEmptyFieldsErrorMessages() {
        if (isFieldEmpty(txtAmount)) {
            txtAmountLayout.setError("Amount cannot be empty");}
        if (isFieldEmpty(lblSelectedDate)) {
            lblSelectedDate.setError("Date cannot be empty");}
        if (isFieldEmpty(txtPlace)) {
            txtPlaceLayout.setError("Place cannot be empty");}
        if (isFieldEmpty(txtDescription)) {
        txtDescriptionLayout.setError("Description cannot be empty");}
    }

    private void postExpense(Expense expense){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest(
                Request.Method.POST,
                POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {}
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                AddExpenseActivity.this,
                                "Unable to post the expense" + error,
                                Toast.LENGTH_LONG).show();
                    }
                }
        )   { //NOTE THIS PART: here we are passing the POST parameters to the webservice
            @Override
            protected Map<String, String> getParams() {
                /* Map<String, String> with key value pairs as data load */
                return expense.getPostParameters();
            }
        };
        requestQueue.add(submitRequest);
    }

    private void goToExpenseView(){
        Intent intent = new Intent(this, ExpensesViewActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("budget", budget);
        intent.putParcelableArrayListExtra("expenses", expenses);
        startActivity(intent);
    }

    public void onBackButton_clicked(View caller) {
        goToExpenseView();
    }
}