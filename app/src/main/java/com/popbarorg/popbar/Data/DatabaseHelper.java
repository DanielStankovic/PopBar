package com.popbarorg.popbar.Data;


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


}
