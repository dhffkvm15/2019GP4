package com.example.gp4;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Emotion1Fragment extends Fragment {

    private int heartrate = 0;

    public static Emotion1Fragment newInstance(){
        return new Emotion1Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_emotion1, container, false);

        Bundle bundle = getArguments();
        heartrate = bundle.getInt("heartrate");
        Log.v("중요", "전달된 것 : " + heartrate);


        return viewGroup;
    }
}

