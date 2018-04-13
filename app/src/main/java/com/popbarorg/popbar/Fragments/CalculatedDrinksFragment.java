package com.popbarorg.popbar.Fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.popbarorg.popbar.Data.CalculatedDrinkRecycleAdapter;
import com.popbarorg.popbar.Data.DatabaseHelper;
import com.popbarorg.popbar.Model.CalculateDialog;
import com.popbarorg.popbar.Model.CalculatedDrinkModel;
import com.popbarorg.popbar.Model.DrinkModel;
import com.popbarorg.popbar.R;
import com.popbarorg.popbar.Util.Constants;
import com.reactiveandroid.query.Select;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CalculatedDrinksFragment extends Fragment {

    private RecyclerView calculatedDrinkRecyclerView;
    private CalculatedDrinkRecycleAdapter adapter;
    private List<CalculatedDrinkModel> calculatedDrinkModelList;

    private ImageView calendarImageView, noDataImageView;
    private TextView  pickedDateTextView;
    private Calendar calendar;
    private int day,month,year;
    private String date;
    private DatabaseHelper databaseHelper;
    private ImageView barCodeImageButton;

    private ProgressBar progressBar;

    File fileToWriteIn;
    private static final String CSV_SEPARATOR = ",";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calculated_drinks_fragment, container, false);

        noDataImageView = view.findViewById(R.id.noDataImageView);
        calendar = Calendar.getInstance();

        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        month = month+1;
        date = day+"/"+month+"/"+year;

        calculatedDrinkRecyclerView = view.findViewById(R.id.calculatedDrinksRecycleView);

        calculatedDrinkModelList = new ArrayList<>();
        refreshRecyclerView(date);


        calendarImageView = view.findViewById(R.id.datePickerImageView);
        pickedDateTextView = view.findViewById(R.id.datePickedTextView);


        progressBar = view.findViewById(R.id.progressBar);


        pickedDateTextView.setText(date);

        calendarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {



                        month = month+1;
                        date = day+"/"+month+"/"+year;
                        pickedDateTextView.setText(date);
                        refreshRecyclerView(date);


                    }
                }, year, month-1, day);

                datePickerDialog.show();
            }
        });

        barCodeImageButton = view.findViewById(R.id.barcodeImageButton);

        barCodeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScanning();
            }
        });

        setHasOptionsMenu(true);
        return view;
    }

    public void refreshRecyclerView(String date) {

        if (calculatedDrinkModelList != null) {
            calculatedDrinkModelList.clear();
            databaseHelper = new DatabaseHelper();
            calculatedDrinkModelList = databaseHelper.getAllCalculatedDrinks(date);
            if (calculatedDrinkModelList.isEmpty()) {

                calculatedDrinkRecyclerView.setVisibility(View.GONE);
                noDataImageView.setVisibility(View.VISIBLE);


            } else {
                calculatedDrinkRecyclerView.setVisibility(View.VISIBLE);
                noDataImageView.setVisibility(View.GONE);
                calculatedDrinkRecyclerView.setBackgroundResource(0);
                adapter = new CalculatedDrinkRecycleAdapter(getActivity(), calculatedDrinkModelList);
                calculatedDrinkRecyclerView.setHasFixedSize(true);
                calculatedDrinkRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                calculatedDrinkRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this.getActivity(), "Skeniranje prekinuto", Toast.LENGTH_SHORT).show();
            } else {

                String barcode = result.getContents();
                DatabaseHelper databaseHelper = new DatabaseHelper();


                if(databaseHelper.checkIfBarcodeExists(barcode)) {
                    DrinkModel drinkModel = Select.from(DrinkModel.class).where("drink_barcode = ?", barcode).fetchSingle();

                    FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack(null);

                    CalculateDialogFragment calculateDialogFragment = new CalculateDialogFragment(drinkModel);
                    calculateDialogFragment.show(fragmentTransaction, Constants.CALCULATE_VOLUME_DIALOG_FRAGMENT_TAG);

//                    CalculateDialog calculateDialog = new CalculateDialog(getActivity());
//                    calculateDialog.calculateVolumeDialog(drinkModel);
                }else{
                    Toast.makeText(this.getActivity(), R.string.drink_not_exist_in_db, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.calculated_drinks_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.export:

                BackgroundWriteToCSV backgroundWriteToCSV = new BackgroundWriteToCSV();
                backgroundWriteToCSV.execute();

                break;

            case R.id.deleteAll:


                deleteAllDrinks();

                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }


    private boolean isExternalStorageWritable(){

        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    private boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            return true;
        }
        return false;

    }

    private void exportObjectsToCSVFile(){



        if(isExternalStorageReadable() && isExternalStorageWritable()){

            File dir = new File(Environment.getExternalStorageDirectory()+"/Pop.Bar");
            dir.mkdir();
            fileToWriteIn = new File(dir, "Tabela_Izracunatih_Pica.csv");

            try{
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToWriteIn, true), "UTF-8"));

                for(CalculatedDrinkModel model : calculatedDrinkModelList){

                    StringBuilder oneLine = new StringBuilder();
                    oneLine.append(model.getName());
                    oneLine.append(CSV_SEPARATOR);
                    oneLine.append(model.getBarcode());
                    oneLine.append(CSV_SEPARATOR);
                    oneLine.append(model.getDate());
                    oneLine.append(CSV_SEPARATOR);
                    oneLine.append(model.getCalculatedVolume());
                    oneLine.append("ml");
                   bw.write(oneLine.toString());
                   bw.newLine();

                }

                bw.flush();
                bw.close();



            } catch (UnsupportedEncodingException ex){
                ex.printStackTrace();
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }


        }else
        {
            Toast.makeText(getActivity(), "Nemate dozvolu da kreirate fajl na Android sistemu", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendFileToEmail(){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        Uri path = Uri.fromFile(fileToWriteIn);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Popis na dan " + date);
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        startActivity(Intent.createChooser(emailIntent, "Slanje tabele..."));

    }



    private class BackgroundWriteToCSV extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            exportObjectsToCSVFile();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressBar.setVisibility(View.GONE);

            sendFileToEmail();



            super.onPostExecute(aVoid);

        }
    }

    private void deleteAllDrinks() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.confirmation_dialog_layout, null);

        TextView message = view.findViewById(R.id.confirmDialogMessage);
        Button noButton =  view.findViewById(R.id.noButton);
        Button yesButton = view.findViewById(R.id.yesButton);

        message.setText(R.string.delete_all_drinks_for_date);

        alertDialogBuilder.setView(view);
        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();


        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!calculatedDrinkModelList.isEmpty()) {
                    DatabaseHelper databaseHelper = new DatabaseHelper();
                    databaseHelper.deleteAllForDate(date);
                    Toast.makeText(getActivity(), "Pića obrisana.", Toast.LENGTH_SHORT).show();

                    refreshRecyclerView(date);
                    dialog.dismiss();
                }else{
                    Toast.makeText(getActivity(), "Ne postoje pića za ovaj dan.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

    }
}
