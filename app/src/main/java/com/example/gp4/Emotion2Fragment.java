package com.example.gp4;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


/* 사용자의 음성 받아들이기 및 음성 감정 분석 */
public class Emotion2Fragment extends Fragment {

    private boolean stress = false; // 스트레스 유무
    private TextView STT;
    private ImageButton talk;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    public static Emotion2Fragment newInstance(){
        return new Emotion2Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_emotion2, container, false);

        STT = (TextView)viewGroup.findViewById(R.id.emotion2_fragment_textview_stt);
        talk = (ImageButton)viewGroup.findViewById(R.id.emotion2_fragment_button_talk);

        checkRecordPermission(); // 마이크 허가 받기

        init(); //기본 설정

        // 마이크 버튼 누를 때
        talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        return viewGroup;
    }

    // Speech to Text
    private void promptSpeechInput(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something...");
        try{
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }catch (ActivityNotFoundException e){
            Toast.makeText(getActivity().getApplicationContext(), "not supported", Toast.LENGTH_SHORT).show();
        }

    }

    // Receiving speech input


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_CODE_SPEECH_INPUT: {
                if(resultCode == RESULT_OK && null != data){
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    STT.setText(result.get(0));
                }
                break;
            }
        }
    }


    // 기본 설정 - 스트레스 값 전달 받기
    private void init() {
        Bundle bundle = getArguments();
        stress = bundle.getBoolean("stress");
        Log.v("전달", "전달 받은 stress : "+ stress);
    }

    /* 마이크 허가 받기 */
    public void checkRecordPermission() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO},
                    123);
        } else {
            Log.v("태그", "마이크 허가 받음");
        }
    }

}
