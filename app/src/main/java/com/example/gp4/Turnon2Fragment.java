package com.example.gp4;

import android.content.SharedPreferences;
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

    public static Turnon2Fragment newInstance(){
        return new Turnon2Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_turnon2, container, false);

        seekBar = (SeekBar)viewGroup.findViewById(R.id.turnon2fragment_seekbar_time);
        time = (TextView)viewGroup.findViewById(R.id.turnon2fragment_textview_time);
        play = (Button)viewGroup.findViewById(R.id.turnon2fragment_button_play);

        Bundle bundle = getArguments();
        totalInfo = (TotalInfo) bundle.getSerializable("total");

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
            @Override
            public void onClick(View v) {

                if(howlong == 0){
                    Toast.makeText(getActivity().getApplicationContext(), "시간을 잘못입력하셨습니다.", Toast.LENGTH_LONG).show();
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

    // TODO 디퓨저 멈추는 코드
    private void stopPlaying() {

        Log.v("디퓨저", "멈춤");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        Map<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put("Motor", 0);
        databaseReference.updateChildren(taskMap);

        // 디퓨저가 꺼져 있음을 저장하기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("turnOn", false);
        editor.commit(); // 저장 완료);
    }

    // TODO 디퓨저 작동하는 코드, 잔량 계산 필요
    private void startPlaying() {

        Log.v("디퓨저", "작동");

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

        databaseReference.updateChildren(taskMap);

        // 디퓨저가 켜져 있음을 저장하기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("turnOn", true);
        editor.commit(); // 저장 완료);
    }
}
