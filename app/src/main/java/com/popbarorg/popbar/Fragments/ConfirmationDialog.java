package com.popbarorg.popbar.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.popbarorg.popbar.R;
import com.popbarorg.popbar.Util.Constants;


public class ConfirmationDialog extends AppCompatDialogFragment {

    private String message, confirmButtonText, dismissButtonText;
    private ConfirmationDialogListener listener;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setConfirmButtonText(String confirmButtonText) {
        this.confirmButtonText = confirmButtonText;
    }

    public void setDismissButtonText(String dismissButtonText) {
        this.dismissButtonText = dismissButtonText;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.confirmation_dialog_layout, null);


        TextView messageTextView = view.findViewById(R.id.confirmDialogMessage);
        Button confirmButton = view.findViewById(R.id.yesButton);
        Button dismissButton = view.findViewById(R.id.noButton);

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmationDialog.this.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onConfirmButtonClicked(ConfirmationDialog.this);
            }
        });

        if(savedInstanceState != null){
            message = savedInstanceState.getString(Constants.CONFIRMATION_DIALOG_MESSAGE);
            confirmButtonText = savedInstanceState.getString(Constants.CONFIRMATION_DIALOG_CONFIRM);
            dismissButtonText = savedInstanceState.getString(Constants.CONFIRMATION_DIALOG_DISMISS);
        }

        if(message != null){
            messageTextView.setText(message);
        }
        if(confirmButtonText != null){
            confirmButton.setText(confirmButtonText);
        }
        if(dismissButtonText != null){
            dismissButton.setText(dismissButtonText);
        }

        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.CONFIRMATION_DIALOG_MESSAGE, message);
        outState.putString(Constants.CONFIRMATION_DIALOG_CONFIRM, confirmButtonText);
        outState.putString(Constants.CONFIRMATION_DIALOG_DISMISS, dismissButtonText);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
try{
    listener = (ConfirmationDialogListener) context;
}catch (ClassCastException e){
    throw  new ClassCastException(context.toString() + " must implement ConfirmationDialogListener");
}

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface ConfirmationDialogListener{

    void onConfirmButtonClicked(DialogFragment dialogFragment);


    }
}
