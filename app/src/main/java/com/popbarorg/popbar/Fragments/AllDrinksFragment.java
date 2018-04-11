package com.popbarorg.popbar.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;

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



import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.popbarorg.popbar.Data.AllDrinksRecycleAdapter;
import com.popbarorg.popbar.Data.DatabaseHelper;

import com.popbarorg.popbar.Model.DrinkModel;
import com.popbarorg.popbar.R;
import com.popbarorg.popbar.Util.Constants;


import java.util.ArrayList;
import java.util.List;

public class AllDrinksFragment extends Fragment {

    private RecyclerView recyclerView;
    private AllDrinksRecycleAdapter recycleAdapter;
    private List<DrinkModel> drinkList;

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





    private void createBoomButtons(){

        HamButton.Builder builder = new HamButton.Builder()
                .normalImageRes(R.drawable.add_drink_icon).normalText("Dodaj piće")
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
//        builder = new HamButton.Builder()
//                .normalImageRes(R.drawable.add_one_time_usable_drink).normalText("Dodaj jednokratno piće")
//                .listener(new OnBMClickListener() {
//                    @Override
//                    public void onBoomButtonClick(int index) {
//                        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
//                        fragmentTransaction.addToBackStack(null);
//                        AddOneTimeDrinkFragment addOneTimeDrinkFragment = new AddOneTimeDrinkFragment();
//                        addOneTimeDrinkFragment.show(fragmentTransaction, Constants.ADD_ONETIME_DRINK_DIALOG_FRAGMENT_TAG);
//                    }
//                });;
//        bmb.addBuilder(builder);

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
