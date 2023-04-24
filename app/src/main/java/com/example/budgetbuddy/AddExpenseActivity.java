package com.example.budgetbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class AddExpenseActivity extends AppCompatActivity {
    private TextView txtDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        txtDate = findViewById(R.id.txtDate);
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
}