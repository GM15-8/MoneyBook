package com.example.grzegorz.moneybook;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class AddTransactionActivity extends Activity {
    private static EditText date;
    //private static String textDate;
    private class addPaymentTask extends AsyncTask<Void,Void,Boolean>{
        SQLiteOpenHelper moneyBookDatabaseHelper;
        SQLiteDatabase db;
        EditText name,description,value;
        Spinner category;
        String textName,textDescripton,textCategory;
        Double textValue;
        String textDate,textCurrentDate;
        Boolean isAnnuled=false;
        Boolean isDone=true;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String[] dateSeparate=new String[3];
            String text;
            text=date.getText().toString();
            dateSeparate=text.split("/");
            if(dateSeparate[1].length()<2){
                dateSeparate[1]="0"+dateSeparate[1];
            }
            if(dateSeparate[0].length()<2){
                dateSeparate[0]="0"+dateSeparate[0];
            }
            moneyBookDatabaseHelper=new MoneyBookDatabaseHelper(AddTransactionActivity.this);
            name=(EditText)findViewById(R.id.name);
            description=(EditText)findViewById(R.id.description);
            value=(EditText)findViewById(R.id.value);
            category=(Spinner)findViewById(R.id.paymentCategory);
            textName=name.getText().toString();
            textDescripton=description.getText().toString();
            textCategory=String.valueOf(category.getSelectedItem());
            textValue=Double.valueOf(value.getText().toString());
            textDate=dateSeparate[2]+"-"+dateSeparate[1]+"-"+dateSeparate[0];
            textCurrentDate=newStringDate();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Toast.makeText(AddTransactionActivity.this,"Wydatek zapisany w BD",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(AddTransactionActivity.this,"Wystąpił błąd",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                db=moneyBookDatabaseHelper.getWritableDatabase();
                ContentValues paymentValues=new ContentValues();
                paymentValues.put("NAME",textName);
                paymentValues.put("DESCRIPTION",textDescripton);
                paymentValues.put("VALUE",textValue);
                paymentValues.put("CATEGORY",textCategory);
                paymentValues.put("DATE_GENERATION",textCurrentDate);
                paymentValues.put("DATE_OPERATION",textDate);
                paymentValues.put("IS_ANNULED",isAnnuled);
                paymentValues.put("IS_DONE",isDone);
                db.insert("PAYMENT",null,paymentValues);
                db.close();
                return true;
            }
            catch(SQLiteException e){
                return false;
            }
        }
    }
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year,month,day;
            final Calendar calendar=Calendar.getInstance();
            year=calendar.get(Calendar.YEAR);
            month=calendar.get(Calendar.MONTH);
            day=calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(),this,year,month,day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String dateText=String.format("%d/%d/%d",dayOfMonth,month+1,year);
            date.setText(dateText);
            //textDate=String.format("%d-%d-%d",year,month+1,dayOfMonth);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        createPrevButton();
        getActionBar().setTitle(getResources().getString(R.string.addTransaction_name));
        initializeVariables();
        setCurrentDate();

    }
    private void initializeVariables(){
        date=(EditText)findViewById(R.id.date);
    }

    private void createPrevButton(){
        ActionBar actionBar=getActionBar();
        actionBar.setHomeButtonEnabled(true);
    }
    private void setCurrentDate(){
        int year,month,day;
        final Calendar calendar=Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        day=calendar.get(Calendar.DAY_OF_MONTH);
        String dateText=String.format("%d/%d/%d",day,month+1,year);
        date.setText(dateText);
    }

    public void changeDateOnClick(View view){
        DialogFragment dateFragment=new DatePickerFragment();
        dateFragment.show(getFragmentManager(),getResources().getString(R.string.date_picker));
    }

    public void confirmPaymentOnClick(View view){
        new addPaymentTask().execute();
    }

    public void cleanPaymentOnClick(View view){
        this.finish();
        Intent intent=new Intent(AddTransactionActivity.this,AddTransactionActivity.class);
        startActivity(intent);
    }

    public void annuledPaymentOnClick(View view){
        this.finish();
    }

    private static String newStringDate(){
        Calendar calendar=Calendar.getInstance();
        int day,month,year;
        day=calendar.get(Calendar.DAY_OF_MONTH);
        month=calendar.get(Calendar.MONTH);
        year=calendar.get(Calendar.YEAR);
        return String.format("%d-%d-%d",year,month+1,day);
    }
}
