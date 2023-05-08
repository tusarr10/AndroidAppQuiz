package com.wrteam.quiz.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.ImageLoader;
import com.wrteam.quiz.Constant;
import com.wrteam.quiz.R;
import com.wrteam.quiz.helper.AppController;
import com.wrteam.quiz.helper.MathJaxWebView;
import com.wrteam.quiz.helper.Session;
import com.wrteam.quiz.helper.TouchImageView;
import com.wrteam.quiz.model.Question;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class MathsReviewFragment extends Fragment {

    private ArrayList<Question> questionList;
    ArrayList<String> options;
    private static final String QUESTION_INDEX = "question_index";
    public ScrollView mainScroll, queScroll;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    TouchImageView imgQuestion;
    public RelativeLayout layout_A, layout_B, layout_C, layout_D, layout_E, noteLyt;
    public static TextView tvAlert, tvIndex, tvA, tvB, tvC, tvD, tvE;

    public TextView tvExtraNote;
    public MathJaxWebView txtQuestion, tvImgQues, btnOpt1, btnOpt2, btnOpt3, btnOpt4, btnOpt5,tvSolution ;
    ImageView imgZoom;
    int click = 0;




    public MathsReviewFragment() {
        // Required empty public constructor
    }


    public MathsReviewFragment(ArrayList<Question> questionList) {
        this.questionList = questionList;

    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mathsreview, container, false);
        btnOpt1 = view.findViewById(R.id.btnOpt1);
        btnOpt2 = view.findViewById(R.id.btnOpt2);
        btnOpt3 = view.findViewById(R.id.btnOpt3);
        btnOpt4 = view.findViewById(R.id.btnOpt4);
        btnOpt5 = view.findViewById(R.id.btnOpt5);

        tvA = view.findViewById(R.id.tvA);
        tvB = view.findViewById(R.id.tvB);
        tvC = view.findViewById(R.id.tvC);
        tvD = view.findViewById(R.id.tvD);
        tvE = view.findViewById(R.id.tvE);
        tvExtraNote = view.findViewById(R.id.tvExtraNote);
        tvSolution = view.findViewById(R.id.tvSolution);

        imgQuestion = view.findViewById(R.id.imgQuestion);
        txtQuestion = view.findViewById(R.id.txtQuestion);
        tvImgQues = view.findViewById(R.id.tvImgQues);
        imgZoom = view.findViewById(R.id.imgZoom);
        mainScroll = view.findViewById(R.id.mainScroll);
        queScroll = view.findViewById(R.id.queScroll);
        noteLyt = view.findViewById(R.id.noteLyt);
        layout_A = view.findViewById(R.id.a_layout);
        layout_B = view.findViewById(R.id.b_layout);
        layout_C = view.findViewById(R.id.c_layout);
        layout_D = view.findViewById(R.id.d_layout);
        layout_E = view.findViewById(R.id.e_layout);


        assert getArguments() != null;
        final Question question = questionList.get(getArguments().getInt(QUESTION_INDEX));

        mainScroll.setOnTouchListener((v, event) -> {
            v.findViewById(R.id.queScroll).getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });
        queScroll.setOnTouchListener((v, event) -> {
            //Disallow the touch request for parent scroll on touch of child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        imgQuestion.resetZoom();

        options = new ArrayList<>();
        options.addAll(question.getOptions());

        if (Session.getBoolean(Session.E_MODE, getActivity())) {
            if (options.size() == 4)
                layout_E.setVisibility(View.GONE);
            else
                layout_E.setVisibility(View.VISIBLE);

        }
        btnOpt1.setText((options.get(0).trim()));
        btnOpt2.setText((options.get(1).trim()));
        btnOpt3.setText((options.get(2).trim()));
        btnOpt4.setText((options.get(3).trim()));
        if (Session.getBoolean(Session.E_MODE, getActivity())) {
            if (options.size() == 5)
                btnOpt5.setText((options.get(4).trim()));

        }
        layout_A.setBackgroundResource(R.drawable.card_shadow);
        layout_B.setBackgroundResource(R.drawable.card_shadow);
        layout_C.setBackgroundResource(R.drawable.card_shadow);
        layout_D.setBackgroundResource(R.drawable.card_shadow);
        layout_E.setBackgroundResource(R.drawable.card_shadow);


        if (question.getNote() != null) {
            if (question.getNote().isEmpty()) {
                noteLyt.setVisibility(View.GONE);
            } else {
                noteLyt.setVisibility(View.VISIBLE);
            }
        } else {
            noteLyt.setVisibility(View.GONE);
        }


        tvSolution.setVisibility(View.GONE);
        tvExtraNote.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
        tvExtraNote.setTag("up");
        tvExtraNote.setOnClickListener(view12 -> {
            String solution = question.getNote();
            if (tvExtraNote.getTag().equals("up")) {
                tvExtraNote.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up, 0);
                tvSolution.setVisibility(View.VISIBLE);
                tvSolution.setText((solution));
                tvExtraNote.setTag("down");
            } else {
                tvExtraNote.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
                tvSolution.setVisibility(View.GONE);
                tvExtraNote.setTag("up");
            }

        });

        if (question.getImage() != null) {
            if (!question.getImage().isEmpty()) {
                txtQuestion.setVisibility(View.GONE);
                imgQuestion.setImageUrl(question.getImage(), imageLoader);
                tvImgQues.setText(question.getQuestion());
                tvImgQues.setVisibility(View.VISIBLE);
                imgZoom.setVisibility(View.VISIBLE);
                imgQuestion.setVisibility(View.VISIBLE);
                imgQuestion.setVisibility(View.VISIBLE);
                imgZoom.setOnClickListener(view1 -> {
                    click++;
                    if (click == 1)
                        imgQuestion.setZoom(1.25f);
                    else if (click == 2)
                        imgQuestion.setZoom(1.50f);
                    else if (click == 3)
                        imgQuestion.setZoom(1.75f);
                    else if (click == 4) {
                        imgQuestion.setZoom(2.00f);
                        click = 0;
                    }
                });

            } else {
                txtQuestion.setText(question.getQuestion());
                imgZoom.setVisibility(View.GONE);
                imgQuestion.setVisibility(View.GONE);
                tvImgQues.setVisibility(View.GONE);
                txtQuestion.setVisibility(View.VISIBLE);
            }

        } else {
            txtQuestion.setText(question.getQuestion());
            imgZoom.setVisibility(View.GONE);
            imgQuestion.setVisibility(View.GONE);
            tvImgQues.setVisibility(View.GONE);
            txtQuestion.setVisibility(View.VISIBLE);
        }

        String wrongAns;
        if (question.getSelectedAns() != null) {
            wrongAns = question.getSelectedAns().trim();
        } else {
            wrongAns = "";
        }

        if (tvA.getText().toString().equalsIgnoreCase(wrongAns)) {
            layout_A.setBackgroundResource(R.drawable.wrong_gradient);
        } else if (tvB.getText().toString().equalsIgnoreCase(wrongAns)) {
            layout_B.setBackgroundResource(R.drawable.wrong_gradient);
        } else if (tvC.getText().toString().equalsIgnoreCase(wrongAns)) {
            layout_C.setBackgroundResource(R.drawable.wrong_gradient);
        } else if (tvD.getText().toString().equalsIgnoreCase(wrongAns)) {
            layout_D.setBackgroundResource(R.drawable.wrong_gradient);
        } else if (tvExtraNote.getText().toString().equalsIgnoreCase(wrongAns)) {
            layout_E.setBackgroundResource(R.drawable.wrong_gradient);
        }
        RightAnswerBackgroundSet(question);
        queScroll.scrollTo(0, 0);


        return view;
    }


    public void RightAnswerBackgroundSet(Question question) {
        if (tvA.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_A.setBackgroundResource(R.drawable.right_gradient);


        } else if (tvB.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_B.setBackgroundResource(R.drawable.right_gradient);


        } else if (tvC.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_C.setBackgroundResource(R.drawable.right_gradient);


        } else if (tvD.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_D.setBackgroundResource(R.drawable.right_gradient);

        } else if (tvE.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_E.setBackgroundResource(R.drawable.right_gradient);

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static MathsReviewFragment newInstance(int sectionNumber, ArrayList<Question> questionList) {
        MathsReviewFragment fragment = new MathsReviewFragment(questionList);
        Bundle args = new Bundle();
        args.putInt(QUESTION_INDEX, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

}