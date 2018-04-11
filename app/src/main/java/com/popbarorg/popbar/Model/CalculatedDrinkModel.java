package com.popbarorg.popbar.Model;


import com.popbarorg.popbar.Data.AppDatabase;
import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;

@Table(name = "CalculatedDrinks", database = AppDatabase.class)
public class CalculatedDrinkModel extends Model{


    @PrimaryKey
    private long id;
    @Column(name = "calculated_drink_barcode")
    private String barcode;
    @Column(name = "calculated_drink_name")
    private String name;
    @Column(name = "calculated_drink_date")
    private String date;
    @Column(name = "calculated_drink_volume")
    private String calculatedVolume;
    @Column(name = "calculated_drink_image")
    private byte[] calculatedDrinkImage;

    public CalculatedDrinkModel(String barcode, String name, String date, String calculatedVolume, byte[] calculatedDrinkImage) {
        this.barcode = barcode;
        this.name = name;
        this.date = date;
        this.calculatedVolume = calculatedVolume;
        this.calculatedDrinkImage = calculatedDrinkImage;
    }

    public CalculatedDrinkModel(){

    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCalculatedVolume() {
        return calculatedVolume;
    }

    public void setCalculatedVolume(String calculatedVolume) {
        this.calculatedVolume = calculatedVolume;
    }

    public byte[] getCalculatedDrinkImage() {
        return calculatedDrinkImage;
    }

    public void setCalculatedDrinkImage(byte[] calculatedDrinkImage) {
        this.calculatedDrinkImage = calculatedDrinkImage;
    }
}
