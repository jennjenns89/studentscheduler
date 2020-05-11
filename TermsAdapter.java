package com.example.studentscheduler;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;


import com.example.studentscheduler.courses.CourseCreator;
import com.example.studentscheduler.courses.CourseViewer;
import com.google.android.material.snackbar.Snackbar;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermsAdapter extends RecyclerView.Adapter<TermsAdapter.ViewHolder> {
    private final List<Term> terms;
    private List<Term> testTerm;
    private final Context myContext;
    private OnTermListener mOnTermListener;
    String tempStart;
    String tempEnd;
    String tempName;
    public final static String TAG = "TermsAdapter";



    @Override
    public int getItemCount () {
        return terms.size();
    }

    public TermsAdapter(List<Term> terms, Context myContext, OnTermListener onTermListener){
        this.terms = terms;
        this.myContext = myContext;
        this.mOnTermListener = onTermListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.term_list_item, parent, false);
        return new ViewHolder(view, mOnTermListener);

    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Term term = terms.get(position);
        holder.mTextView.setText(term.getText());
        DatabaseHelper dbHelper = new DatabaseHelper(myContext);

        holder.mOptions.setOnClickListener(view -> {


            //creating a popup menu
            PopupMenu popup = new PopupMenu(myContext, holder.mOptions);
            //inflating menu from xml resource
            popup.inflate(R.menu.options_menu);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.viewMenu:
                        //handle view term click
                        Intent intent = new Intent(myContext, CourseViewer.class);
                        int id = term.getTerm_id();
                        String termName = term.getTerm_name();
                        String strtDate = term.getStart_date();
                        String endDate = term.getEnd_date();

                        intent.putExtra("term_id", id);
                        intent.putExtra("term_name", termName);
                        myContext.startActivity(intent);

                        break;
                    case R.id.editMenu:
                        //Get the information needed to send to term editor
                        id = term.getTerm_id();
                        termName = term.getTerm_name();
                        strtDate = term.getStart_date();
                        endDate = term.getEnd_date();

                        //handle edit Menu click
                        intent = new Intent(myContext, TermEditor.class);
                        //send id, term name, start date and end date
                        intent.putExtra("termId", id);
                        intent.putExtra("termName", termName);
                        intent.putExtra("startDate", strtDate);
                        intent.putExtra("endDate", endDate);

                        myContext.startActivity(intent);

                        break;
                    case R.id.deleteMenu:
                        //handle delete option

                        Intent deleteIntent = new Intent(myContext, DummyDelete.class);
                        String strID;
                        int termID;
                        termName = term.getTerm_name();
                        termID = term.getTerm_id();
                        String deleteMessage = "Term " + termName + " deleted";
                        String cannotDelete = "Term " + termName + " cannot be deleted, has courses";
                        String undoClick = "UNDO";
                        int duration = Snackbar.LENGTH_LONG;

                        //termporary variables in case user decides they don't want to delete term item
                        int tempId = term.getTerm_id();
                        tempName = term.getTerm_name();
                        tempStart = term.getStart_date();
                        tempEnd = term.getEnd_date();

                        //make sure the start and end dates are in the correct format
                        SimpleDateFormat formatDate = new SimpleDateFormat("mm/dd/yyyy", Locale.US);
                        Date formattedSDate = new Date();
                        Date formattedEDate = new Date();

                        try{
                            formattedSDate = formatDate.parse(tempStart);
                            formattedEDate = formatDate.parse(tempEnd);

                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //format to mm/dd/yyyy for the user
                        tempStart = new SimpleDateFormat("yyyy-mm-dd", Locale.US).format(formattedSDate);
                        tempEnd = new SimpleDateFormat("yyyy-mm-dd", Locale.US).format(formattedEDate);




                        //make the termID integer into a string
                        strID = Integer.toString(termID);
                        //put it in where args to be used in delete statement
                        String[] whereArgs = {strID};

                        //use this method to check and see if there are any courses attatched to the
                        //current term we are trying to delete
                        boolean areRows = dbHelper.checkForCourses("SELECT t.term_id " +
                                "FROM course_table c JOIN term_table t " +
                                "ON c.term_id = " + termID);
                        //if there are courses attatched then let the user know it cannot be deleted
                        if(areRows){
                            Snackbar.make(view, cannotDelete, duration).show();
                        }
                        //if there are no courses attatched remove the term
                        else {
                            //remove term
                            dbHelper.removeRecord("term_table", "term_id = ?", whereArgs);

                            //Create snackbar with option to undo delete
                            Snackbar.make(view, deleteMessage, duration)
                                    .setAction(undoClick, v -> {
                                        //if user clicks "UNDO" put term back with same id
                                        dbHelper.addTermRecordWithID("term_id", tempId, "term_name", tempName,
                                                "start_date", tempStart, "end_date", tempEnd);
                                        //this will allow the termviewer recyclerview to update by first
                                        //going to a dummy activity then going back into termviewer
                                        myContext.startActivity(deleteIntent);
                                    }).show();
                        }
                        //this will allow the termviewer recyclerview to update by first
                        //going to a dummy activity then going back into termviewer
                        myContext.startActivity(deleteIntent);



                //for switch
                }
                //this needs to be here for popup menu
                return false;
            //for popup menu
            });
            //displaying the popup
            popup.show();



        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.term_item)
        TextView mTextView;
        TextView mOptions;
        OnTermListener onTermListener;

        public ViewHolder(@NonNull View itemView, OnTermListener onTermListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.onTermListener = onTermListener;
            itemView.setOnClickListener(this);
            mOptions = itemView.findViewById(R.id.textViewOptions);
        }

        @Override
        public void onClick(View view) {
            onTermListener.onTermClick(getAdapterPosition());
        }
    }


    public interface OnTermListener {
        void onTermClick(int position);
    }
}

