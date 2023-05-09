package com.example.budgetbuddy;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class CustomMarkerView extends MarkerView {
    private TextView txtLabelAndAmount;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        // find your layout components
        txtLabelAndAmount = findViewById(R.id.txtLabelAndAmount);
    }
    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        String label = ((PieEntry) e).getLabel();
        float value = e.getY();
        // set the entry-value as the display text
        txtLabelAndAmount.setText(label + ": " + value + " EUR");
        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }
    private MPPointF mOffset;
    @Override
    public MPPointF getOffset() {
        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2f), -getHeight());
        }
        return mOffset;
    }
}
