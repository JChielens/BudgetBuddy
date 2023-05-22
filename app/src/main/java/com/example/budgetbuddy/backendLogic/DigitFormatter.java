package com.example.budgetbuddy.backendLogic;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class DigitFormatter extends ValueFormatter {
    private final DecimalFormat mFormat;
    private int mDigits;
    private String mPrefix;

    public DigitFormatter(int digits, String prefix) {
        mDigits = digits;
        mPrefix = prefix;
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0) {
                b.append(".");
            }
            b.append("0");
        }
        mFormat = new DecimalFormat("###,###,##0" + b.toString());
    }

    @Override
    public String getFormattedValue(float value) {
        if (value > 1000000) {
            return mPrefix + mFormat.format(value / 1000000f) + "m";
        } else if (value > 1000) {
            return mPrefix + mFormat.format(value / 1000f) + "k";
        } else {
            return mPrefix + mFormat.format(value);
        }
    }
}
