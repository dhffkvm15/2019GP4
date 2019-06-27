package com.example.gp4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/* 커스터마이징 */
public class MakeFragment extends Fragment {

    private Button[] buttons = {};
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;

    private TextView[] textViews = {};
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;

    private SeekBar seekBar1;
    private SeekBar seekBar2;
    private SeekBar seekBar3;
    private SeekBar seekBar4;
    private SeekBar seekBar5;
    private SeekBar seekBar6;

    private CatridgeInfo[] catridgeInfos = {};
    private CatridgeInfo catridgeInfo1 = new CatridgeInfo("", 3);
    private CatridgeInfo catridgeInfo2 = new CatridgeInfo("", 3);
    private CatridgeInfo catridgeInfo3 = new CatridgeInfo("", 3);
    private CatridgeInfo catridgeInfo4 = new CatridgeInfo("", 3);
    private CatridgeInfo catridgeInfo5 = new CatridgeInfo("", 3);
    private CatridgeInfo catridgeInfo6 = new CatridgeInfo("", 3);

    private Button turnon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_make, container, false);
        button1 = (Button) viewGroup.findViewById(R.id.fragment_make_button1);
        button2 = (Button) viewGroup.findViewById(R.id.fragment_make_button2);
        button3 = (Button) viewGroup.findViewById(R.id.fragment_make_button3);
        button4 = (Button) viewGroup.findViewById(R.id.fragment_make_button4);
        button5 = (Button) viewGroup.findViewById(R.id.fragment_make_button5);
        button6 = (Button) viewGroup.findViewById(R.id.fragment_make_button6);
        buttons = new Button[]{button1, button2, button3, button4, button5, button6};

        textView1 = (TextView) viewGroup.findViewById(R.id.make_fragment_textview1);
        textView2 = (TextView) viewGroup.findViewById(R.id.make_fragment_textview2);
        textView3 = (TextView) viewGroup.findViewById(R.id.make_fragment_textview3);
        textView4 = (TextView) viewGroup.findViewById(R.id.make_fragment_textview4);
        textView5 = (TextView) viewGroup.findViewById(R.id.make_fragment_textview5);
        textView6 = (TextView) viewGroup.findViewById(R.id.make_fragment_textview6);
        textViews = new TextView[]{textView1, textView2, textView3, textView4, textView5, textView6};

        // 여기서 카트리지 인포의 rest 값은 약 중 강을 의미
        catridgeInfos = new CatridgeInfo[]{catridgeInfo1, catridgeInfo2, catridgeInfo3, catridgeInfo4, catridgeInfo5, catridgeInfo6};

        SBCListener sbcListener = new SBCListener();
        seekBar1 = (SeekBar) viewGroup.findViewById(R.id.make_fragment_seekbar1);
        seekBar1.setOnSeekBarChangeListener(sbcListener);
        seekBar2 = (SeekBar) viewGroup.findViewById(R.id.make_fragment_seekbar2);
        seekBar2.setOnSeekBarChangeListener(sbcListener);
        seekBar3 = (SeekBar) viewGroup.findViewById(R.id.make_fragment_seekbar3);
        seekBar3.setOnSeekBarChangeListener(sbcListener);
        seekBar4 = (SeekBar) viewGroup.findViewById(R.id.make_fragment_seekbar4);
        seekBar4.setOnSeekBarChangeListener(sbcListener);
        seekBar5 = (SeekBar) viewGroup.findViewById(R.id.make_fragment_seekbar5);
        seekBar5.setOnSeekBarChangeListener(sbcListener);
        seekBar6 = (SeekBar) viewGroup.findViewById(R.id.make_fragment_seekbar6);
        seekBar6.setOnSeekBarChangeListener(sbcListener);

        turnon = (Button) viewGroup.findViewById(R.id.make_fragment_turnon_button);

        settingButton(); // 각 향의 종류, 잔량 등을 표시해주는 함수

        // 동작 버튼 눌렀을 때
        turnon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("태그", "태그 확인 동작 버튼 누름");
                Log.v("태그", "1번째 향 정보 : " +catridgeInfo1.getName() +" " + catridgeInfo1.getRest());
                Log.v("태그", "2번째 향 정보 : " +catridgeInfo2.getName() +" " + catridgeInfo2.getRest());
                Log.v("태그", "3번째 향 정보 : " +catridgeInfo3.getName() +" " + catridgeInfo3.getRest());
                Log.v("태그", "4번째 향 정보 : " +catridgeInfo4.getName() +" " + catridgeInfo4.getRest());
                Log.v("태그", "5번째 향 정보 : " +catridgeInfo5.getName() +" " + catridgeInfo5.getRest());
                Log.v("태그", "6번째 향 정보 : " +catridgeInfo6.getName() +" " + catridgeInfo6.getRest());

            }
        });

        return viewGroup;
    }

    // 각 향의 종류, 잔량 등을 표시해주는 함수
    public void settingButton() {
        final ArrayList<String> datas = new ArrayList<String>();
        final ArrayList<Integer> rest = new ArrayList<Integer>();

        final String[] scentColor = getContext().getResources().getStringArray(R.array.scentcolor); // 컬러 색상 가져오기

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        final String tmp = sharedPreferences.getString("pushID", ""); // 저장되어 있는 향 정보 불러오기

        if(tmp.equals("")){ // 저장된 정보가 없으면
            Log.v("태그", "태그 확인 : 저장 정보 없음");
        }else{

            // 향 정보 받아오기
            FirebaseDatabase.getInstance().getReference("catridge").child(tmp).addValueEventListener(new ValueEventListener() {
                @SuppressLint("Range")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        CatridgeInfo catridgeInfo = snapshot.getValue(CatridgeInfo.class);
                        datas.add(catridgeInfo.getName());
                        rest.add(catridgeInfo.getRest());
                    } // 향 정보 받아오기

                    // 향에 따른 버튼 색 변경
                    Drawable drawable;
                    String tmpColor = "";
                    for(int i=0; i<datas.size(); i++){

                        catridgeInfos[i].setName(datas.get(i)); // 카트리지 인포에 향 종류 적기

                        textViews[i].setText(rest.get(i) + "%"); // 현재 남아 있는 잔량 표시하기
                        buttons[i].setText(datas.get(i)); // 버튼에 향 종류 표시

                        switch (datas.get(i)){
                            case "A":
                                tmpColor = "#" + clearness(rest.get(i)) + scentColor[0];
                                break;
                            case "B":
                                tmpColor = "#" + clearness(rest.get(i)) + scentColor[1];
                                break;
                            case "C":
                                tmpColor = "#" + clearness(rest.get(i)) + scentColor[2];
                                break;
                            case "D":
                                tmpColor = "#" + clearness(rest.get(i)) + scentColor[3];
                                break;
                            case "E":
                                tmpColor = "#" + clearness(rest.get(i)) + scentColor[4];
                                break;
                            case "F":
                                tmpColor = "#" + clearness(rest.get(i)) + scentColor[5];
                                break;

                        }
                        drawable = getContext().getResources().getDrawable(R.drawable.circlebutton2);
                        drawable.setColorFilter(Color.parseColor(tmpColor), PorterDuff.Mode.SRC_ATOP);
                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            buttons[i].setBackgroundDrawable(drawable);
                        } else {
                            buttons[i].setBackground(drawable);
                        } // 버튼 색상 변경
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    // 잔량에 따른 투명도를 반환하는 함수
    public String clearness(int rest){

        String string = "";

        if(rest >95 && rest <=100 ){ string = "FF";}
        else if (rest >90 && rest <=95) {string = "F2"; }
        else if (rest >85 && rest <=90) {string = "E6"; }
        else if (rest >80 && rest <=85) {string = "D9"; }
        else if (rest >75 && rest <=80) {string = "CC"; }
        else if (rest >70 && rest <=75) {string = "BF"; }
        else if (rest >65 && rest <=70) {string = "B3"; }
        else if (rest >60 && rest <=65) {string = "A6"; }
        else if (rest >55 && rest <=60) {string = "99"; }
        else if (rest >50 && rest <=55) {string = "8C"; }
        else if (rest >45 && rest <=50) {string = "80"; }
        else if (rest >40 && rest <=45) {string = "73"; }
        else if (rest >35 && rest <=40) {string = "66"; }
        else if (rest >30 && rest <=35) {string = "59"; }
        else if (rest >25 && rest <=30) {string = "4D"; }
        else if (rest >20 && rest <=25) {string = "40"; }
        else if (rest >15 && rest <=20) {string = "33"; }
        else if (rest >10 && rest <=15) {string = "26"; }
        else if (rest >5 && rest <=10) { string = "1A"; }
        else { string = "0D"; }

        return string;
    }

    // 시크바 동작할 때
    public class SBCListener implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()){
                case R.id.make_fragment_seekbar1 :
                    catridgeInfo1.setRest(progress);
                    break;
                case R.id.make_fragment_seekbar2 :
                    catridgeInfo2.setRest(progress);
                    break;
                case R.id.make_fragment_seekbar3 :
                    catridgeInfo3.setRest(progress);
                    break;
                case R.id.make_fragment_seekbar4 :
                    catridgeInfo4.setRest(progress);
                    break;
                case R.id.make_fragment_seekbar5 :
                    catridgeInfo5.setRest(progress);
                    break;
                case R.id.make_fragment_seekbar6 :
                    catridgeInfo6.setRest(progress);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}

// 내부 저장소 사용시 settingButton 함수 else 문 안
//gson = new GsonBuilder().create();
//        ArrayList<String> datas = new ArrayList<String>();
//
//        try {
//        JSONArray jsonArray = new JSONArray(tmp);
//        for(int i=0; i < jsonArray.length(); i++){
//        String scent = jsonArray.optString(i);
//        datas.add(scent);
//        }
//        } catch (JSONException e) {
//        e.printStackTrace();
//        } // 향 정보 받아오기
//
//        // 향에 따른 버튼 색 변경
//        Drawable drawable;
//        for(int i=0; i<datas.size(); i++){
//        switch (datas.get(i)){
//        case "A":
//        drawable = getContext().getResources().getDrawable(R.drawable.circlebutton);
//        drawable.setColorFilter(0xFF9232CC, PorterDuff.Mode.SRC_ATOP);
//        buttons[i].setText("A");
//        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//        buttons[i].setBackgroundDrawable(drawable);
//        } else {
//        buttons[i].setBackground(drawable);
//        }
//        break;
//        case "B":
//        drawable = getContext().getResources().getDrawable(R.drawable.circlebutton);
//        drawable.setColorFilter(0xFFADC8E6, PorterDuff.Mode.SRC_ATOP);
//        buttons[i].setText("B");
//        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//        buttons[i].setBackgroundDrawable(drawable);
//        } else {
//        buttons[i].setBackground(drawable);
//        }
//        break;
//        case "C":
//        drawable = getContext().getResources().getDrawable(R.drawable.circlebutton);
//        drawable.setColorFilter(0xFFF08080, PorterDuff.Mode.SRC_ATOP);
//        buttons[i].setText("C");
//        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//        buttons[i].setBackgroundDrawable(drawable);
//        } else {
//        buttons[i].setBackground(drawable);
//        }
//        break;
//        case "D":
//        drawable = getContext().getResources().getDrawable(R.drawable.circlebutton);
//        drawable.setColorFilter(0xFFFFB6C1, PorterDuff.Mode.SRC_ATOP);
//        buttons[i].setText("D");
//        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//        buttons[i].setBackgroundDrawable(drawable);
//        } else {
//        buttons[i].setBackground(drawable);
//        }
//        break;
//        case "E":
//        drawable = getContext().getResources().getDrawable(R.drawable.circlebutton);
//        drawable.setColorFilter(0xFFFFFF00, PorterDuff.Mode.SRC_ATOP);
//        buttons[i].setText("E");
//        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//        buttons[i].setBackgroundDrawable(drawable);
//        } else {
//        buttons[i].setBackground(drawable);
//        }
//        break;
//        case "F":
//        drawable = getContext().getResources().getDrawable(R.drawable.circlebutton);
//        drawable.setColorFilter(0xFFFF0000, PorterDuff.Mode.SRC_ATOP);
//        buttons[i].setText("F");
//        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//        buttons[i].setBackgroundDrawable(drawable);
//        } else {
//        buttons[i].setBackground(drawable);
//        }
//        break;
//
//        }
//        }