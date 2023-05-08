package com.wrteam.quiz.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.wrteam.quiz.R;
import com.wrteam.quiz.model.Question;


import java.util.ArrayList;

public class BottomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public ArrayList<Question> questionList;
    public Activity activity;
    BottomSheetDialog dialog;
    ViewPager viewPager;

    public BottomAdapter(ArrayList<Question> questionList, Activity activity, BottomSheetDialog dialog, ViewPager viewPager) {
        this.questionList = questionList;
        this.activity = activity;
        this.viewPager = viewPager;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_layout, parent, false);
        return new TestHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1,final int position) {
        TestHolder holder = (TestHolder) holder1;
        final Question question = questionList.get(position);
        holder.tvQueNo.setText(String.valueOf(position + 1));
        holder.tvQueNo.setBackgroundResource(question.isAttended ? R.drawable.ic_attended_bg : R.drawable.bottom_view_bg);
        holder.tvQueNo.setOnClickListener(view -> {
            dialog.dismiss();
            viewPager.setCurrentItem(position);

        });

    }

    public static class TestHolder extends RecyclerView.ViewHolder {

        TextView tvQueNo;

        public TestHolder(@NonNull View itemView) {
            super(itemView);
            tvQueNo = itemView.findViewById(R.id.tvQueNo);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return questionList.size();
        //return 23;
    }
}
