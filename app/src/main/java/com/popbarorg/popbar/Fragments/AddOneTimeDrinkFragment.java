package com.popbarorg.popbar.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.popbarorg.popbar.Activities.DrinksActivity;
import com.popbarorg.popbar.Data.DatabaseHelper;
import com.popbarorg.popbar.Model.DrinkModel;
import com.popbarorg.popbar.R;
import com.popbarorg.popbar.Util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;


public class AddOneTimeDrinkFragment extends AppCompatDialogFragment implements View.OnClickListener {

    private EditText drinkName, barcode;
    private FloatingActionButton submitButton, cancelButton;
    private ImageView addDrinkImage, scanBarcodeImage;
    private android.support.v7.app.AlertDialog alertDialog;
    private Bitmap mBitmap;
    private Uri drinkImageUri;
    String nameOfDrink;
    String barCodeOfDrink;
    String formattedDate;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_onetime_drink_fragment, null);

        drinkName = view.findViewById(R.id.drinkNameEditText);
        barcode = view.findViewById(R.id.drinkBarcodeEt);

        submitButton = view.findViewById(R.id.submitButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        addDrinkImage = view.findViewById(R.id.drinkImage);
        scanBarcodeImage = view.findViewById(R.id.scanBarcodeImage);

        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        formattedDate = dateFormat.format(new Date(java.lang.System.currentTimeMillis()).getTime());

        scanBarcodeImage.setOnClickListener(this);
        addDrinkImage.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        builder.setView(view);
        return builder.create();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Skeniranje prekinuto", Toast.LENGTH_SHORT).show();
            } else {
                barcode.setText(result.getContents());
            }
        } else if (requestCode == Constants.CAMERA_CODE && resultCode == DrinksActivity.RESULT_OK ) {

            //OVDE SE DOBIJA BITMAP IZ KAMERE

            mBitmap = (Bitmap) data.getExtras().get("data");
            addDrinkImage.setImageBitmap(mBitmap);
            if (alertDialog != null)
                alertDialog.dismiss();
        } else if(requestCode == Constants.GALLERY_CODE && resultCode == DrinksActivity.RESULT_OK){


            //OVDE SE DOBIJA URI FAJLA

            drinkImageUri = data.getData();
            addDrinkImage.setImageURI(drinkImageUri);
            if (alertDialog != null)
                alertDialog.dismiss();


        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startScanning(){
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setPrompt("Skeniraj barkod proizvoda");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();

    }



    @Override
    public void onClick(View view) {


        switch (view.getId()){

            case R.id.scanBarcodeImage:
                startScanning();
                break;

            case R.id.drinkImage:
                createAlertDialog();
                break;

            case R.id.submitButton:
                 submitNewDrinkToDatabase();

                break;

            case R.id.cancelButton:
                this.dismiss();
                break;

        }
    }

    private void submitNewDrinkToDatabase() {

        nameOfDrink = drinkName.getText().toString().trim();
        barCodeOfDrink = barcode.getText().toString().trim();

        if(!TextUtils.isEmpty(nameOfDrink)&& !TextUtils.isEmpty(barCodeOfDrink)){

            DatabaseHelper databaseHelper = new DatabaseHelper();
            if(databaseHelper.checkIfBarcodeExists(barCodeOfDrink)){
                Toast.makeText(getActivity(), "Piće sa ovim barkodom već postoji u bazi", Toast.LENGTH_SHORT).show();
            }else {


                if (mBitmap == null && drinkImageUri == null) {
                    Toast.makeText(getActivity(), "Unesi sliku pića.", Toast.LENGTH_SHORT).show();

                }else {


                    //IMAM BITMAP ALI NEMAM URI
                    if (mBitmap != null && drinkImageUri == null) {


                        saveNewPhoto(mBitmap);

                        //NEMAM BITMAP ALI IMAM URI
                    } else if (mBitmap == null && drinkImageUri != null) {

                        saveNewPhoto(drinkImageUri);

                    }
                }
            }

        }else{
            Toast.makeText(getActivity(), "Ubaci sliku i popuni sva polja pre potvrde", Toast.LENGTH_SHORT).show();
        }

    }


    private void saveNewPhoto(Bitmap mBitmap) {
        AddOneTimeDrinkFragment.BackgroundImageResize backgroundImageResize = new AddOneTimeDrinkFragment.BackgroundImageResize(mBitmap);
        Uri uri = null;
        backgroundImageResize.execute(uri);

    }

    private void saveNewPhoto(Uri drinkImageUri) {

        AddOneTimeDrinkFragment.BackgroundImageResize backgroundImageResize = new AddOneTimeDrinkFragment.BackgroundImageResize(null);
        backgroundImageResize.execute(drinkImageUri);

    }


    private class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {

        Bitmap bitmap;

        public BackgroundImageResize(Bitmap bitmap) {

            if(bitmap != null){
                this.bitmap = bitmap;
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {

            if(bitmap == null){

                try{

                    //OVO PRETVARA URI OBJEKAT U BITMAP
                    //OVA LINIJA KODA NEKAD ROTIRA SLIKU
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uris[0]);

                }catch (IOException e){
                    e.getMessage();

                }
            }
            byte[] bytes = null;

            Log.i("DO IN BACKGROUND", "MB BEFORE COMPRESION: " + bitmap.getByteCount() / 1000000);
            bytes = getBytesFromBitmap(bitmap, 50);
            Log.i("DO IN BACKGROUND", "MB BEFORE COMPRESION: " + bytes.length / 1000000);
            return  bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);


            DrinkModel drinkModel = new DrinkModel(barCodeOfDrink, nameOfDrink,formattedDate, bytes, true);

            drinkModel.save();

            Toast.makeText(getActivity(), "Pice sacuvano", Toast.LENGTH_SHORT).show();

            AllDrinksFragment allDrinksFragment = (AllDrinksFragment) AddOneTimeDrinkFragment.this.getParentFragment();
            if(allDrinksFragment != null) {
                allDrinksFragment.refreshList();
            }

            AddOneTimeDrinkFragment.this.dismiss();




        }
    }
    private void createAlertDialog() {


        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_import_image, null);
        builder.setView(dialogView);

        LinearLayout takePhoto = dialogView.findViewById(R.id.useCameraButton);
        LinearLayout importPhoto = dialogView.findViewById(R.id.importFromGalleryButton);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                AddOneTimeDrinkFragment.this.startActivityForResult(intent, Constants.CAMERA_CODE );
            }
        });

        importPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                AddOneTimeDrinkFragment.this.startActivityForResult(galleryIntent, Constants.GALLERY_CODE);
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();

    }
}
