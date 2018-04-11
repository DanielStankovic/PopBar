package com.popbarorg.popbar.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.popbarorg.popbar.Data.AppDatabase;
import com.popbarorg.popbar.Fragments.AllDrinksFragment;
import com.popbarorg.popbar.Fragments.CalculatedDrinksFragment;
import com.popbarorg.popbar.Fragments.ConfirmationDialog;
import com.popbarorg.popbar.Fragments.ContactUsFragment;
import com.popbarorg.popbar.R;
import com.popbarorg.popbar.Util.Constants;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.ReActiveConfig;
import com.reactiveandroid.internal.database.DatabaseConfig;

public class DrinksActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ConfirmationDialog.ConfirmationDialogListener {
    private FragmentManager fragmentManager;
    private  Fragment fragment;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinks);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();


        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        DatabaseConfig appDatabaseConfig = new DatabaseConfig.Builder(AppDatabase.class)
                .build();

        ReActiveAndroid.init(new ReActiveConfig.Builder(this)
                .addDatabaseConfigs(appDatabaseConfig)
                .build());

        fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentById(R.id.myContainer);


        if (savedInstanceState == null) {
            if (fragment == null) {

                fragment = new CalculatedDrinksFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.myContainer, fragment)
                        .commit();
                navigationView.setCheckedItem(R.id.calculated_drinks);
            }
        }
    }



    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {


            exitApp(getString(R.string.exit_app_popup_dialog), getString(R.string.submit), getString(R.string.cancel));

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {



        switch (item.getItemId()){
            case R.id.drink_list:

                fragment = new AllDrinksFragment();


                break;
            case R.id.calculated_drinks:

                fragment = new CalculatedDrinksFragment();

                break;

            case R.id.signout:
                signOutUser();

                break;

            case R.id.contact:
                fragment = new ContactUsFragment();
                break;

            case R.id.exit_app:
                exitApp(getString(R.string.exit_app_popup_dialog), getString(R.string.submit), getString(R.string.cancel));

                break;



        }
        fragmentManager.beginTransaction()
                .replace(R.id.myContainer, fragment)
                .commit();
        item.setChecked(true);
        setTitle(item.getTitle());
        drawerLayout.closeDrawers();
        return true;
    }

    private void exitApp(String message,String confirmButtonText, String dismissButtonText) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        ConfirmationDialog confirmationDialog = new ConfirmationDialog();
        confirmationDialog.setMessage(message);
        confirmationDialog.setConfirmButtonText(confirmButtonText);
        confirmationDialog.setDismissButtonText(dismissButtonText);
        confirmationDialog.show(fragmentTransaction, Constants.CONFIRMATION_DIALOG_TAG);

    }

    @Override
    public void onConfirmButtonClicked(DialogFragment dialogFragment) {

        finish();

    }

    private void signOutUser(){
        if(mAuth != null){
            mAuth.signOut();
            Toast.makeText(this, "Korisnik je odjavljen", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DrinksActivity.this, LoginActivity.class));
            finish();
        }else{
            Toast.makeText(this, "Došlo je do problema prilikom odjave", Toast.LENGTH_SHORT).show();
        }

    }
}
