package com.example.gp4;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private TextView emotion;
    private ImageButton talk;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView tempText;
    private String emoword = "";
    private ArrayList emolist; // TODO emoword, emolist 둘 중 하나 지우기

    private Fragment fragment = new Emotion3Fragment();

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
        emotion = (TextView)viewGroup.findViewById(R.id.emotion2_fragment_textview_emotion);

        emolist = new ArrayList();

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
                KomoranResult komoranResult = komoran.analyze(strToAnalyze);

                List<Token> tokenList = komoranResult.getTokenList();
                String word = "";

//                if(stress) {
//                    emoword = "O ";
//                    emolist.add("O");
//                } else{
//                    emoword = "X ";
//                    emolist.add("X");
//                }

                for(Token token : tokenList){
                    word = word + token.getMorph() +"/" + token.getPos();
                    // token.getMorph()
                    emoword = emoword + " " + whatIs(token.getMorph(),stress);
                    emolist.add( whatIs(token.getMorph(),stress) );
                    //list.add(whatIs(token.getMorph(), stress));
                }

                boolean temp = false;
                for(int i=0; i<emolist.size(); i++){
                    if(emolist.get(i).toString().contains("10") || emolist.get(i).toString().contains("20") || emolist.get(i).toString().contains("30")
                    || emolist.get(i).toString().contains("40") || emolist.get(i).toString().contains("50")){
                        temp = true;
                        break;
                    }
                }

                tempText.setText(word);
                if(temp){

                    emotion.setText(emoword);


                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("stress", stress);
                            bundle.putParcelableArrayList("emotion", emolist);

                            fragment.setArguments(bundle);

                            ((MainActivity)getActivity()).replaceFragment(fragment);
                        }
                    },7000); // 3초 후 실행
                }else{
                    Toast.makeText(getContext(), "감정 언어를 다시 말해주세요", Toast.LENGTH_SHORT).show();
                }




            }
        });
        return viewGroup;
    }

    private String whatIs(String text, Boolean stress){

        // csv 파일을 읽어서 resultList에 넣기
        //InputStream inputStream = getActivity().getResources().openRawResource(R.raw.emotionDic);
        InputStream inputStream = getResources().openRawResource(R.raw.emotiondic);
        List resultList = new ArrayList();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try{
            String csvLIne;
            while( (csvLIne = bufferedReader.readLine()) != null ){
                String[] row = csvLIne.split(",");
                resultList.add(row);
            }
        }catch(IOException e){
            throw new RuntimeException("Error in reading CSV file : " +e);
        }finally {
            try{
                inputStream.close();
            }catch (IOException e){
                throw new RuntimeException("Error while closing input stream : " + e);
            }
        }
        // csv 파일을 읽어서 resultList에 넣기

        for(int i=0; i<resultList.size(); i++ ){
            String[] temp = (String[]) resultList.get(i);

            if(text.equals(temp[0])){
                if(stress) { return temp[1]; }
                else{ return temp[2]; }
            }
        }


        return "0";
    }

    //    private String whatIs(String text, Boolean stress){
//
//        String[] plus = {"가장", "개", "굉장히","극단", "너무", "당연"};
//        String[] little = {"그냥", "딱히", "별로", "살짝", "약간", "조금"};
//        String[] nono = {"않", "않다", "아니하다", "못"};
//        String[] happy = {"감격", "감동","감사","감탄","고마움","기쁘다","긍정"};
//        String[] sad = {"고통", "그립다", "꿀꿀", "동정"};
//        String[] soso = {"괴로움", "귀찮", "그저그래", "노곤"};
//        String[] angry = {"가증", "격분", "경멸"};
//        String[] fear = {"공포", "걱정", "겁"};
//        String[] ex = {"거부감", "나쁘다","놀람", "눈물", "미치겠다"}; // 생각해야 함 어떻게 구분할지
//
//        for(int i=0; i<plus.length; i++){
//            if(text.equals(plus[i])){
//                return "+";
//            }
//        }
//
//        for(int i=0; i<little.length; i++){
//            if(text.equals(little[i])){
//                return "-";
//            }
//        }
//
//        for(int i=0; i<nono.length; i++){
//            if(text.equals(nono[i])){
//                return "!";
//            }
//        }
//
//        for(int i=0; i<happy.length; i++){
//            if(text.equals(happy[i])){
//                return "2";
//            }
//        }
//        for(int i=0; i<sad.length; i++){
//            if(text.equals(sad[i])){
//                return "3";
//            }
//        }
//        for(int i=0; i<soso.length; i++){
//            if(text.equals(soso[i])){
//                return "4";
//            }
//        }
//        for(int i=0; i<angry.length; i++){
//            if(text.equals(angry[i])){
//                return "5";
//            }
//        }
//        for(int i=0; i<fear.length; i++){
//            if(text.equals(fear[i])){
//                return "6";
//            }
//        }
//        for(int i=0; i<ex.length; i++){
//            if(text.equals(ex[i])){
//                return "7";
//            }
//        }
//
//
//        return "0";
//    }

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
