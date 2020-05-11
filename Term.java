package com.example.studentscheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Term {

    private int term_id;
    private String term_name;
    private String start_date;
    private String end_date;

    public Term(int term_id, String term_name, String start_date, String end_date) {
        this.term_id = term_id;
        this.term_name = term_name;
        this.start_date = start_date;
        this.end_date = end_date;
    }




    public int getTerm_id() {
        return term_id;
    }

    public void setTerm_id(int term_id) {
        this.term_id = term_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getTerm_name() {
        return term_name;
    }

    public void setTerm_name(String term_name) {
        this.term_name = term_name;
    }

    public String getText() {
        //format current chosen start/end date
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-mm-dd", Locale.US);
        Date formattedSDate = new Date();
        Date formattedEDate = new Date();

        try {
            formattedSDate = formatDate.parse(start_date);
            formattedEDate = formatDate.parse(end_date);
        }
        catch(ParseException e){
            e.printStackTrace();
        }
        //format to mm/dd/yyyy for the user
        start_date = new SimpleDateFormat("mm/dd/yyyy", Locale.US).format(formattedSDate);
        end_date = new SimpleDateFormat("mm/dd/yyyy", Locale.US).format(formattedEDate);

        return term_name + "\nStart: " + start_date + "\nEnd: " + end_date;
    }
}
