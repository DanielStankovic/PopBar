package com.popbarorg.popbar.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.popbarorg.popbar.Fragments.ConfirmationDialog;
import com.popbarorg.popbar.R;
import com.popbarorg.popbar.Util.Constants;

public class LoginActivity extends AppCompatActivity implements ConfirmationDialog.ConfirmationDialogListener {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    EditText emailField, password;
    CardView loginButton;
    CheckBox rememberPassword;
    TextView forgotPassword;

    SharedPreferences sharedPreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        verifyPermissions();


        emailField = findViewById(R.id.emailEt);
        password = findViewById(R.id.passwordEt);
        loginButton = findViewById(R.id.loginButton);
        rememberPassword = findViewById(R.id.rememberPasswordCheckBox);
        forgotPassword = findViewById(R.id.forgotPasswordTextView);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userEmail = "Niste uneli e-mail.";
                if(mUser != null){
                    userEmail = mUser.getEmail();
                }
                showConfirmationDialog("Poslaćemo e-mail na Vašu e-mail adresu kako biste resetovali lozinku.\n" +
                        "Vaš e-mail: " + userEmail, "Potvrdi", "Otkaži");
            }
        });

        sharedPreferences = this.getSharedPreferences(Constants.SHARED_PREFERENCES_VALUE, Context.MODE_PRIVATE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(emailField.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())){


                    String email = emailField.getText().toString();
                    String pwd = password.getText().toString();


                    saveEmailAndPassValue(email, pwd, rememberPassword.isChecked());

                    login(email, pwd);

                } else{
                    if(TextUtils.isEmpty(emailField.getText().toString())){
                        emailField.setError("Unesite E-mail");
                    } else {
                        password.setError("Unesite lozinku");
                    }
                }
            }
        });

        setEmailAndPassValue();

    }

    private void login(String email, String pwd) {

        mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    goToNextActivity();
                    Toast.makeText(LoginActivity.this, "Korisnik je uspesno prijavljen", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Doslo je do problema prilikom prijave. Problem: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void goToNextActivity(){
        startActivity(new Intent(LoginActivity.this, DrinksActivity.class));
        finish();
    }
    private void saveEmailAndPassValue(String email, String password, Boolean checkBox){

        sharedPreferences.edit().putString(Constants.EMAIL_VALUE, email).apply();
        if(checkBox){
            sharedPreferences.edit().putString(Constants.PASSWORD_VALUE, password).apply();
            sharedPreferences.edit().putBoolean(Constants.REMEMBER_PASS_CB, true).apply();
        } else{
            sharedPreferences.edit().putString(Constants.PASSWORD_VALUE, "").apply();
            sharedPreferences.edit().putBoolean(Constants.REMEMBER_PASS_CB, false).apply();
        }

    }
    private void setEmailAndPassValue(){
        String emailValue = sharedPreferences.getString(Constants.EMAIL_VALUE, "");
        String passValue = sharedPreferences.getString(Constants.PASSWORD_VALUE, "");
        Boolean isChecked = sharedPreferences.getBoolean(Constants.REMEMBER_PASS_CB, false);

        emailField.setText(emailValue);
        password.setText(passValue);
        rememberPassword.setChecked(isChecked);

    }
    private void verifyPermissions(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO};

            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[3]) == PackageManager.PERMISSION_GRANTED){

            }else{
                ActivityCompat.requestPermissions(LoginActivity.this, permissions, 1);
            }


        }
    }

    private void showConfirmationDialog(String message,String confirmButtonText, String dismissButtonText ){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        ConfirmationDialog confirmationDialog = new ConfirmationDialog();
        confirmationDialog.setMessage(message);
        confirmationDialog.setConfirmButtonText(confirmButtonText);
        confirmationDialog.setDismissButtonText(dismissButtonText);
        confirmationDialog.show(fragmentTransaction, Constants.CONFIRMATION_DIALOG_TAG);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }

    @Override
    public void onConfirmButtonClicked(DialogFragment dialogFragment) {

        if(mUser != null && mAuth != null) {
            String userEmail = mUser.getEmail();
            mAuth.sendPasswordResetEmail(userEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(LoginActivity.this, "E-mail je poslat.", Toast.LENGTH_SHORT).show();

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Slanje e-maila neuspešno", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }
}
