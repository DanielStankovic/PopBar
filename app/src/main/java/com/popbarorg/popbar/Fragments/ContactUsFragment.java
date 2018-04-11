package com.popbarorg.popbar.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.popbarorg.popbar.R;


public class ContactUsFragment extends Fragment {
    TextView phoneNumber, emailAddress;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_us_fragment,container,false);



        phoneNumber = view.findViewById(R.id.phone_number_texView);
        emailAddress = view.findViewById(R.id.email_address_textView);
        emailAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callNumber();
            }
        });


        makeTextUnderlined(phoneNumber, getString(R.string.phone_number));
        makeTextUnderlined(emailAddress, getString(R.string.email_address));




        return view;
    }


    private void makeTextUnderlined(TextView textView, String text){

        SpannableString string = new SpannableString(text);
        string.setSpan(new UnderlineSpan(), 0, string.length(), 0);
        textView.setText(string);
    }

    private void sendEmail(){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_address)});
        startActivity(Intent.createChooser(intent, getString(R.string.choose_email_app)));
    }

    private void callNumber(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + getString(R.string.phone_number)));
        startActivity(intent);
    }
}
