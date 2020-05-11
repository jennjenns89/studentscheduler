package com.example.studentscheduler;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import com.example.studentscheduler.DividerItemDecoration;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TermViewer extends AppCompatActivity implements TermsAdapter.OnTermListener {


    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    DatabaseHelper dbHelp;

    private List<Term> terms = new ArrayList<>();
    private TermsAdapter termAdapter;


    public final static String TAG = "TermViewer";

    @OnClick(R.id.addTerm)
    void creatorClickHandler() {
        Intent intent = new Intent(this, TermCreator.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_viewer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        dbHelp = new DatabaseHelper(this);
        dbHelp.getWritableDatabase();

        ButterKnife.bind(this);
        //Create a string object that will hold true or false
        String rows;
        //this will check if there are any rows in the terms table
        rows = dbHelp.checkForRows("SELECT COUNT(*) FROM term_table");

        //This text will display if there are no terms available to show in the recyclerview
        TextView addTerm = findViewById(R.id.AddTermsText);
        Log.d(TAG, "terms has " + terms.size() + " terms");
        //If there are no rows available send a message to the user to add terms
        if (rows.equals("false")) {
            addTerm.setVisibility(View.VISIBLE);

        }
        else{
            addTerm.setVisibility(View.INVISIBLE);
            //Use this method to create the recyclerview objects
            createRecyclerView();
        }

        /*
        int size = terms.size();
        try {

            if(size > 0){
                terms.clear();
            }


            terms = dbHelp.readRecords("SELECT * FROM term_table");
            for (Term oneTerm: terms) {
                terms.add(new Term(oneTerm.getTerm_id(), oneTerm.getTerm_name(),
                        oneTerm.getStart_date(), oneTerm.getEnd_date()));
            }

        } catch (Exception e) {
            //Log.d(TAG, e.getMessage());
        }
        for(Term oneTerm: terms) {
            Log.d(TAG, " " + oneTerm);
        }
        Log.d(TAG, "number of items in terms (onCreate): " + terms.size());
*/

        //FloatingActionButton addTerm = findViewById(R.id.addTerm);
        //addTerm.setOnClickListener(new View.OnClickListener() {


    }

    private void createRecyclerView() {
        mRecyclerView.hasFixedSize();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        termAdapter = new TermsAdapter(terms, this, this);
        mRecyclerView.setAdapter(termAdapter);

        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    //@Override
    protected void onResume() {
        int size = terms.size();
        //This text will display if there are no terms available to show in the recyclerview
        TextView addTerm = findViewById(R.id.AddTermsText);
        //this string will show true or false for rows in the term table
        String rows;
        //this will check for rows in the term table
        rows = dbHelp.checkForRows("SELECT COUNT(*) FROM term_table");

        //if it didn't find any rows, show the textview that tells the user they can add terms
        if (rows.equals("false")) {
            addTerm.setVisibility(View.VISIBLE);
            if (size > 0) {
                terms.clear();
            }
            //Use this method to have the recyclerview object check for objects and clear
            //the last object on the screen. Otherwise, the last object remains on the screen
            //after you've deleted it.
            mRecyclerView.hasFixedSize();
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(layoutManager);

            termAdapter = new TermsAdapter(terms, this, this);
            mRecyclerView.setAdapter(termAdapter);
        }
        else {
            addTerm.setVisibility(View.INVISIBLE);
            try {
                if (size > 0) {
                    terms.clear();
                }
                terms = dbHelp.readRecords("SELECT * FROM term_table");
                for (Term oneTerm : terms) {
                    terms.add(new Term(oneTerm.getTerm_id(), oneTerm.getTerm_name(),
                            oneTerm.getStart_date(), oneTerm.getEnd_date()));
                }

            } catch (Exception ignored) {

            }
            //terms has 1 more item than it should so I take it out with this:
            terms.remove((terms.size() - 1));


            //Use this method to create the recyclerview objects
            mRecyclerView.hasFixedSize();
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(layoutManager);

            termAdapter = new TermsAdapter(terms, this, this);
            mRecyclerView.setAdapter(termAdapter);

        }
        super.onResume();
    //List<Term> terms = new ArrayList<>();

       /* try {
            terms = dbHelp.readRecords("SELECT * FROM term_table");
            for(Term oneTerm: terms){
                Log.d(TAG, oneTerm.getTerm_id() + ", " + oneTerm.getTerm_name() + ", "
                        + oneTerm.getStart_date() + ", " + oneTerm.getEnd_date() );
                terms.add(new Term(oneTerm.getTerm_id(), oneTerm.getTerm_name(),
                          oneTerm.getStart_date(), oneTerm.getEnd_date()));
            }

        }
        catch (Exception e){
            Log.d(TAG, e.getMessage());
        }

    }*/

//    public static List<Term> getTerms(){


//


//        //List<Term> terms = new ArrayList<>();


//        //terms = dbHelp.readRecords("SELECT * FROM terms_table");


//        //add term data from database here


//        terms.add(new Term());


//


//        return terms;


//    }



}


    @Override
    public void onTermClick(int position) {

    }
}
