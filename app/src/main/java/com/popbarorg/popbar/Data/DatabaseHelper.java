package com.popbarorg.popbar.Data;


import com.popbarorg.popbar.Model.CalculatedDrinkModel;
import com.popbarorg.popbar.Model.DrinkModel;
import com.reactiveandroid.query.Select;


import java.util.List;

public class DatabaseHelper {



    public List<DrinkModel> getAllDrinks(){
        List<DrinkModel> drinkModels = Select.from(DrinkModel.class).fetch();
        return drinkModels;
    }


    public boolean checkIfBarcodeExists(String barcode){

        DrinkModel drinkModel = Select.from(DrinkModel.class).where("drink_barcode = ?", barcode).fetchSingle();
        if(drinkModel == null){
            return false;
        } else{
            return true;
        }

    }


    public List<CalculatedDrinkModel> getAllCalculatedDrinks(String date){

        List<CalculatedDrinkModel> calculatedDrinkModelList = Select.from(CalculatedDrinkModel.class)
                .where("calculated_drink_date = ?", date).fetch();

        return calculatedDrinkModelList;
    }

    public boolean checkIfCalculatedDrinkExistsInRV(String barcode, String date){


        CalculatedDrinkModel calculatedDrinkModel = Select.from(CalculatedDrinkModel.class)
                .where("calculated_drink_barcode = ? AND calculated_drink_date = ?", barcode, date).fetchSingle();

        if(calculatedDrinkModel == null){
            return false;
        }else{
            return true;
        }

    }


}
