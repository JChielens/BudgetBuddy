package com.example.budgetbuddy.backendLogic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddy.R;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder>{

    private List<Expense> expenseList;
    private static final DigitFormatter df = new DigitFormatter(2, "€");

    public ExpenseAdapter(List<Expense> coffeeOrderList){
        this.expenseList = coffeeOrderList;
    }

    @Override
    public ExpenseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View orderView = layoutInflater.inflate(R.layout.expense_info_overview, parent, false);
        ViewHolder myViewHolder = new ViewHolder(orderView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Expense expense = expenseList.get(position);
        ((TextView) holder.order.findViewById(R.id.date)).setText("Date: " + expense.getDate());
        ((TextView) holder.order.findViewById(R.id.category)).setText("Category: " + expense.getCategory());
        ((TextView) holder.order.findViewById(R.id.place)).setText("Place: " + expense.getPlace());
        ((TextView) holder.order.findViewById(R.id.amount)).setText("Amount: " + df.getFormattedValue(expense.getAmount()));
        if(expense.getDescription().equals("null") || expense.getDescription().equals("")){
            ((TextView) holder.order.findViewById(R.id.description)).setText("Description:  No description");
        }
        else{
            ((TextView) holder.order.findViewById(R.id.description)).setText("Description: " + expense.getDescription());
        }

    }

    @Override
    public int getItemCount(){
        return expenseList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        public View order;

        public ViewHolder(View coffeeOrderView){
            super(coffeeOrderView);
            order = (View) coffeeOrderView;
        }
    }
}
