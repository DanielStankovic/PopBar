package com.popbarorg.popbar.Fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.popbarorg.popbar.Data.CalculatedDrinkRecycleAdapter;
import com.popbarorg.popbar.Data.DatabaseHelper;
import com.popbarorg.popbar.Model.CalculateDialog;
import com.popbarorg.popbar.Model.CalculatedDrinkModel;
import com.popbarorg.popbar.Model.DrinkModel;
import com.popbarorg.popbar.R;
import com.popbarorg.popbar.Util.Constants;
import com.reactiveandroid.query.Select;

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

        return view;
    }

    public void refreshRecyclerView(String date) {

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

}
