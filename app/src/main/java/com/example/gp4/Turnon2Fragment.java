package com.example.gp4;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class Turnon2Fragment extends Fragment {

    private SeekBar seekBar;
    private TextView time;
    private Button play;
    private int howlong = 120; // 디퓨저 작동 시간
    private boolean isplay = false; // 현재 작동하고 있는지 여부
    private TotalInfo totalInfo;
    private TextView name;

    public static Turnon2Fragment newInstance(){
        return new Turnon2Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_turnon2, container, false);

        seekBar = (SeekBar)viewGroup.findViewById(R.id.turnon2fragment_seekbar_time);
        time = (TextView)viewGroup.findViewById(R.id.turnon2fragment_textview_time);
        play = (Button)viewGroup.findViewById(R.id.turnon2fragment_button_play);
        name = (TextView)viewGroup.findViewById(R.id.turnon2fragment_textview_name);

        Bundle bundle = getArguments();
        totalInfo = (TotalInfo) bundle.getSerializable("total");

        name.setText(totalInfo.getName());

        // 시크바 동작할 때
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                howlong = progress;
                time.setText(String.valueOf(howlong) + "분");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Play 버튼 누를 때
        play.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                if(howlong == 0){
                    LayoutInflater inflater = getLayoutInflater();
                    View toastDesign = inflater.inflate(R.layout.toast_design, (ViewGroup)viewGroup.findViewById(R.id.toast_design_root));
                    TextView textView = toastDesign.findViewById(R.id.toast_design_textview); // 토스트 꾸미기 위함
                    textView.setTextColor(R.color.colorPrimaryDark);

                    textView.setText("시간을 잘못입력하셨습니다.");
                    Toast toast = new Toast(getContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 30);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(toastDesign);
                    toast.show();
                }else{

                    if(isplay == false){
                        play.setText("STOP");
                        isplay = true;
                        startPlaying();
                    }else{
                        play.setText("PLAY");
                        isplay = false;
                        stopPlaying();
                    }

                }
            }
        });


        return viewGroup;
    }

    private void stopPlaying() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        Map<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put("Motor", 0);
        taskMap.put("1Status", 0);
        taskMap.put("2Status", 0);
        taskMap.put("3Status", 0);
        taskMap.put("4Status", 0);
        taskMap.put("5Status", 0);
        taskMap.put("6Status", 0);
        databaseReference.updateChildren(taskMap);

        // 디퓨저가 꺼져 있음을 저장하기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("turnOn", false);
        editor.commit(); // 저장 완료);
    }

    private void startPlaying() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        Map<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put("1Status", totalInfo.getCatridgeInfo1().getRest());
        taskMap.put("2Status", totalInfo.getCatridgeInfo2().getRest());
        taskMap.put("3Status", totalInfo.getCatridgeInfo3().getRest());
        taskMap.put("4Status", totalInfo.getCatridgeInfo4().getRest());
        taskMap.put("5Status", totalInfo.getCatridgeInfo5().getRest());
        taskMap.put("6Status", totalInfo.getCatridgeInfo6().getRest());
        taskMap.put("Motor", 1);
        taskMap.put("Time", howlong); // 동작 시간 설정

        databaseReference.updateChildren(taskMap);

        // 디퓨저가 켜져 있음을 저장하기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("turnOn", true);
        editor.commit(); // 저장 완료);
    }
}
