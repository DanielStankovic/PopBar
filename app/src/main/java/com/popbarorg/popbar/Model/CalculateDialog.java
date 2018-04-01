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
import com.popbarorg.popbar.R;

import java.math.BigDecimal;

public class CalculateDialog {

    Context context;
    AlertDialog alertDialog;

    int numberPickerValue = 0;

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
        NumberPicker numberPicker = dialogView.findViewById(R.id.numberPicker);
        TextInputLayout textInputLayout = dialogView.findViewById(R.id.calculateVolumeTextInputLayout);
       final EditText calculatedWeightEditText = dialogView.findViewById(R.id.calculateVolumeEditText);
        FloatingActionButton submitButton = dialogView.findViewById(R.id.calculateVolumeSubmitButton);
        FloatingActionButton cancelButton = dialogView.findViewById(R.id.calculateVolumeCancelButton);
        final TextView rezultat = dialogView.findViewById(R.id.rezultat);

        if(drinkModel.isOneTimeUsable()){
            textInputLayout.setVisibility(View.INVISIBLE);
        }


        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(1000);



       numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
           @Override
           public void onValueChange(NumberPicker numberPicker, int i, int i1) {
               numberPickerValue = numberPicker.getValue();
           }
       });


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

                String quantity = String.valueOf(numberPickerValue);
                if(drinkModel.isOneTimeUsable()) {

                    rezultat.setText(quantity);
                }
                else {

                    String calculatedWeightString = calculatedWeightEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(calculatedWeightString)) {
                        float emptyBottleWeight = Float.parseFloat(drinkModel.getEmptyBottleWeight());
                        float fullBottleWeight = Float.parseFloat(drinkModel.getFullBottleWeight());
                        float startingVolume = Float.parseFloat(drinkModel.getMilliliters());
                        float density = (fullBottleWeight - emptyBottleWeight) / startingVolume;

                        float calculatedWeight = Float.parseFloat(calculatedWeightString);
                        float volumeLeftInBottle = (calculatedWeight - emptyBottleWeight) / density;
                        float amountSold = startingVolume - volumeLeftInBottle;
                        BigDecimal bd = new BigDecimal(amountSold);
                        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);

                        rezultat.setText("Prodato je " + quantity + " flaša i " + String.valueOf(bd) + " ml ovog alkohola");


                    }else{
                        Toast.makeText(context, "Unesite težinu izmerene flaše.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

}
