package com.test.papin.dictionary;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DIalogAdd extends DialogFragment implements View.OnClickListener {

    TextView Word,tmp,forAnswer;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Добавление");

        View v = inflater.inflate(R.layout.dialog_add, null);
        v.findViewById(R.id.button2).setOnClickListener(this);
        v.findViewById(R.id.button3).setOnClickListener(this);
        tmp=(TextView)v.findViewById(R.id.textView2);
        Word=(TextView)getActivity().findViewById(R.id.inviz1);
        tmp.setText(Word.getText().toString());
        return v;
    }

    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.button2:
                forAnswer = (TextView)getActivity().findViewById(R.id.inviz2);
                forAnswer.setText("OKEY");
                String[] s = tmp.getText().toString().split("-");

                dismiss();
            case R.id.button3:
                forAnswer = (TextView)getActivity().findViewById(R.id.inviz2);
                forAnswer.setText("NO");
                dismiss();
                default:dismiss();
        }
    }
}

