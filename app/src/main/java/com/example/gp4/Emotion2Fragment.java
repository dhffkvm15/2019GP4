package com.example.gp4;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private ImageView imageView;
    private ImageButton talk;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private ArrayList emolist; // 형태소의 감정 분석 결과 저장
    private ArrayList pos; // 형태소의 품사 저장
    private int[] depth = {-1, +1, +1, -2, +1}; // joy, fear, sadness, disgust, anger
    private String str = ""; // 전달해주기 위한 감정

    private Fragment fragment = new Emotion3Fragment();

    public static Emotion2Fragment newInstance(){
        return new Emotion2Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_emotion2, container, false);

        STT = (TextView)viewGroup.findViewById(R.id.emotion2_fragment_textview_stt);
        talk = (ImageButton)viewGroup.findViewById(R.id.emotion2_fragment_button_talk);
        emotion = (TextView)viewGroup.findViewById(R.id.emotion2_fragment_textview_emotion);
        imageView = (ImageView)viewGroup.findViewById(R.id.emotion2_fragment_imageview);

        emolist = new ArrayList();
        pos = new ArrayList();

        checkRecordPermission(); // 마이크 허가 받기

        init(); //기본 설정 - stress 값 전달 받음

        // 마이크 버튼 누를 때
        talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emolist.clear();
                pos.clear();
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

            @SuppressLint("ResourceAsColor")
            @Override
            public void afterTextChanged(Editable s) {
                Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
                String strToAnalyze = s.toString();
                KomoranResult komoranResult = komoran.analyze(strToAnalyze);

                List<Token> tokenList = komoranResult.getTokenList();
                String word = "";

                // 형태소 분석하기
                for(Token token : tokenList){
                    word = word + token.getMorph() +"," +token.getPos() +"/";
                pos.add(token.getPos()); // 형태소 분석 품사 넣기
                    emolist.add( whatIs(token.getMorph(),stress) ); // 감정 결과 넣기
                }

                //Todo log 지우기 및 word 지우기
                Log.v("태그", "형태소 분석 " +word);
                for(int i=0; i<emolist.size(); i++){
                    Log.v("태그", "형태소" + pos.get(i));
                    Log.v("태그", "감정 " + emolist.get(i));
                }

                int tempCount = 0;

                // 감정이 포함된 개수 세기
                for(int i=0; i<emolist.size(); i++){
                    if(emolist.get(i).toString().equals("joy") || emolist.get(i).toString().equals("fear")
                    || emolist.get(i).toString().equals("sadness") || emolist.get(i).toString().equals("disgust")
                    || emolist.get(i).toString().equals("anger")){
                        tempCount ++;
                    }
                }

                Log.v("태그", "감정 개수 : " +tempCount);

                // 감정이 파악 되었을 경우에만
                if(tempCount != 0){

                    // 감정 표현이 1개인 경우
                    if(tempCount == 1){

                        Boolean noexpression = false; // 부정어 표현이 있는지 확인
                        str = "";
                        for(int i=0; i<emolist.size(); i++) {
                            if (emolist.get(i).equals("+")) {
                                str = str + "많이 ";
                            } else if (emolist.get(i).equals("-")) {
                                str = str + "조금 ";
                            } else if (emolist.get(i).equals("!")) {

                                // 기쁘지 않고의 '않'처럼 보조용언이거나 안 좋고의 '안' 처럼 일반부사 일 때만 인식
                                if(pos.get(i).equals("VX") || pos.get(i).equals("MAG")) {
                                    noexpression = true;
                                }

                            } else if (emolist.get(i).equals("joy")) {
                                str = str + "기쁨 ";
                                // todo 이미지 변경
                            } else if (emolist.get(i).equals("fear")) {
                                    str = str + "두려움 ";
                            } else if (emolist.get(i).equals("sadness")) {
                                    str = str + "슬픔 ";
                            } else if (emolist.get(i).equals("disgust")) {
                                    str = str + "무기력 ";
                            } else if (emolist.get(i).equals("anger")) {
                                    str = str + "분노 ";
                            }

                        }
                        if(noexpression) {

                            str="";
                            str = "무기력";

                            if(str.contains("많이")) {
                                str = "";
                                str = "많이 무기력";
                            } else if(str.contains("조금")) {
                                str = "";
                                str = "조금 무기력";
                            }
                        }

                    }else{
                        // 감정 표현이 두개 이상이면
                        str ="";
                        int high = -3; // 현재 가장 높은 강도를 저장할 변수
                        ArrayList highIndex = new ArrayList(); // 현재 가장 높은 강도를 가지고 있는 형태소의 배열 내 위치(index) 저장

                        Log.v("태그", "부정어 정리전 emolist size : " + emolist.size());

                        // 부정어 표현 정리
                        for(int i=0; i<emolist.size(); i++) {

                            // 부정어 표현이 있으면
                            if (emolist.get(i).equals("!")) {

                                if (pos.get(i).equals("VX")) {
                                    // 부정어가 보조용언 일때

                                    for(int j=1; i-j >= 0; j++){
                                        if( ! emolist.get(i-j).equals("0") ) {
                                            emolist.set(i-j, "disgust");
                                            break;
                                        }
                                    }

                                } else if (pos.get(i).equals("MAG")) {
                                    // 부정어가 일반 부사 일때

                                    for(int j=1; i+j < emolist.size(); j++){
                                        if( ! emolist.get(i+j).equals("0") ) {
                                            emolist.set(i+j, "disgust");
                                            break;
                                        }
                                    }
                                }

                                emolist.set(i,"0"); // 부정어 표현 제거
                                --i;
                            }
                        } // 부정어 표현 정리

                        //Todo log 지우기
                        Log.v("태그", "부정어 정리후 emolist size : " + emolist.size());
                        Log.v("태그", "부정어 정리후");
                        for(int i=0; i<emolist.size(); i++){
                            Log.v("태그", "감정 " + emolist.get(i));
                        }
                        Log.v("태그", "부정어 정리후 끝");

                        // 감정 강도 비교
                        for(int i=0; i<emolist.size(); i++){

                            if (emolist.get(i).equals("joy")) {

                                    if( high < -1){
                                        str = "기쁨 ";
                                        highIndex.clear();
                                        highIndex.add(i);
                                        high = -1;
                                    }else if(high == -1){
                                        highIndex.add(i);
                                    }

                            } else if (emolist.get(i).equals("fear")) {

                                    if( high < 1){
                                        str = "두려움";
                                        highIndex.clear();
                                        highIndex.add(i);
                                        high = 1;
                                    }else if(high == 1){
                                        Log.v("태그", "여기옴2");
                                        highIndex.add(i);
                                    }

                            } else if (emolist.get(i).equals("sadness")) {

                                    if( high < 1){
                                        str = "슬픔";
                                        highIndex.clear();
                                        highIndex.add(i);
                                        high = 1;
                                    }else if(high == 1){
                                        highIndex.add(i);
                                    }

                            } else if (emolist.get(i).equals("disgust")) {

                                if( high < -2){
                                    str = "무기력";
                                    highIndex.clear();
                                    highIndex.add(i);
                                    high = -2;
                                }else if(high == -2){
                                    highIndex.add(i);
                                }

                            } else if (emolist.get(i).equals("anger")) {

                                if( high < 1){
                                    str = "분노";
                                    Log.v("태그", "여기옴1");
                                    highIndex.clear();
                                    highIndex.add(i);
                                    high = 1;
                                }else if(high == 1){
                                    highIndex.add(i);
                                }
                            }
                        } // 감정 강도 비교

                        // 강도 큰 감정이 하나가 아니라면
                        if(highIndex.size() >= 2){

                            Log.v("태그", "highIndex 사이즈 : "+highIndex.size());
                            // todo 지우기
                            for(int i=0; i<highIndex.size(); i++){
                                Log.v("태그","highIndex : " + highIndex.get(i));
                            }

                            str = "";
                            ArrayList highPosition = new ArrayList(); // 가장 높은 품사 단어 위치 저장
                            int highPos = -1; // 가장 높은 품사 저장하는 변수 [명사:4 / 동사:3 / 형용사:2 / 부사:1 }
                            int position = -1;

                            //품사 비교
                            for(int i=0; i<highIndex.size(); i++){

                                int temp = Integer.valueOf( highIndex.get(i).toString() );
                                Log.v("태그", "temp : "+temp);

                                if( pos.get(temp).toString().contains("N") ) {
                                    Log.v("태그", "명사");

                                    if(highPos < 4){
                                        highPosition.clear();
                                        highPos = 4;
                                        position = temp;
                                        Log.v("태그", "position1 : "+position);
                                    } else if(highPos == 4){
                                        highPosition.add(temp);
                                    }
                                }else if( pos.get(temp).toString().contains("VV") || pos.get(temp).toString().contains("XR")) {
                                    Log.v("태그", "동사");
                                    if(highPos < 3){
                                        highPosition.clear();
                                        highPos = 3;
                                        position = temp;
                                        Log.v("태그", "position2 : "+position);
                                    } else if(highPos == 3){
                                        highPosition.add(temp);
                                    }
                                }else if( pos.get(temp).toString().contains("VA") ) {
                                    Log.v("태그", "형용사");
                                    if(highPos < 2){
                                        highPosition.clear();
                                        highPos = 2;
                                        position = temp;
                                        Log.v("태그", "position3 : "+position);
                                    } else if(highPos == 2){
                                        highPosition.add(temp);
                                    }
                                }else if( pos.get(temp).toString().contains("MAG") ) {
                                    Log.v("태그", "부사");
                                    if(highPos < 1){
                                        highPosition.clear();
                                        highPos = 1;
                                        position = temp;
                                        Log.v("태그", "position4 : "+position);
                                    } else if(highPos == 1){
                                        highPosition.add(temp);
                                    }
                                }
                            }
                            // 품사 비교 끝

                            if(highPosition.size() == 0){
                                str = "";
                                switch (emolist.get(position).toString()){
                                    case "joy":
                                        str = "기쁨 ";
                                        break;
                                    case "fear":
                                        str = "두려움 ";
                                        break;
                                    case "sadness":
                                        str = "슬픔 ";
                                        break;
                                        case "disgust":
                                        str = "무기력 ";
                                        break;
                                        case "anger":
                                        str = "분노 ";
                                        break;
                                }
                            }else{
                                str = "";
                                // 첫 번째 위치 감정으로 설정
                                switch (emolist.get(Integer.valueOf( highPosition.get(0).toString() )).toString()) {
                                    case "joy":
                                        str = "기쁨 ";
                                        break;
                                    case "fear":
                                        str = "두려움 ";
                                        break;
                                    case "sadness":
                                        str = "슬픔 ";
                                        break;
                                    case "disgust":
                                        str = "무기력 ";
                                        break;
                                    case "anger":
                                        str = "분노 ";
                                        break;
                                }

                            }

                        } // 품사 비교 끝

                        for(int i=0; i<emolist.size(); i++){
                            if(emolist.get(i).equals("+")){
                                str = "많이 " +str;
                            }else if(emolist.get(i).equals("-")){
                                str = "조금 " +str;
                            }
                        }
                    } // 감정 표현이 여러개일 때 끝

                    emotion.setText(str);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("stress", stress);
                            bundle.putString("strEmotion", str);
                            bundle.putParcelableArrayList("emotion", emolist);

                            fragment.setArguments(bundle);

                            ((MainActivity)getActivity()).replaceFragment(fragment);
                        }
                    },3000);
                }else {
                    // 감정 표현이 없을 경우
                    emolist.clear();
                    pos.clear();
                    str="";
                    LayoutInflater inflater = getLayoutInflater();
                    View toastDesign = inflater.inflate(R.layout.toast_design, (ViewGroup)viewGroup.findViewById(R.id.toast_design_root));
                    TextView textView = toastDesign.findViewById(R.id.toast_design_textview); // 토스트 꾸미기 위함
                    textView.setTextColor(R.color.colorPrimaryDark);

                    textView.setText("감정 언어를 다시 말해주세요");
                    Toast toast = new Toast(getContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 30);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(toastDesign);
                    toast.show();
                    //Toast.makeText(getContext(), "감정 언어를 다시 말해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return viewGroup;
    }

    private String whatIs(String text, Boolean stress){

        // csv 파일을 읽어서 resultList에 넣기
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
        // 여기까지 csv 파일을 읽어서 resultList에 넣기

        // 스트레스 유무와 단어를 가지고 감정 번호 가져오기
        for(int i=0; i<resultList.size(); i++ ){
            String[] temp = (String[]) resultList.get(i);

            if(text.equals(temp[0])){
                if(stress) { return temp[1]; }
                else{ return temp[2]; }
            }
        }
        return "0";
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
