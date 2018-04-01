package com.popbarorg.popbar.Model;

import com.popbarorg.popbar.Data.AppDatabase;
import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

@Table(name = "Drinks", database = AppDatabase.class)
public class DrinkModel extends Model {

    @PrimaryKey
    private long id;
    @Column(name = "drink_barcode")
    private String barCode;
    @Column(name = "drink_name")
    private String name;
    @Column(name = "drink_volume")
    private String milliliters;
    @Column(name = "drink_full_weight")
    private String fullBottleWeight;
    @Column(name = "drink_empty_weight")
    private String emptyBottleWeight;
    @Column(name = "date_added")
    private String dateDrinkAdded;
    @Column(name = "drink_image")
    private byte[] drinkImage;
    @Column(name = "is_one_time_usable")
    private boolean isOneTimeUsable;


    public DrinkModel(String barCode, String name, String milliliters, String fullBottleWeight, String emptyBottleWeight,String dateDrinkAdded, byte[] drinkImage, boolean isOneTimeUsable) {
        this.barCode = barCode;
        this.name = name;
        this.milliliters = milliliters;
        this.fullBottleWeight = fullBottleWeight;
        this.emptyBottleWeight = emptyBottleWeight;
        this.drinkImage = drinkImage;
        this.isOneTimeUsable = isOneTimeUsable;
        this.dateDrinkAdded = dateDrinkAdded;
    }

    public DrinkModel(String barCode, String name, String dateDrinkAdded, byte[] drinkImage, boolean isOneTimeUsable) {
        this.barCode = barCode;
        this.name = name;
        this.dateDrinkAdded = dateDrinkAdded;
        this.drinkImage = drinkImage;
        this.isOneTimeUsable = isOneTimeUsable;
    }

    public DrinkModel(){

    }

    public String getDateDrinkAdded() {
        return dateDrinkAdded;
    }

    public void setDateDrinkAdded(String dateDrinkAdded) {
        this.dateDrinkAdded = dateDrinkAdded;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMilliliters() {
        return milliliters;
    }

    public void setMilliliters(String milliliters) {
        this.milliliters = milliliters;
    }

    public String getFullBottleWeight() {
        return fullBottleWeight;
    }

    public void setFullBottleWeight(String fullBottleWeight) {
        this.fullBottleWeight = fullBottleWeight;
    }

    public String getEmptyBottleWeight() {
        return emptyBottleWeight;
    }

    public void setEmptyBottleWeight(String emptyBottleWeight) {
        this.emptyBottleWeight = emptyBottleWeight;
    }

    public byte[] getDrinkImage() {
        return drinkImage;
    }

    public void setDrinkImage(byte[] drinkImage) {
        this.drinkImage = drinkImage;
    }

    public boolean isOneTimeUsable() {
        return isOneTimeUsable;
    }

    public void setOneTimeUsable(boolean oneTimeUsable) {
        isOneTimeUsable = oneTimeUsable;
    }



}
