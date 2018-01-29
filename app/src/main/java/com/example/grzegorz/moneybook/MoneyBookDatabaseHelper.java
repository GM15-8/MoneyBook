package com.example.grzegorz.moneybook;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;

/**
 * Created by Grzegorz on 2018-01-17.
 */

public class MoneyBookDatabaseHelper extends SQLiteOpenHelper{
    private final static String DB_NAME="moneyBookTest6";
    private static final int DB_VERSION=1;

    MoneyBookDatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db,0,DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDatabase(db,oldVersion,newVersion);
    }

    private void updateDatabase(SQLiteDatabase db,int oldVersion,int newVersion){

        //first creation database
        //zadbac o szczegoly typu refwydatku dla budzetu nie moze byc pustym polem itp.
        if(oldVersion<1){

            String create="CREATE TABLE LOAN (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "VALUE REAL NOT NULL," +
                    "VALUE_TO_GET_BACK REAL NOT NULL," +
                    "DATE_GENERATION NUMERIC NOT NULL," +
                    "DATE_FIRST_PAYMENT NUMERIC NOT NULL," +
                    "DATE_OF_EXECUTION NUMERIC NOT NULL," +
                    "COUNT_PAYMENT INTEGER NOT NULL," +
                    "IS_ENDING NUMERIC NOT NULL," +
                    "IS_ANNULED NUMERIC NOT NULL," +
                    "REF_PERMAMENT_PAYMENT INTEGER);";
            db.execSQL(create);

            String[] categoryTable={"Inne","Rachunki","Żywność","Chemia","Higiena os.","Odzież","Rozrywka","Sport","Art. domowe","Samochód","Zwierzęta","Rachunki"};
            String category;
            StringBuilder stringBuilder=new StringBuilder();

            for(int i=0;i<categoryTable.length;i++){
                stringBuilder.append("CATEGORY='");
                stringBuilder.append(categoryTable[i]);
                if(i<categoryTable.length-1) {
                    stringBuilder.append("' OR ");
                }
                else{
                    stringBuilder.append("'");
                }
            }
            create="CREATE TABLE PAYMENT (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT NOT NULL,"+
                    "DESCRIPTION TEXT NOT NULL,"+
                    "CATEGORY TEXT CHECK ("+stringBuilder.toString()+") NOT NULL,"+
                    "VALUE REAL NOT NULL," +
                    "DATE_GENERATION NUMERIC NOT NULL," +
                    "DATE_OPERATION NUMERIC NOT NULL," +
                    "IS_ANNULED NUMERIC NOT NULL," +
                    "IS_DONE NUMERIC NOT NULL);";
            db.execSQL(create);

            create="CREATE TABLE PERMAMENT_PAYMENT (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "DATE_GENERATION NUMERIC NOT NULL," +
                    "DATE_FIRST_OPERATION NUMERIC NOT NULL," +
                    "PERIOD TEXT CHECK (PERIOD='DAY' OR PERIOD='WEEK')," +
                    "PERIOD_COUNT INTEGER NOT NULL,"+
                    "IS_ACTIVE NUMERIC NOT NULL," +
                    "ALARM NUMERIC NOT NULL);";
            db.execSQL(create);

            create="CREATE TABLE BUDGET (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "VALUE REAL NOT NULL," +
                    "SAVINGS_VALUE REAL NOT NULL," +
                    "REF_PAYMENT INTEGER," +
                    "REF_PREV_BUDGET INTEGER);";
            db.execSQL(create);

            insertBudget(db,2345.67,300);
            insertBudget(db,1222.27,300);
            insertPayment(db,"testBD","testowaBD","Chemia",23.47,"2018-01-21");
            insertPayment(db,"testBD2","testowaBD2","Inne",213.47,"2018-01-22");
            insertPayment(db,"testBD3","testowaBD3","Żywność",3.47,"2018-01-19");
        }
        //update database...
    }
    private void insertBudget(SQLiteDatabase db,double value,double savingValue){
        ContentValues contentValues=new ContentValues();
        contentValues.put("VALUE",value);
        contentValues.put("SAVINGS_VALUE",savingValue);
        db.insert("BUDGET",null,contentValues);

    }

    private void insertPayment(SQLiteDatabase db,String name,String description,String category,double value,String date){
        ContentValues paymentValues=new ContentValues();
        paymentValues.put("NAME",name);
        paymentValues.put("DESCRIPTION",description);
        paymentValues.put("VALUE",value);
        paymentValues.put("CATEGORY",category);
        paymentValues.put("DATE_GENERATION",date);
        paymentValues.put("DATE_OPERATION",date);
        paymentValues.put("IS_ANNULED",false);
        paymentValues.put("IS_DONE",true);
        db.insert("PAYMENT",null,paymentValues);
    }
}
