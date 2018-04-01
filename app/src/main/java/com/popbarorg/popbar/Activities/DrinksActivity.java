package com.popbarorg.popbar.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.popbarorg.popbar.Data.AppDatabase;
import com.popbarorg.popbar.Fragments.AllDrinksFragment;
import com.popbarorg.popbar.R;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.ReActiveConfig;
import com.reactiveandroid.internal.database.DatabaseConfig;

public class DrinksActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private  Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinks);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        DatabaseConfig appDatabaseConfig = new DatabaseConfig.Builder(AppDatabase.class)
                .build();

        ReActiveAndroid.init(new ReActiveConfig.Builder(this)
                .addDatabaseConfigs(appDatabaseConfig)
                .build());

         fragmentManager = getSupportFragmentManager();
         fragment = fragmentManager.findFragmentById(R.id.myContainer);

        if(fragment == null){

            fragment = new AllDrinksFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.myContainer, fragment)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

}
