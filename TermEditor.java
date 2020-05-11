package com.example.studentscheduler;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TermEditor extends AppCompatActivity {

    public final static String TAG = "TermEditor";
    String startFinalSelect = null;
    String endFinalSelect = null;
    String strID;
    Intent intent;


    DatabaseHelper dbHelper = new DatabaseHelper(this);


    DatePickerDialog datePickerDial;

    @OnClick(R.id.submitButton)
    void doneClickHandler() {
        //edit name field for Terms
        EditText nameEdit = findViewById(R.id.editName);
        String[] idArgs = {strID};



        //Prevent user from leaving fields blank in term creator
        if (TextUtils.isEmpty(nameEdit.getText()) || (startFinalSelect == null)
                || (endFinalSelect == null)) {
            View view = findViewById(R.id.coordinatorLayout2);
            String message = "Please choose information for all fields";
            int duration = Snackbar.LENGTH_LONG;

            Snackbar.make(view, message, duration).show();

        } else {
            String name, start, end;

            name = nameEdit.getText().toString();


            int result = 0;


           try {

                //change a record in the table if the user enters some data
                result = dbHelper.changeRecord("term_table", name, startFinalSelect, endFinalSelect,"term_id = ?", idArgs);
            } catch (NullPointerException e) {
              Log.d(TAG, "Null Pointer Exception from addTermRecord");

            }
            catch (Exception e) {
               Log.d(TAG, e.getMessage());
            }
            Log.i(TAG, "How many records were updated? " + result);
            finish();


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        intent = getIntent();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);


        //receive id, name, start and end date from terms adapter class
        int id = intent.getIntExtra("termId", 0);
        String name = intent.getStringExtra("termName");
        String startDate = intent.getStringExtra("startDate");
        String endDate = intent.getStringExtra("endDate");


        getSupportActionBar().setTitle("Term " + name + " editor");

        //make sure they are in the correct format
        SimpleDateFormat formatDate = new SimpleDateFormat("mm/dd/yyyy", Locale.US);
        Date formattedSDate = new Date();
        Date formattedEDate = new Date();

        try{
            formattedSDate = formatDate.parse(startDate);
            formattedEDate = formatDate.parse(endDate);

        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        //format to mm/dd/yyyy for the user
        startDate = new SimpleDateFormat("yyyy-mm-dd", Locale.US).format(formattedSDate);
        endDate = new SimpleDateFormat("yyyy-mm-dd", Locale.US).format(formattedEDate);


        //if user chooses to keep the start and end date the same set it to current chosen start/end
        startFinalSelect = startDate;
        endFinalSelect = endDate;

        //format current chosen start/end date
        SimpleDateFormat formatBackDate = new SimpleDateFormat("yyyy-mm-dd", Locale.US);
        Date formattedBackSDate = new Date();
        Date formattedBackEDate = new Date();

        try {
            formattedBackSDate = formatBackDate.parse(startDate);
            formattedBackEDate = formatBackDate.parse(endDate);
        }
        catch(ParseException e){
            e.printStackTrace();
        }
        //format to mm/dd/yyyy for the user
        startDate = new SimpleDateFormat("mm/dd/yyyy", Locale.US).format(formattedBackSDate);
        endDate = new SimpleDateFormat("mm/dd/yyyy", Locale.US).format(formattedBackEDate);


        //convert the int "id" to a string object
        strID = Integer.toString(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Calendar startCal = Calendar.getInstance();
        //Set up start and end edit boxes for calendar date picker
        EditText startEdit = findViewById(R.id.editStart);
        EditText endEdit = findViewById(R.id.editEndDate);


        startEdit.setShowSoftInputOnFocus(false);
        endEdit.setShowSoftInputOnFocus(false);


        //Set text in name on screen to be what is in the database
        final EditText nameText = findViewById(R.id.editName);
        nameText.setText(name);

        //Set text in date fields to show what is in the database
        startEdit.setText(startDate);
        endEdit.setText(endDate);


        //This date picker is for the start date in the term
        startEdit.setOnClickListener(v -> {

            //close keyboard when start edit is clicked
            closeKeyboard();

            //make sure calendar starts at current date
            int sYear = startCal.get(Calendar.YEAR); // current year
            int sMonth = startCal.get(Calendar.MONTH); // current month
            int sDay = startCal.get(Calendar.DAY_OF_MONTH); // current day

            datePickerDial = new DatePickerDialog(TermEditor.this,
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

            datePickerDial = new DatePickerDialog(TermEditor.this,
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



