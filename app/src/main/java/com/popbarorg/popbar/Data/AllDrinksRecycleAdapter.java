package com.popbarorg.popbar.Data;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.popbarorg.popbar.Model.CalculateDialog;
import com.popbarorg.popbar.Model.DrinkModel;
import com.popbarorg.popbar.R;
import com.reactiveandroid.query.Delete;

import java.util.List;


public class AllDrinksRecycleAdapter extends RecyclerView.Adapter<AllDrinksRecycleAdapter.ViewHolder> {

    private Context context;
    private List<DrinkModel> drinkList;
    private AlertDialog dialog;

    public AllDrinksRecycleAdapter(Context context, List<DrinkModel> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drink_item_row, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        DrinkModel drinkModel = drinkList.get(position);
        holder.drinkName.setText(drinkModel.getName());
        holder.drinkBarcode.setText(drinkModel.getBarCode());
        holder.dateAdded.setText("Datum: " + drinkModel.getDateDrinkAdded());

        Glide.with(context).load(drinkModel.getDrinkImage()).into(holder.drinkImage);

    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView drinkName, drinkBarcode, dateAdded;
        public ImageView drinkImage;
        public Button deleteDrinkButton;

        public ViewHolder(View itemView, final Context ctx) {
            super(itemView);

            context = ctx;

            drinkName = itemView.findViewById(R.id.drinkNameTextView);
            drinkBarcode = itemView.findViewById(R.id.barcodeTextView);
            drinkImage = itemView.findViewById(R.id.drinkImage);
            deleteDrinkButton = itemView.findViewById(R.id.deleteButton);
            dateAdded = itemView.findViewById(R.id.dateAddedTextView);

            deleteDrinkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    DrinkModel drinkModel = drinkList.get(position);
                    String barcodeDrink = drinkModel.getBarCode();
                    deleteDrink(barcodeDrink);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    DrinkModel drinkModel = drinkList.get(position);
                    CalculateDialog calculateDialog = new CalculateDialog(ctx);
                    calculateDialog.calculateVolumeDialog(drinkModel);
                }
            });


        }
        public void deleteDrink(final String barcode) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_dialog_layout, null);

            Button noButton =  view.findViewById(R.id.noButton);
            Button yesButton = view.findViewById(R.id.yesButton);




            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();


            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Delete.from(DrinkModel.class).where("drink_barcode = ?", barcode).execute();
                    drinkList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    dialog.dismiss();


                }
            });

        }
    }



    public void filterList (List<DrinkModel> list){

        drinkList = list;
        notifyDataSetChanged();
    }
}
