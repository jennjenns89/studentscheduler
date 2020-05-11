package com.example.studentscheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.studentscheduler.assessments.Assessment;
import com.example.studentscheduler.courses.Course;
import com.example.studentscheduler.mentor.Mentor;
import com.example.studentscheduler.notes.Note;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.example.studentscheduler.TermsAdapter.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudentDB.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onOpen(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL("PRAGMA foreign_keys = ON;");

    }
    public void deleteTable(String tableName){
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    //...................Create table methods.............................................
    public void createTermTable(String tableName){
        this.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "(term_id INTEGER " +
                "PRIMARY KEY AUTOINCREMENT, term_name TEXT, start_date DATE, end_date DATE)");
    }

    public void createCourseTable(String tableName){
        this.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "(course_id INTEGER " +
                "PRIMARY KEY AUTOINCREMENT, course_name TEXT, start_date DATE, possible_end_date DATE," +
                " status TEXT, term_id INTEGER, assess_id INTEGER, mentor_id INTEGER, note_id INTEGER," +
                " CONSTRAINT fk_terms FOREIGN KEY (term_id) REFERENCES term_table(term_id))");
    }


    public void createAssessmentTable(String tableName){
        this.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "(assess_id INTEGER " +
                "PRIMARY KEY AUTOINCREMENT, assess_name TEXT, start_date DATE, goal_date DATE, assess_type TEXT, course_id INTEGER," +
                " CONSTRAINT fk_courses FOREIGN KEY (course_id) REFERENCES course_table(course_id) ON DELETE CASCADE)");
    }

    public void createMentorTable(String tableName){
        this.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "(mentor_id INTEGER " +
                "PRIMARY KEY AUTOINCREMENT, mentor_name TEXT, mentor_ph_num TEXT, mentor_email TEXT, course_id INTEGER," +
                "CONSTRAINT fk_courses FOREIGN KEY (course_id) REFERENCES course_table(course_id) ON DELETE CASCADE)");
    }

    public void createNotesTable(String tableName){
        this.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "(note_id INTEGER " +
                "PRIMARY KEY AUTOINCREMENT, note_title TEXT, course_id INTEGER, note_item VARCHAR(300)," +
                "date_of_note DATE," +
                "CONSTRAINT fk_courses FOREIGN KEY (course_id) REFERENCES course_table(course_id) ON DELETE CASCADE)");
    }




    public String checkForRows(String sqlStatement){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);
        if(cursor != null && cursor.moveToFirst()){
            cursor.moveToFirst();
            if(cursor.getInt(0) == 0){
                return "false";
            }
            else{
                return "true";
            }
        }


        return "false";
    }
    //..............................Term methods..................................
    public boolean addTermRecord(String nameKey, String nameValue, String startDateKey, String startDateValue, String endDateKey, String endDateValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(nameKey, nameValue);
        values.put(startDateKey, startDateValue);
        values.put(endDateKey , endDateValue);

        long result = db.insert("term_table", null, values);
        if(result == -1){
            return false;
        }
        return true;
    }


    public boolean addTermRecordWithID(String idKey, int id, String nameKey, String nameValue, String startDateKey, String startDateValue, String endDateKey, String endDateValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(idKey, id);
        values.put(nameKey, nameValue);
        values.put(startDateKey, startDateValue);
        values.put(endDateKey , endDateValue);

        long result = db.insert("term_table", null, values);
        if(result == -1){
            return false;
        }
        return true;
    }

    public ArrayList<Term> readRecords(String sqlStmnt){
        ArrayList<Term> allTerms = new ArrayList<Term>();
        int term_id;
        String term_name;
        String start_date;
        String end_date;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStmnt, null);

        while(cursor.moveToNext()){
            term_id = cursor.getInt(cursor.getColumnIndex("term_id"));
            term_name = cursor.getString(cursor.getColumnIndex("term_name"));
            start_date = cursor.getString(cursor.getColumnIndex("start_date"));
            end_date = cursor.getString(cursor.getColumnIndex("end_date"));


            allTerms.add(new Term(term_id, term_name, start_date, end_date));

        }

        return allTerms;
    }

    public int changeRecord(String tblName, String name, String startDate, String endDate, String whereClause, String[] whereArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("term_name", name);
        cv.put("start_date", startDate);
        cv.put("end_date", endDate);

        return db.update(tblName, cv, whereClause, whereArgs);

    }

    public boolean checkForCourses(String sqlStmnt){
        int countOfRows;


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStmnt, null);
        countOfRows = cursor.getCount();
        Log.d(TAG, " countOf Rows is: " + countOfRows);

        if(countOfRows > 0){
            return true;
        }

        return false;
    }



    //....................Course methods...........................
    public boolean addCourseRecord(String nameKey, String nameValue, String startDateKey,
                                   String startDateValue, String possEndDateKey, String possEndDateValue,
                                   String statusKey, String statusValue, String termIdKey, int termId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(nameKey, nameValue);
        values.put(startDateKey, startDateValue);
        values.put(possEndDateKey, possEndDateValue);
        values.put(statusKey, statusValue);
        values.put(termIdKey, termId);

        long result = db.insert("course_table", null, values);
        if(result == -1){
            return false;
        }
        return true;



    }

    public ArrayList<Course> readCourseRecords(String sqlStmnt){

        ArrayList<Course> allCourses = new ArrayList<>();
        int course_id;
        String course_name;
        String start_date;
        String poss_end_date;
        String status;
        int term_id;


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStmnt, null);

        while(cursor.moveToNext()){
            course_id = cursor.getInt(cursor.getColumnIndex("course_id"));
            course_name = cursor.getString(cursor.getColumnIndex("course_name"));
            start_date = cursor.getString(cursor.getColumnIndex("start_date"));
            poss_end_date = cursor.getString(cursor.getColumnIndex("possible_end_date"));
            status = cursor.getString(cursor.getColumnIndex("status"));
            term_id = cursor.getInt(cursor.getColumnIndex("term_id"));






            allCourses.add(new Course(course_id, course_name, start_date, poss_end_date, status, term_id));

        }

        return allCourses;



    }

    public int changeCourseRecord(String tblName, String name, String startDate, String endDate, String status, String whereClause, String[] whereArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("Course_name", name);
        cv.put("start_date", startDate);
        cv.put("possible_end_date", endDate);
        cv.put("status", status);

        return db.update(tblName, cv, whereClause, whereArgs);

    }

    public boolean addCourseRecordWithID(String idKey, int course_id, String nameKey, String nameValue,
                                         String startDateKey, String startDateValue, String endDateKey,
                                         String endDateValue, String statusKey, String status,
                                         String termIdKey, int term_id){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(idKey, course_id);
        values.put(nameKey, nameValue);
        values.put(startDateKey, startDateValue);
        values.put(endDateKey , endDateValue);
        values.put(statusKey, status);
        values.put(termIdKey, term_id);

        long result = db.insert("course_table", null, values);
        if(result == -1){
            return false;
        }
        return true;
    }

    //.......................Mentor Methods..................................
    public boolean addMentorRecord(String nameKey, String nameValue, String phoneNumKey, String phoneNum,
                                   String emailKey, String email, String courseIdKey, int courseId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(nameKey, nameValue);
        values.put(phoneNumKey, phoneNum);
        values.put(emailKey, email);
        values.put(courseIdKey, courseId);


        long result = db.insert("mentor_table", null, values);
        if(result == -1){
            return false;
        }
        return true;

    }

    public ArrayList<Mentor> readMentorRecords(String sqlStmnt){

        ArrayList<Mentor> allMentors = new ArrayList<>();
        int mentor_id;
        String mentor_name;
        String mentor_phone_num;
        String mentor_email;
        int course_id;


        SQLiteDatabase db = this.getWritableDatabase();
        try (Cursor cursor = db.rawQuery(sqlStmnt, null)) {

            while (cursor.moveToNext()) {
                mentor_id = cursor.getInt(cursor.getColumnIndex("mentor_id"));
                mentor_name = cursor.getString(cursor.getColumnIndex("mentor_name"));
                mentor_phone_num = cursor.getString(cursor.getColumnIndex("mentor_ph_num"));
                mentor_email = cursor.getString(cursor.getColumnIndex("mentor_email"));
                course_id = cursor.getInt(cursor.getColumnIndex("course_id"));


                allMentors.add(new Mentor(mentor_id, mentor_name, mentor_phone_num, mentor_email, course_id));

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            close();
        }

        return allMentors;

    }

    public int changeMentorRecord(String tblName, String name, String phone, String email, String whereClause, String[] whereArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("mentor_name", name);
        cv.put("mentor_ph_num", phone);
        cv.put("mentor_email", email);


        return db.update(tblName, cv, whereClause, whereArgs);

    }

//.......................Note Methods................................................
public boolean addNoteRecord(String titleKey, String titleValue, String courseIdKey, int courseId,
                               String noteItemKey, String noteItem, String dateKey, String noteDate){
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();

    values.put(titleKey, titleValue);
    values.put(courseIdKey, courseId);
    values.put(noteItemKey, noteItem);
    values.put(dateKey, noteDate);


    long result = db.insert("note_table", null, values);
    if(result == -1){
        return false;
    }
    return true;

}

    public ArrayList<Note> readNoteRecords(String sqlStmnt){

        ArrayList<Note> allNotes = new ArrayList<>();
        int note_id;
        String note_title;
        int course_id;
        String note_item;
        String date_of_note;


        SQLiteDatabase db = this.getWritableDatabase();
        try (Cursor cursor = db.rawQuery(sqlStmnt, null)) {

            while (cursor.moveToNext()) {
                note_id = cursor.getInt(cursor.getColumnIndex("note_id"));
                note_title = cursor.getString(cursor.getColumnIndex("note_title"));
                course_id = cursor.getInt(cursor.getColumnIndex("course_id"));
                note_item = cursor.getString(cursor.getColumnIndex("note_item"));
                date_of_note = cursor.getString(cursor.getColumnIndex("date_of_note"));


                allNotes.add(new Note(note_id, note_title, note_item, course_id, date_of_note));

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            close();
        }

        return allNotes;

    }

    public int changeNoteRecord(String tblName, String title, String note_body, String date_of_note,
                                String whereClause, String[] whereArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("note_title", title);
        cv.put("note_item", note_body);
        cv.put("date_of_note", date_of_note);


        return db.update(tblName, cv, whereClause, whereArgs);

    }

    public boolean addNoteRecordWithID(String idKey, int note_id, String titleKey, String titleValue,
                                       String courseIdKey, int course_id, String bodyKey, String bodyValue,
                                       String dateKey, String dateValue){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(idKey, note_id);
        values.put(titleKey, titleValue);
        values.put(courseIdKey, course_id);
        values.put(bodyKey, bodyValue);
        values.put(dateKey, dateValue);


        long result = db.insert("note_table", null, values);
        if(result == -1){
            return false;
        }
        return true;
    }

    //..............................Assessment Methods................................
    public ArrayList<Assessment> readAssessmentRecords(String sqlStmnt){
        ArrayList<Assessment> allAssessments = new ArrayList<>();
        int assess_id;
        String assess_name;
        String start_date;
        String goal_date;
        String assess_type;
        int course_id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStmnt, null);

        while(cursor.moveToNext()){
            assess_id = cursor.getInt(cursor.getColumnIndex("assess_id"));
            assess_name = cursor.getString(cursor.getColumnIndex("assess_name"));
            start_date = cursor.getString(cursor.getColumnIndex("start_date"));
            goal_date = cursor.getString(cursor.getColumnIndex("goal_date"));
            assess_type = cursor.getString(cursor.getColumnIndex("assess_type"));
            course_id = cursor.getInt(cursor.getColumnIndex("course_id"));


            allAssessments.add(new Assessment(assess_id, assess_name, start_date, goal_date, assess_type, course_id));

        }

        return allAssessments;
    }

    public boolean addAssessmentRecord(String titleKey, String titleValue, String goalDateKey, String goalDateValue,
                                       String startDateKey, String startDateValue, String typeKey, String assess_type,
                                       String courseIdkey, int courseIdValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(titleKey, titleValue);
        values.put(goalDateKey, goalDateValue);
        values.put(startDateKey, startDateValue);
        values.put(typeKey,assess_type);
        values.put(courseIdkey, courseIdValue);

        long result = db.insert("assessment_table", null, values);
        if(result == -1){
            return false;
        }
        return true;

    }

    public int changeAssessmentRecord(String tblName, String name, String start_date, String goal_date, String assess_type,
                                String whereClause, String[] whereArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("assess_name", name);
        cv.put("start_date", start_date);
        cv.put("goal_date", goal_date);
        cv.put("assess_type", assess_type);



        return db.update(tblName, cv, whereClause, whereArgs);

    }

    public boolean addAssessmentRecordWithID(String idKey, int assess_id, String nameKey, String nameValue,
                                       String startIdKey, String start_date, String goalIdKey,
                                             String goal_date, String typeKey, String assess_type,
                                             String course_idKey, int course_idValue){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(idKey, assess_id);
        values.put(nameKey, nameValue);
        values.put(startIdKey, start_date);
        values.put(goalIdKey, goal_date);
        values.put(typeKey,assess_type);
        values.put(course_idKey, course_idValue);


        long result = db.insert("assessment_table", null, values);
        if(result == -1){
            return false;
        }
        return true;
    }

    public int checkForAmountOfRows(String sqlStmnt){
        int countOfRows;


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStmnt, null);
        countOfRows = cursor.getCount();
        Log.d(TAG, " countOfRows is: " + countOfRows);


        return countOfRows;
    }



    public int removeRecord(String tableName, String whereClause, String[] whereArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(tableName, whereClause, whereArgs);

    }



}
