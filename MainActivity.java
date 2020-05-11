package com.example.studentscheduler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.studentscheduler.courses.ViewAllCourses;
import com.example.studentscheduler.notes.ViewAllNotes;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;

    //When view terms button is clicked go to termviewer activity
    @OnClick(R.id.term_view)
    void termClickHandler(){

        String rows;

        Intent passIntent = new Intent(MainActivity.this, TermViewer.class);

        rows = dbHelper.checkForRows("SELECT COUNT(*) FROM term_table");

        if(rows == "false"){
            passIntent.putExtra("rows", rows);
        }
        else{
            passIntent.putExtra("rows", rows);
        }

        startActivity(passIntent);

    }

    @OnClick(R.id.notes_view)
    void notesClickHandler(){
        Intent passIntent = new Intent(MainActivity.this, ViewAllNotes.class);
        startActivity(passIntent);

    }

    @OnClick(R.id.courses_view)
    void coursesClickHandler(){
        Intent passIntent = new Intent(MainActivity.this, ViewAllCourses.class);
        startActivity(passIntent);

    }


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        dbHelper = new DatabaseHelper(this);
        dbHelper.getWritableDatabase();
        dbHelper.createTermTable("term_table");
        dbHelper.createAssessmentTable("assessment_table");
        dbHelper.createMentorTable("mentor_table");
        dbHelper.createNotesTable("note_table");
        dbHelper.createCourseTable("course_table");




    }



}
