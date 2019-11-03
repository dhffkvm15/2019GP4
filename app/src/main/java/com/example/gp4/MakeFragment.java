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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

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

    private SeekBar[] seekBars = {};
    private SeekBar seekBar1;
    private SeekBar seekBar2;
    private SeekBar seekBar3;
    private SeekBar seekBar4;
    private SeekBar seekBar5;
    private SeekBar seekBar6;
    private SeekBar timeSeekBar;

    private int time = 120; // 동작 시간을 저장할 변수
    private TextView timeView;

    private CatridgeInfo[] catridgeInfos = {};
    private CatridgeInfo catridgeInfo1 = new CatridgeInfo("", 2);
    private CatridgeInfo catridgeInfo2 = new CatridgeInfo("", 2);
    private CatridgeInfo catridgeInfo3 = new CatridgeInfo("", 2);
    private CatridgeInfo catridgeInfo4 = new CatridgeInfo("", 2);
    private CatridgeInfo catridgeInfo5 = new CatridgeInfo("", 2);
    private CatridgeInfo catridgeInfo6 = new CatridgeInfo("", 2);

    private Button turnon;

    private String pushId; // 사용자의 푸시 아이디 값
    private SharedPreferences sharedPreferences;

    public static MakeFragment newInstance(){
        return new MakeFragment();
    }

    private android.content.res.Resources resources;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_make, container, false);

        resources = getActivity().getResources();

        sharedPreferences = this.getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        pushId = sharedPreferences.getString("pushID", ""); // 저장되어 있는 pushId 불러오기

        ButtonOnclick buttonOnclick = new ButtonOnclick();
        button1 = (Button) viewGroup.findViewById(R.id.fragment_make_button1);
        button1.setOnClickListener(buttonOnclick);
        button2 = (Button) viewGroup.findViewById(R.id.fragment_make_button2);
        button2.setOnClickListener(buttonOnclick);
        button3 = (Button) viewGroup.findViewById(R.id.fragment_make_button3);
        button3.setOnClickListener(buttonOnclick);
        button4 = (Button) viewGroup.findViewById(R.id.fragment_make_button4);
        button4.setOnClickListener(buttonOnclick);
        button5 = (Button) viewGroup.findViewById(R.id.fragment_make_button5);
        button5.setOnClickListener(buttonOnclick);
        button6 = (Button) viewGroup.findViewById(R.id.fragment_make_button6);
        button6.setOnClickListener(buttonOnclick);
        buttons = new Button[]{button1, button2, button3, button4, button5, button6};

        textView1 = (TextView) viewGroup.findViewById(R.id.make_fragment_textview1);
        textView2 = (TextView) viewGroup.findViewById(R.id.make_fragment_textview2);
        textView3 = (TextView) viewGroup.findViewById(R.id.make_fragment_textview3);
        textView4 = (TextView) viewGroup.findViewById(R.id.make_fragment_textview4);
        textView5 = (TextView) viewGroup.findViewById(R.id.make_fragment_textview5);
        textView6 = (TextView) viewGroup.findViewById(R.id.make_fragment_textview6);
        textViews = new TextView[]{textView1, textView2, textView3, textView4, textView5, textView6};

        // 여기서 카트리지 인포의 rest 값은 약, 강을 의미
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
        seekBars = new SeekBar[]{seekBar1, seekBar2, seekBar3, seekBar4, seekBar5, seekBar6};

        timeSeekBar = (SeekBar) viewGroup.findViewById(R.id.make_fragment_seekbar_time);
        timeView = (TextView) viewGroup.findViewById(R.id.make_fragment_textview_time);

        turnon = (Button) viewGroup.findViewById(R.id.make_fragment_turnon_button);
        Boolean isOn = sharedPreferences.getBoolean("turnOn", false); // 디퓨저 작동하는지 가져오기
        // 디퓨저 동작 중이면 동작 버튼 클릭 불가능하도록
        if(isOn){
            turnon.setEnabled(false);
        }else{
            turnon.setEnabled(true);
        }

        settingButton(); // 각 향의 종류, 잔량 등을 표시해주는 함수

        // 시간 설정하는 시크바 작동시킬 때
        timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                time = progress;
                timeView.setText(String.valueOf(time) + "분");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // 동작 버튼 눌렀을 때
        turnon.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                boolean bool = true;

                if(time == 0){
                    bool = false;
                    LayoutInflater inflater = getLayoutInflater();
                    View toastDesign = inflater.inflate(R.layout.toast_design, (ViewGroup)viewGroup.findViewById(R.id.toast_design_root));
                    TextView textView = toastDesign.findViewById(R.id.toast_design_textview); // 토스트 꾸미기 위함
                    textView.setTextColor(R.color.colorPrimaryDark);

                    textView.setText("시간 설정이 잘못되었습니다.");
                    Toast toast = new Toast(getContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 30);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(toastDesign);
                    toast.show();
                   // Toast.makeText(getContext(), "시간 설정이 잘못되었습니다.", Toast.LENGTH_LONG).show();
                }

                if( bool == true && catridgeInfo1.getRest() == 0 && catridgeInfo2.getRest() == 0 && catridgeInfo3.getRest() ==0
                        && catridgeInfo4.getRest() == 0 && catridgeInfo5.getRest() == 0 && catridgeInfo6.getRest() == 0 ){
                    bool = false;
                    LayoutInflater inflater = getLayoutInflater();
                    View toastDesign = inflater.inflate(R.layout.toast_design, (ViewGroup)viewGroup.findViewById(R.id.toast_design_root));
                    TextView textView = toastDesign.findViewById(R.id.toast_design_textview); // 토스트 꾸미기 위함
                    textView.setTextColor(R.color.colorPrimaryDark);

                    textView.setText("카트리지가 모두 닫혀있습니다.");
                    Toast toast = new Toast(getContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 30);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(toastDesign);
                    toast.show();
                    //Toast.makeText(getContext(), "카트리지가 모두 닫혀있습니다.", Toast.LENGTH_LONG).show();
                }

                // 동작시켜야 할 때
                if(bool){

                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference();

                    Map<String, Object> taskMap = new HashMap<String, Object>();
                    taskMap.put("1Status", catridgeInfo1.getRest());
                    taskMap.put("2Status", catridgeInfo2.getRest());
                    taskMap.put("3Status", catridgeInfo3.getRest());
                    taskMap.put("4Status", catridgeInfo4.getRest());
                    taskMap.put("5Status", catridgeInfo5.getRest());
                    taskMap.put("6Status", catridgeInfo6.getRest());
                    taskMap.put("Time", time); // 동작 시간 설정

                    databaseReference.updateChildren(taskMap); // 아두이노 구멍 열기

                    // 이전에 켰었던 향 정보 저장
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String temp = String.valueOf(catridgeInfo1.getRest()) + "/"
                            + String.valueOf(catridgeInfo2.getRest()) + "/"
                            + String.valueOf(catridgeInfo3.getRest()) + "/"
                            + String.valueOf(catridgeInfo4.getRest()) + "/"
                            + String.valueOf(catridgeInfo5.getRest()) + "/"
                            + String.valueOf(catridgeInfo6.getRest());

                    editor.putString("before", temp);
                    editor.commit(); // 이전에 켰었던 정보 저장

                    Fragment turnonfragment = new TurnonFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("obj1", catridgeInfo1);
                    bundle.putSerializable("obj2", catridgeInfo2);
                    bundle.putSerializable("obj3", catridgeInfo3);
                    bundle.putSerializable("obj4", catridgeInfo4);
                    bundle.putSerializable("obj5", catridgeInfo5);
                    bundle.putSerializable("obj6", catridgeInfo6);
                    bundle.putInt("time", time);
                    turnonfragment.setArguments(bundle);

                    ((MainActivity)getActivity()).replaceFragment(turnonfragment);
                }

            }
        });

        return viewGroup;
    }

    // 각 향의 종류, 잔량 등을 표시해주는 함수
    public void settingButton() {
        final ArrayList<String> datas = new ArrayList<String>();
        final ArrayList<Integer> rest = new ArrayList<Integer>();

        final String[] scentColor = getContext().getResources().getStringArray(R.array.scentcolor); // 컬러 색상 가져오기

        final String temp = sharedPreferences.getString("before", "2/2/2/2/2/2");
        final String[] arr = temp.split("/"); // 이전에 켰던 향 정보 가져오기

        if(pushId.equals("")){ // 저장된 정보가 없으면
            Log.v("태그", "태그 확인 : 저장 정보 없음");
        }else{
            // 향 정보 받아오기
            FirebaseDatabase.getInstance().getReference("catridge").child(pushId).addValueEventListener(new ValueEventListener() {
                @SuppressLint("Range")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    datas.clear();
                    rest.clear();

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



                        seekBars[i].setProgress(Integer.valueOf(arr[i])); // 이전에 켰던 향 정보로 불러오기, 이전에 저장된 정보가 없을 시 2단계로 지정
                        seekBars[i].setEnabled(true); // 초기화

                        if(rest.get(i) == 0){ // 잔량이 없을 경우
                            seekBars[i].setProgress(0); // 0으로 프로그레스 바 이동
                            seekBars[i].setEnabled(false); // 못 움직이도록
                            catridgeInfos[i].setRest(0);
                        }

                        buttons[i].setText(datas.get(i)); // 버튼에 향 종류 표시

                        switch (i){
                            case 0:
                                tmpColor = "#" + clearness(rest.get(i)) + scentColor[0];
                                break;
                            case 1:
                                tmpColor = "#" + clearness(rest.get(i)) + scentColor[1];
                                break;
                            case 2:
                                tmpColor = "#" + clearness(rest.get(i)) + scentColor[2];
                                break;
                            case 3:
                                tmpColor = "#" + clearness(rest.get(i)) + scentColor[3];
                                break;
                            case 4:
                                tmpColor = "#" + clearness(rest.get(i)) + scentColor[4];
                                break;
                            case 5:
                                tmpColor = "#" + clearness(rest.get(i)) + scentColor[5];
                                break;
                        }

                        drawable = resources.getDrawable(R.drawable.circlebutton2);
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

    // 향 비율 조정하는 시크바 동작할 때
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

    // 카트리지 향 클릭 할 때, 카트리지 정보 교체
    class ButtonOnclick implements Button.OnClickListener{

        Map<String, Object> taskMap = new HashMap<String, Object>();
        CatridgeInfo catridgeInfo = new CatridgeInfo();
        @Override
        public void onClick(View v) {
            CustomDialog customDialog = new CustomDialog(getActivity());
            switch (v.getId()){
                case R.id.fragment_make_button1:
                    customDialog.callFunction(button1,1);
                    customDialog.setDialogListener(new CustomDialog.CustomDialogListener() {
                        @Override
                        public void onPositiveClicked(String name) {
                            catridgeInfo = new CatridgeInfo(name, 100);
                            taskMap.put("1", catridgeInfo);
                            FirebaseDatabase.getInstance().getReference("catridge").child(pushId).updateChildren(taskMap);
                        }

                        @Override
                        public void onNegativeClicked() {

                        }
                    });

                    break;
                case R.id.fragment_make_button2 :
                    customDialog.callFunction(button2,2);
                    customDialog.setDialogListener(new CustomDialog.CustomDialogListener() {
                        @Override
                        public void onPositiveClicked(String name) {
                            catridgeInfo = new CatridgeInfo(name, 100);
                            taskMap.put("2", catridgeInfo);
                            FirebaseDatabase.getInstance().getReference("catridge").child(pushId).updateChildren(taskMap);
                        }

                        @Override
                        public void onNegativeClicked() {

                        }
                    });
                    break;
                case R.id.fragment_make_button3 :
                    customDialog.callFunction(button3,3);
                    customDialog.setDialogListener(new CustomDialog.CustomDialogListener() {
                        @Override
                        public void onPositiveClicked(String name) {
                            catridgeInfo = new CatridgeInfo(name, 100);
                            taskMap.put("3", catridgeInfo);
                            FirebaseDatabase.getInstance().getReference("catridge").child(pushId).updateChildren(taskMap);
                        }

                        @Override
                        public void onNegativeClicked() {

                        }
                    });

                    break;
                case R.id.fragment_make_button4 :
                       customDialog.callFunction(button4,4);
                    customDialog.setDialogListener(new CustomDialog.CustomDialogListener() {
                        @Override
                        public void onPositiveClicked(String name) {
                            catridgeInfo = new CatridgeInfo(name, 100);
                            taskMap.put("4", catridgeInfo);
                            FirebaseDatabase.getInstance().getReference("catridge").child(pushId).updateChildren(taskMap);
                        }

                        @Override
                        public void onNegativeClicked() {

                        }
                    });
                    break;
                case R.id.fragment_make_button5 :
                    customDialog.callFunction(button5,5) ;
                    customDialog.setDialogListener(new CustomDialog.CustomDialogListener() {
                        @Override
                        public void onPositiveClicked(String name) {
                            catridgeInfo = new CatridgeInfo(name, 100);
                            taskMap.put("5", catridgeInfo);
                            FirebaseDatabase.getInstance().getReference("catridge").child(pushId).updateChildren(taskMap);
                        }

                        @Override
                        public void onNegativeClicked() {

                        }
                    });

                    break;
                case R.id.fragment_make_button6 :
                    customDialog.callFunction(button6,6);
                    customDialog.setDialogListener(new CustomDialog.CustomDialogListener() {
                        @Override
                        public void onPositiveClicked(String name) {
                            catridgeInfo = new CatridgeInfo(name, 100);
                            taskMap.put("6", catridgeInfo);
                            FirebaseDatabase.getInstance().getReference("catridge").child(pushId).updateChildren(taskMap);
                        }

                        @Override
                        public void onNegativeClicked() {

                        }
                    });
                    break;
            }


        }
    }


}

