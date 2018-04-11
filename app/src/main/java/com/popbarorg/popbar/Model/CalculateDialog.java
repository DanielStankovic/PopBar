package com.popbarorg.popbar.Model;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.popbarorg.popbar.Data.DatabaseHelper;
import com.popbarorg.popbar.Fragments.CalculatedDrinksFragment;
import com.popbarorg.popbar.R;

import java.math.BigDecimal;
import java.util.Calendar;

public class CalculateDialog {

    Context context;
    AlertDialog alertDialog;



    private Calendar calendar = Calendar.getInstance();

    private String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    private String month = String.valueOf(calendar.get(Calendar.MONTH)+1);
    private String year = String.valueOf(calendar.get(Calendar.YEAR));
    private String date = day+"/"+month+"/"+year;


    public CalculateDialog(Context context) {
        this.context = context;
    }



    public void calculateVolumeDialog(final DrinkModel drinkModel){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.calculate_volume_dialog, null);
        builder.setView(dialogView);


        TextView drinkTitleTextView = dialogView.findViewById(R.id.calculateVolumeDrinkTitle);
        ImageView drinkImage = dialogView.findViewById(R.id.calculateVolumeDrinkImage);


       final EditText calculatedWeightEditText = dialogView.findViewById(R.id.calculateVolumeEditText);
        FloatingActionButton submitButton = dialogView.findViewById(R.id.calculateVolumeSubmitButton);
        FloatingActionButton cancelButton = dialogView.findViewById(R.id.calculateVolumeCancelButton);




    cancelButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        alertDialog.dismiss();
    }
});

        Glide.with(context).load(drinkModel.getDrinkImage()).into(drinkImage);

        drinkTitleTextView.setText(drinkModel.getName());





        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    String calculatedWeightString = calculatedWeightEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(calculatedWeightString)) {
                        DatabaseHelper databaseHelper = new DatabaseHelper();
                        if(databaseHelper.checkIfCalculatedDrinkExistsInRV(drinkModel.getBarCode(), date)){
                            Toast.makeText(context, "Piće sa ovim barkodom je već izmereno za danas.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(context, "Piće izračunato.", Toast.LENGTH_SHORT).show();

                            CalculatedDrinksFragment calculatedDrinksFragment = new CalculatedDrinksFragment();
                            calculatedDrinksFragment.refreshRecyclerView(date);

                            alertDialog.dismiss();


                        }
                    }else{
                        Toast.makeText(context, "Unesite težinu izmerene flaše.", Toast.LENGTH_SHORT).show();
                    }

            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }



}
