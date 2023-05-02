package com.example.budgetbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.budgetbuddy.backendLogic.Expense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity {
    private static final String POST_URL = "https://studev.groept.be/api/a22pt403/addRow/";
    private EditText txtDescription;
    private EditText txtAmount;
    private Spinner categorySpinner;
    private TextView txtDate;
    private EditText txtPlace;
    private ArrayList<Expense> expenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        txtDescription = findViewById(R.id.txtDescription);
        txtAmount = findViewById(R.id.txtAmount);
        categorySpinner = findViewById(R.id.spinner);
        txtDate = findViewById(R.id.txtDate);
        txtPlace = findViewById(R.id.txtPlace);
        Intent intent = getIntent();
        expenses = intent.getParcelableArrayListExtra("expenses");
    }

    public void onTxtDate (View Caller) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth)
            {
                monthOfYear++;
                // want alle zaken van Calender class starten maand op index 0
                if (monthOfYear >= 10) {
                    txtDate.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                }
                else txtDate.setText(year + "-" + 0 + monthOfYear + "-" + dayOfMonth);

            }
        }, year, month, day);

        datePickerDialog.show();

    }

    public void onBtnSubmit_Clicked(View Caller){
        Expense expense = new Expense(expenses.get(expenses.size()-1).getId() + 1,
                Float.parseFloat(txtAmount.getText().toString()), txtDate.getText().toString(),
                txtPlace.getText().toString(), txtDescription.getText().toString(),
                categorySpinner.getSelectedItem().toString());
        expenses.add(expense);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest(
            Request.Method.POST,
            POST_URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    goToExpenseView();
                }
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
        intent.putParcelableArrayListExtra("expenses", expenses);
        startActivity(intent);
    }
}