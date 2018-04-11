package com.popbarorg.popbar.Data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.popbarorg.popbar.Model.CalculatedDrinkModel;
import com.popbarorg.popbar.R;
import com.reactiveandroid.query.Delete;

import java.util.List;


public class CalculatedDrinkRecycleAdapter extends RecyclerView.Adapter<CalculatedDrinkRecycleAdapter.ViewHolder> {

    private Context context;
    private List<CalculatedDrinkModel> calculatedDrinkModelList;
    private AlertDialog dialog;

    public CalculatedDrinkRecycleAdapter(Context context, List<CalculatedDrinkModel> calculatedDrinkModelList) {
        this.context = context;
        this.calculatedDrinkModelList = calculatedDrinkModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calculated_drink_item_row, parent, false);


        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CalculatedDrinkModel calculatedDrinkModel = calculatedDrinkModelList.get(position);
        holder.titleTextView.setText(calculatedDrinkModel.getName());
        holder.barcodeTextView.setText(calculatedDrinkModel.getBarcode());
        holder.calculatedVolumeTextView.setText(calculatedDrinkModel.getCalculatedVolume() + "ml");

        Glide.with(context).load(calculatedDrinkModel.getCalculatedDrinkImage()).into(holder.calculatedDrinkImageView);




    }

    @Override
    public int getItemCount() {
        return calculatedDrinkModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView, barcodeTextView, calculatedVolumeTextView;
        private ImageView calculatedDrinkImageView;
        private Button calculatedDrinkDeleteButton;

        public ViewHolder(View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            titleTextView = itemView.findViewById(R.id.calculatedDrinkTitle);
            barcodeTextView = itemView.findViewById(R.id.calculatedDrinkBarcode);
            calculatedVolumeTextView = itemView.findViewById(R.id.calculatedDrinkVolumeLeft);
            calculatedDrinkImageView = itemView.findViewById(R.id.calculatedDrinkImage);
            calculatedDrinkDeleteButton = itemView.findViewById(R.id.calculatedDrinkDeleteButton);

            calculatedDrinkDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    CalculatedDrinkModel calculatedDrinkModel = calculatedDrinkModelList.get(position);
                    String barcode = calculatedDrinkModel.getBarcode();
                    deleteDrink(barcode);
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
                    Delete.from(CalculatedDrinkModel.class).where("calculated_drink_barcode = ?", barcode).execute();
                    calculatedDrinkModelList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    dialog.dismiss();


                }
            });

        }
    }
}
