package com.example.studentscheduler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class TermCreator extends AppCompatActivity{

    public final static String TAG = "TermCreator";
    String startFinalSelect = null;
    String endFinalSelect = null;

    DatabaseHelper dbHelper = new DatabaseHelper(this);


    DatePickerDialog datePickerDial;



    @OnClick(R.id.submitButton)
    void doneClickHandler() {
        //edit name field for Terms
        EditText nameEdit = findViewById(R.id.editName);

        //Prevent user from leaving fields blank in term creator
        if (TextUtils.isEmpty(nameEdit.getText()) || (startFinalSelect == null)
                || (endFinalSelect == null)) {
            View view = findViewById(R.id.coordinatorLayout2);
            String message = "Please enter information in all fields";
            int duration = Snackbar.LENGTH_LONG;

            Snackbar.make(view, message, duration).show();

        } else {
            String name;

            name = nameEdit.getText().toString();


            boolean result = false;


            try {
                //add a record to the table if the user enters some data
                result = dbHelper.addTermRecord("term_name", name, "start_date", startFinalSelect, "end_date", endFinalSelect);
            } catch (NullPointerException e) {
                Log.d(TAG, "Null Pointer Exception from addTermRecord");

            }
            catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            Log.i(TAG, "Were records added to the database? " + result);
            finish();


        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_creator);
        //Set up tool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        ButterKnife.bind(this);

        final Calendar startCal = Calendar.getInstance();
        //Set up start and end edit boxes for calendar date picker
        EditText startEdit = findViewById(R.id.editStart);
        EditText endEdit = findViewById(R.id.editEndDate);


        startEdit.setShowSoftInputOnFocus(false);
        endEdit.setShowSoftInputOnFocus(false);

        //This date picker is for the start date in the term
        startEdit.setOnClickListener(v -> {

            //close keyboard when start edit is clicked
            closeKeyboard();

            //make sure calendar starts at current date
            int sYear = startCal.get(Calendar.YEAR); // current year
            int sMonth = startCal.get(Calendar.MONTH); // current month
            int sDay = startCal.get(Calendar.DAY_OF_MONTH); // current day

            datePickerDial = new DatePickerDialog(TermCreator.this,
                    (view, year, monthOfYear, dayOfMonth) -> {

                        //put chosen date in editText
                        startEdit.setText((monthOfYear + 1) + "/"
                                + dayOfMonth + "/" + year);
                        //make sure the strings are set up correctly for the date to go into the database
                        if((monthOfYear +1) < 10 && dayOfMonth > 10){
                            startFinalSelect =year + "-0" +  (monthOfYear +1) + "-" + dayOfMonth;
                        }
                        else if((monthOfYear +1) > 10 && dayOfMonth < 10){
                            startFinalSelect =year + "-" + (monthOfYear +1) + "-" + ("0" + dayOfMonth);
                        }
                        else if((monthOfYear +1) < 10 && dayOfMonth < 10){
                            startFinalSelect =year + "-0" + (monthOfYear +1) + "-" + ("0" + dayOfMonth);
                        }
                        else {
                            startFinalSelect = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        }



                    }, sYear, sMonth, sDay);
            datePickerDial.show();
        });

        //This date picker is for the end date in the term
        endEdit.setOnClickListener(v -> {
            //close keyboard when end edit is clicked
            closeKeyboard();
            //make sure calendar starts at current date
            final Calendar endCal = Calendar.getInstance();
            int eYear = endCal.get(Calendar.YEAR); // current year
            int eMonth = endCal.get(Calendar.MONTH); // current month
            int eDay = endCal.get(Calendar.DAY_OF_MONTH); // current day

            datePickerDial = new DatePickerDialog(TermCreator.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        //display chosen date in editText object
                        endEdit.setText((monthOfYear + 1) + "/"
                                + dayOfMonth + "/" + year);

                        //make sure the strings are set up correctly for the date to go into the database
                        if((monthOfYear +1) < 10 && dayOfMonth > 10){
                            endFinalSelect =year + "-0" +  (monthOfYear +1) + "-" + dayOfMonth;
                        }
                        else if((monthOfYear +1) > 10 && dayOfMonth < 10){
                            endFinalSelect =year + "-" + (monthOfYear +1) + "-" + ("0" + dayOfMonth);
                        }
                        else if((monthOfYear +1) < 10 && dayOfMonth < 10){
                            endFinalSelect =year + "-0" + (monthOfYear +1) + "-" + ("0" + dayOfMonth);
                        }
                        else {
                            endFinalSelect = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        }

                    }, eYear, eMonth, eDay);
            datePickerDial.show();
        });







    }
   private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }}






