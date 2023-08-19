package com.example.finalproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class BirthSelectDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    private String date;


    public BirthSelectDialog(){

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Instance of Calendar, used to obtain the current time
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //Because of the implementation of OnDateSetListener ，the second parameter is "this"
        return new DatePickerDialog(getActivity(), this, year, month, day);
        //After selected the scene showed on the button
    }

    //实现OnDateSetListener接口的onDateSet()方法
    //send the date in the dialog back to the activity
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (getActivity() instanceof DataCallBack) {
                //convert activity into DataCallBack
                DataCallBack dataCallBack = (DataCallBack) getActivity();
                date = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                //Pass the data back to the Activity and display
                dataCallBack.getData(date);
            }
    }
}
