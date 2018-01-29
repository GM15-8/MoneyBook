package com.example.grzegorz.moneybook;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

public class WalletActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ActionBar actionBar=getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.wallet_name));
    }
}
