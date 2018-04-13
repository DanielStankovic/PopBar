package com.popbarorg.popbar.Fragments;

import android.content.ActivityNotFoundException;
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


public class AboutAppFragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_app_fragment, container, false);

        TextView goToVideoLink = view.findViewById(R.id.goToVideoTextView);

        goToVideoLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchVideo();
            }
        });

        makeTextUnderlined(goToVideoLink, getString(R.string.here));



        return view;
    }

    private void makeTextUnderlined(TextView textView, String text){

        SpannableString string = new SpannableString(text);
        string.setSpan(new UnderlineSpan(), 0, string.length(), 0);
        textView.setText(string);
    }

    private void watchVideo(){

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + getString(R.string.youtube_video_id)));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + getString(R.string.youtube_video_id)));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
}
