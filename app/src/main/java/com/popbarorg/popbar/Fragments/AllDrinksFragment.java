package com.popbarorg.popbar.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.popbarorg.popbar.Data.AllDrinksRecycleAdapter;
import com.popbarorg.popbar.Data.DatabaseHelper;
import com.popbarorg.popbar.Model.CalculateDialog;
import com.popbarorg.popbar.Model.DrinkModel;
import com.popbarorg.popbar.R;
import com.popbarorg.popbar.Util.Constants;
import com.reactiveandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

public class AllDrinksFragment extends Fragment {

    private RecyclerView recyclerView;
    private AllDrinksRecycleAdapter recycleAdapter;
    private List<DrinkModel> drinkList;
    private ImageButton barCodeImageButton;
    private  EditText searchBar;

    private BoomMenuButton bmb;

    private  DatabaseHelper databaseHelper;

    public AllDrinksFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.all_drinks_fragment, container, false);


       recyclerView = view.findViewById(R.id.recycleView);
       bmb = view.findViewById(R.id.bmb);
       createBoomButtons();

       drinkList = new ArrayList<>();
       databaseHelper = new DatabaseHelper();
       drinkList = databaseHelper.getAllDrinks();

       barCodeImageButton = view.findViewById(R.id.barcodeImageButton);

       barCodeImageButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startScanning();
           }
       });

       searchBar = view.findViewById(R.id.searchEditText);


       searchBar.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

           }

           @Override
           public void afterTextChanged(Editable s) {

               filter(s.toString());
           }
       });

        recycleAdapter = new AllDrinksRecycleAdapter(getActivity(), drinkList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recycleAdapter);
        recycleAdapter.notifyDataSetChanged();
        return view;

    }


    private void filter(String text){

        List<DrinkModel> newList = new ArrayList<>();

        for(DrinkModel model:drinkList ){

            if(model.getName().toLowerCase().contains(text.toLowerCase())){
                newList.add(model);
            }
        }
        recycleAdapter.filterList(newList);
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
                DrinkModel drinkModel = Select.from(DrinkModel.class).where("drink_barcode = ?", barcode).fetchSingle();

                CalculateDialog calculateDialog = new CalculateDialog(getActivity());
                calculateDialog.calculateVolumeDialog(drinkModel);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void createBoomButtons(){

        HamButton.Builder builder = new HamButton.Builder()
                .normalImageRes(R.drawable.ic_add_white_24dp).normalText("Dodaj piće")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                        fragmentTransaction.addToBackStack(null);
                        AddDrinkDialogFragment addDrinkDialogFragment = new AddDrinkDialogFragment();
                        addDrinkDialogFragment.show(fragmentTransaction, Constants.ADD_DRINK_DIALOG_FRAGMENT_TAG);
                    }
                });
        bmb.addBuilder(builder);
        builder = new HamButton.Builder()
                .normalImageRes(R.drawable.barcode).normalText("Dodaj jednokratno piće")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                        fragmentTransaction.addToBackStack(null);
                        AddOneTimeDrinkFragment addOneTimeDrinkFragment = new AddOneTimeDrinkFragment();
                        addOneTimeDrinkFragment.show(fragmentTransaction, Constants.ADD_ONETIME_DRINK_DIALOG_FRAGMENT_TAG);
                    }
                });;
        bmb.addBuilder(builder);

    }

    public void refreshList(){

        drinkList.clear();
        databaseHelper = new DatabaseHelper();
        drinkList = databaseHelper.getAllDrinks();
        recycleAdapter = new AllDrinksRecycleAdapter(getActivity(), drinkList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recycleAdapter);
        recycleAdapter.notifyDataSetChanged();

    }
}
