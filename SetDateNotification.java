package com.example.studentscheduler;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.studentscheduler.assessments.AssessmentCreator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetDateNotification extends AppCompatActivity {
    Intent intent;
    String date;
    String title;
    String body;
    String type;
    EditText dateText;
    DatePickerDialog datePickerDial;
    Date chosenDate;
    long dateInMillis;
    public final static String TAG = "SetDateNotification";

    String dateFinalSelect = null;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_date_notification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        ButterKnife.bind(this);



        intent = getIntent();
        date = intent.getStringExtra("date");
        title = intent.getStringExtra("title");
        body = intent.getStringExtra("body");
        type = intent.getStringExtra("type");


        dateText = findViewById(R.id.editText);
        dateText.setText(date);

        Log.d(TAG, "date is: " + date);
        getSupportActionBar().setTitle("Set " + type + " Notification");

        SimpleDateFormat formatBackDate = new SimpleDateFormat("mm/dd/yyyy", Locale.US);
        Date formattedBackSDate = new Date();
        try {
            formattedBackSDate = formatBackDate.parse(date);

        }
        catch(ParseException e){
            e.printStackTrace();
        }

        date = new SimpleDateFormat("yyyy-mm-dd", Locale.US).format(formattedBackSDate);

        dateFinalSelect = date;



        final Calendar dateCal = Calendar.getInstance();
        dateText.setShowSoftInputOnFocus(false);
        dateCal.getTimeInMillis();
        //get start date from user
        dateText.setOnClickListener(v -> {

            //close keyboard when start edit is clicked
            closeKeyboard();

            //make sure calendar starts at current date
            int sYear = dateCal.get(Calendar.YEAR); // current year
            int sMonth = dateCal.get(Calendar.MONTH); // current month
            int sDay = dateCal.get(Calendar.DAY_OF_MONTH); // current day

            datePickerDial = new DatePickerDialog(SetDateNotification.this,
                    (view, year, monthOfYear, dayOfMonth) -> {



                        //put chosen date in editText
                        dateText.setText((monthOfYear + 1) + "/"
                                + dayOfMonth + "/" + year);
                        //make sure the strings are set up correctly for the date to go into the database
                        if((monthOfYear +1) < 10 && dayOfMonth > 10){
                            dateFinalSelect =year + "-0" +  (monthOfYear +1) + "-" + dayOfMonth;
                        }
                        else if((monthOfYear +1) >= 10 && dayOfMonth < 10){
                            dateFinalSelect =year + "-" + (monthOfYear +1) + "-" + ("0" + dayOfMonth);
                        }
                        else if((monthOfYear +1) < 10 && dayOfMonth < 10){
                            dateFinalSelect =year + "-0" + (monthOfYear +1) + "-" + ("0" + dayOfMonth);
                        }
                        else {
                            dateFinalSelect = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        }



                    }, sYear, sMonth, sDay);
            datePickerDial.show();
        });




    }

    @OnClick(R.id.button)
    void doneClickHandler() {


        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date formattedDate = new Date();

        try{
            formattedDate = formatDate.parse(dateFinalSelect);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "formatted date is: "+ formattedDate);
        //get the difference between now and the requested notification date so it
        //can be added to the currentTimeMillis() below
        //NOTE: This notification will go off at midnight on the date chosen!
        dateInMillis = formattedDate.getTime() - System.currentTimeMillis();
        Log.d(TAG, "dateinMillis is: " + dateInMillis);
        View view = findViewById(R.id.coordinatorLayout6);
        String message = "Notification Set";
        int duration = Snackbar.LENGTH_LONG;
        //if the date chosen is before today's date let user know
        if(dateInMillis < 0){
            message = "Date chosen is before today's date. Please try again.";
            Snackbar.make(view, message, duration).show();
        }
        //if not, proceed
        else {
            message = "Notification Set";


            Snackbar.make(view, message, duration).show();
            //set up the notification setter
            //NOTE: This notification will go off at midnight on the date chosen!
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("body", body);
            intent.putExtra("title", title);
            PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + dateInMillis, sender);
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //This allows the "up" button to act like the "back" button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }



}
