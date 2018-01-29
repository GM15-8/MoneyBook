package com.example.grzegorz.moneybook;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

//platnosc,historia,statystyki-aktywnosc portfel,skarbonka, - fragmenty
public class MainActivity extends Activity {
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private MainInfoAboutTransaction mainInfoAboutTransaction;
    private class DrawerItemClickedListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem((int)id);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerList=(ListView)findViewById(R.id.drawerList);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,getResources().getStringArray(R.array.menuArray));
        drawerList.setAdapter(adapter);
        drawerList.setOnItemClickListener(new DrawerItemClickedListener());
        //Toast.makeText(this,"blabla",Toast.LENGTH_LONG).show();
        if(savedInstanceState==null) {
            addFragment();
        }


        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open_drawer,R.string.close_drawer){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

    }


    private void addFragment(){
        mainInfoAboutTransaction=new MainInfoAboutTransaction();
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(R.id.mainInfoContainer,mainInfoAboutTransaction);
        ft.addToBackStack("info");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch(item.getItemId()){
            case R.id.newTransaction:
                intent=new Intent(this,AddTransactionActivity.class);
                startActivity(intent);
                return true;
            case R.id.wallet:
                intent=new Intent(this,WalletActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isDrawerOpen=drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.wallet).setVisible(!isDrawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void selectItem(int position){
        Intent intent;
        switch(position){
            case 0:
                break;
            case 1:
                intent=new Intent(this,WalletActivity.class);
                startActivity(intent);
                break;
            case 2:
                break;
            case 3:
                intent=new Intent(this,AddTransactionActivity.class);
                startActivity(intent);
                break;
            case 4:
                break;
            default:
                break;

        }
        drawerLayout.closeDrawer(drawerList);

    }
}
