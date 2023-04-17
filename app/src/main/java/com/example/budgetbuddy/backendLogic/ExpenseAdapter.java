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

    public ExpenseAdapter(List<Expense> coffeeOrderList){
        this.expenseList = coffeeOrderList;
    }

    @Override
    public ExpenseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View orderView = layoutInflater.inflate(R.layout.expense_view, parent, false);
        ViewHolder myViewHolder = new ViewHolder(orderView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Expense expense = expenseList.get(position);
        ((TextView) holder.order.findViewById(R.id.expenseId)).setText("Id: " + expense.getId());
        ((TextView) holder.order.findViewById(R.id.date)).setText("Date: " + expense.getDate());
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
