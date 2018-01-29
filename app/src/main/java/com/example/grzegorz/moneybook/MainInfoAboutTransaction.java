package com.example.grzegorz.moneybook;


import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainInfoAboutTransaction extends Fragment {
    private SeekBar periodSeekBar;
    private TextView periodText;
    private TextView infoAboutLastTransaction;
    private int period;
    private static final String PERIOD_EXTRA="period";

    private class getInfoTask extends AsyncTask<Void,Void,Boolean>{
        private String infoAboutLastTransactionString;
        List<String> name=new LinkedList();
        List<String> description=new LinkedList();
        List<String> category=new LinkedList();
        List<Double> value=new LinkedList();
        List<String> date=new LinkedList();
        private int countRow=0;
        private SQLiteOpenHelper moneyBookDatabaseHelper;
        private StringBuilder stringBuilder;
        private String periodDate;
        private String currentDate;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //infoAboutLastTransaction=(TextView)getView().findViewById(R.id.infoAboutLastTransaction);
            moneyBookDatabaseHelper=new MoneyBookDatabaseHelper(getView().getContext());
            stringBuilder=new StringBuilder();
            stringBuilder.append("Ostanie transakcje:\n");
            periodDate=createPeriodDate();
            currentDate=getCurrentDate();
            //Toast.makeText(getView().getContext(), periodDate+":"+currentDate, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                //infoAboutLastTransaction.setText(infoAboutLastTransactionString);
                initTable();
            }
            else{
                try {
                    Toast.makeText(getView().getContext(), "BD niedostępna", Toast.LENGTH_SHORT).show();
                }
                catch(NullPointerException e){
                    //sth for it..
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(period>0) {
                try {
                    SQLiteDatabase db = moneyBookDatabaseHelper.getReadableDatabase();
                /*Cursor cursor = db.query("BUDGET", new String[]{"VALUE", "SAVINGS_VALUE"}, null,null, null, null, "_id
                if (cursor.moveToFirst()) {
                    value = cursor.getDouble(0);
                    valueSaving = cursor.getDouble(1);
                    infoAboutLastTransactionString = String.format("Budżet:%f, Oszczędności:%f", value, valueSaving);
                }*/
                    Cursor cursor = db.query("PAYMENT", new String[]{"NAME", "DESCRIPTION", "CATEGORY", "VALUE", "DATE_OPERATION"}, "DATE_OPERATION>=? AND DATE_OPERATION<=?", new String[]{periodDate, currentDate}, null, null, "DATE_OPERATION DESC");
                    while(cursor.moveToNext()) {
                        name.add( cursor.getString(0));
                        description.add(cursor.getString(1));
                        category.add(cursor.getString(2));
                        value.add(cursor.getDouble(3));
                        date.add(cursor.getString(4));
                        countRow++;
                    }

                    return true;
                } catch (SQLiteException e) {
                    return false;
                }
            }
            return true;
        }
        private String createPeriodDate(){
            String date;
            Date currentDate= new Date();
            Date newDate=new Date(currentDate.getTime() - (long)1000*60*60*24*period); //warning, too big !
            date=new SimpleDateFormat("yyyy-MM-dd").format(newDate);
            return date;
        }
        private String getCurrentDate(){
            String date;
            Date currentDate= new Date();
            Date newDate=new Date(currentDate.getTime());
            date=new SimpleDateFormat("yyyy-MM-dd").format(newDate);
            return date;
        }
        private void initTable(){

            TableLayout tableLayout=(TableLayout)getView().findViewById(R.id.tablePayment);
            //we have to delete every existing table row :
            int count=tableLayout.getChildCount();
            for(int i=0;i<count;i++){
                View child=tableLayout.getChildAt(i);
                if(child instanceof TableRow){
                    ((TableRow) child).removeAllViews();
                }
            }
            TableRow tableRow;
            Iterator iteratorName=name.iterator();
            Iterator iteratorDescription=description.iterator();
            Iterator iteratorCategory=category.iterator();
            Iterator iteratorValue=value.iterator();
            Iterator iteratorDate=date.iterator();
                //stringBuilder.append(String.format("%s %s %s %.2f %s \n", name, description, category, value, date));
            for(int i=0;i<countRow;i++) {
                tableRow = new TableRow(getView().getContext());
                initView(tableRow, iteratorName.next().toString());
                initView(tableRow, iteratorDescription.next().toString());
                initView(tableRow, iteratorCategory.next().toString());
                initView(tableRow, iteratorValue.next().toString());
                initView(tableRow, iteratorDate.next().toString());
                tableLayout.addView(tableRow);
            }
            //infoAboutLastTransactionString = stringBuilder.toString();


        }
        private void initView(TableRow tableRow,String text){
            TextView nameView=new TextView(getView().getContext());
            nameView.setText(text);
            nameView.setTextColor(Color.DKGRAY);
            nameView.setGravity(Gravity.CENTER);
            tableRow.addView(nameView);
        }
    }


    public MainInfoAboutTransaction() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(savedInstanceState!=null){
            period=savedInstanceState.getInt(PERIOD_EXTRA);
        }
        return inflater.inflate(R.layout.fragment_main_info_about_transaction, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PERIOD_EXTRA,period);

    }


    @Override
    public void onStart() {
        super.onStart();
        View view=getView();
        if(view!=null){
            initializeView(view);
            createSeekBarListener();
            new getInfoTask().execute();
            updateText(period);
        }
    }

    private void initializeView(View view){
        periodSeekBar=(SeekBar)view.findViewById(R.id.periodSeekBar);
        periodText=(TextView) view.findViewById(R.id.periodText);
    }
    private void createSeekBarListener(){
        periodSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateText(progress);
                period=progress;
                new getInfoTask().execute();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }


        });
    }
    private void updateText(int value){
        String text=String.format("Obecny zakres to:%d dni",value);
        periodText.setText(text);

    }
}
