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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;


import static android.app.Activity.RESULT_OK;


/* 사용자의 음성 받아들이기 및 음성 감정 분석 */
public class Emotion2Fragment extends Fragment {

    private boolean stress = false; // 스트레스 유무
    private TextView STT;
    private ImageButton talk;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView tempText;

    public static Emotion2Fragment newInstance(){
        return new Emotion2Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_emotion2, container, false);

        STT = (TextView)viewGroup.findViewById(R.id.emotion2_fragment_textview_stt);
        talk = (ImageButton)viewGroup.findViewById(R.id.emotion2_fragment_button_talk);
        tempText = (TextView)viewGroup.findViewById(R.id.emotion2_fragment_textview_temp);

        checkRecordPermission(); // 마이크 허가 받기

        init(); //기본 설정

        // 마이크 버튼 누를 때
        talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        STT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
                String strToAnalyze = s.toString();
                Log.v("태그", "형태소 분석 " + strToAnalyze);
                KomoranResult komoranResult = komoran.analyze(strToAnalyze);

                List<Token> tokenList = komoranResult.getTokenList();
                String word = "";
                for(Token token : tokenList){
                    word = word + token.getMorph() +"/" + token.getPos();

                    //string = token.getMorph() + "/" +token.getPos() +" ";
                    System.out.format("%s/%s\n", token.getMorph(), token.getPos()); // pos 가 품사
                }
                Log.v("여기", "여기 word "+word);
                tempText.setText(word);


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

                   //STT.getText().toString()
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
