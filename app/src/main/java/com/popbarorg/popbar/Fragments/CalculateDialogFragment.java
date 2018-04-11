package com.popbarorg.popbar.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.popbarorg.popbar.Data.DatabaseHelper;
import com.popbarorg.popbar.Model.CalculatedDrinkModel;
import com.popbarorg.popbar.Model.DrinkModel;
import com.popbarorg.popbar.R;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Created by Daniel on 4/10/2018.
 */

public class CalculateDialogFragment extends AppCompatDialogFragment {

    private DrinkModel drinkModel;

    public CalculateDialogFragment(DrinkModel drinkModel) {
        this.drinkModel = drinkModel;
    }

    public CalculateDialogFragment(){

    }

    private Calendar calendar = Calendar.getInstance();

    private String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    private String month = String.valueOf(calendar.get(Calendar.MONTH)+1);
    private String year = String.valueOf(calendar.get(Calendar.YEAR));
    private String date = day+"/"+month+"/"+year;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.calculate_volume_dialog, null);

        TextView drinkTitleTextView = view.findViewById(R.id.calculateVolumeDrinkTitle);
        ImageView drinkImage = view.findViewById(R.id.calculateVolumeDrinkImage);


        final EditText calculatedWeightEditText = view.findViewById(R.id.calculateVolumeEditText);
        FloatingActionButton submitButton = view.findViewById(R.id.calculateVolumeSubmitButton);
        FloatingActionButton cancelButton = view.findViewById(R.id.calculateVolumeCancelButton);

        Glide.with(getActivity()).load(drinkModel.getDrinkImage()).into(drinkImage);
        drinkTitleTextView.setText(drinkModel.getName());


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalculateDialogFragment.this .dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String calculatedWeightString = calculatedWeightEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(calculatedWeightString)) {
                    DatabaseHelper databaseHelper = new DatabaseHelper();
                    if(databaseHelper.checkIfCalculatedDrinkExistsInRV(drinkModel.getBarCode(), date)){
                        Toast.makeText(getActivity(), "Piće sa ovim barkodom je već izmereno za danas.", Toast.LENGTH_SHORT).show();
                    } else {


                        float emptyBottleWeight = Float.parseFloat(drinkModel.getEmptyBottleWeight());
                        float fullBottleWeight = Float.parseFloat(drinkModel.getFullBottleWeight());
                        float startingVolume = Float.parseFloat(drinkModel.getMilliliters());
                        float density = (fullBottleWeight - emptyBottleWeight) / startingVolume;

                        float calculatedWeight = Float.parseFloat(calculatedWeightString);
                        float volumeLeftInBottle = (calculatedWeight - emptyBottleWeight) / density;

                        BigDecimal bd = new BigDecimal(volumeLeftInBottle);
                        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                        String calculatedVolume = String.valueOf(bd);

                        CalculatedDrinkModel calculatedDrinkModel = new CalculatedDrinkModel(drinkModel.getBarCode(), drinkModel.getName(),
                                date, calculatedVolume, drinkModel.getDrinkImage());

                        calculatedDrinkModel.save();
                        Toast.makeText(getActivity(), "Piće izračunato.", Toast.LENGTH_SHORT).show();

                        CalculatedDrinksFragment calculatedDrinksFragment = (CalculatedDrinksFragment) CalculateDialogFragment.this.getParentFragment();

                        if(calculatedDrinksFragment != null){
                            calculatedDrinksFragment.refreshRecyclerView(date);
                        }

                        CalculateDialogFragment.this.dismiss();


                    }
                }else{
                    Toast.makeText(getActivity(), "Unesite težinu izmerene flaše.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.setView(view);
        return builder.create();
    }
}
