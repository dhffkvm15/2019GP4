package com.example.gp4;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

// 디퓨저 동작할 때
public class TurnonFragment extends Fragment {

    private Button stop; // 멈춤 버튼
    private Button save; // 저장 버튼

    private TextView timeText; // 몇 분 남았는지 보여주는 텍스트 뷰

    private Boolean isOn = true; // 켜져 있는지 확인
    private int time = 0; // 디퓨저 동작하는 시간

    private CatridgeInfo[] catridgeInfos = {};
    private CatridgeInfo catridgeInfo1;
    private CatridgeInfo catridgeInfo2;
    private CatridgeInfo catridgeInfo3;
    private CatridgeInfo catridgeInfo4;
    private CatridgeInfo catridgeInfo5;
    private CatridgeInfo catridgeInfo6;

    private Intent intent; // 서비스 실행하기 위함

    private IMyTimerService binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 서비스가 가진 binder 리턴 받기
            binder = IMyTimerService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    public static TurnonFragment newInstance(){
        return new TurnonFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_turnon, container, false);

        stop = (Button) viewGroup.findViewById(R.id.turnon_fragment_button_stop);
        save = (Button) viewGroup.findViewById(R.id.turnon_fragment_button_save);
        timeText = (TextView)viewGroup.findViewById(R.id.turnon_fragment_textview);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        Boolean turnOn = sharedPreferences.getBoolean("turnOn", false); // 디퓨저 작동하는지 가져오기

        intent = new Intent(getActivity(), MyService.class);

        // 꺼져 있으면
        if( !turnOn ){
            Bundle bundle = getArguments();
            time = bundle.getInt("time");
            timeText.setText(String.valueOf(time) +"분"); // 동작 시간 화면에 표시

            // 향 저장을 위해 필요
            catridgeInfo1 = (CatridgeInfo) bundle.getSerializable("obj1");
            catridgeInfo2 = (CatridgeInfo) bundle.getSerializable("obj2");
            catridgeInfo3 = (CatridgeInfo) bundle.getSerializable("obj3");
            catridgeInfo4 = (CatridgeInfo) bundle.getSerializable("obj4");
            catridgeInfo5 = (CatridgeInfo) bundle.getSerializable("obj5");
            catridgeInfo6 = (CatridgeInfo) bundle.getSerializable("obj6");
            catridgeInfos = new CatridgeInfo[]{catridgeInfo1, catridgeInfo2, catridgeInfo3, catridgeInfo4, catridgeInfo5, catridgeInfo6};
        }


        startPlaying();

        ((MainActivity)getActivity()).getBottomNavigationView().setEnabled(false);

        // 동작 멈춤 버튼 클릭 시
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOn == false){
                    stop.setText("STOP");
                    isOn = true;

                    startPlaying();
                }else{
                    ((MainActivity)getActivity()).getBottomNavigationView().setEnabled(true);
                    stop.setText("REPLAY");
                    isOn = false;

                    stopPlaying();
                }
            }
        });

        // 향 남기기 버튼 클릭했을 때
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveDialog saveDialog = new SaveDialog(getActivity());
                saveDialog.saveInfo(catridgeInfos);
            }
        });


        return viewGroup;
    }

    // 디퓨저 동작하는 코드
    private void startPlaying() {
        //Log.v("디퓨저", "작동");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        Map<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put("Motor", 1);
        databaseReference.updateChildren(taskMap);

        //getActivity().startService(intent);
        intent.putExtra("time", time);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        new Thread(new getTimeThread()).start();

        // 디퓨저가 켜져 있음을 저장하기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("turnOn", true);
        editor.commit(); // 저장 완료

        // 작동 멈춤 작동 했을 때, 작동한 시간만큼 원래 작동하고자 했던 시간에서 빼야하는지
    }

    // 디퓨저 작동 멈추는 코드
    private void stopPlaying() {
        //Log.v("디퓨저", "작동 멈춤");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        Map<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put("Motor", 0);
        databaseReference.updateChildren(taskMap);

        //getActivity().stopService(intent);
        getActivity().unbindService(connection);

        // 디퓨저가 꺼져 있음을 저장하기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("turnOn", false);
        editor.commit(); // 저장 완료

    }

    private class getTimeThread implements Runnable{
        private Handler handler = new Handler();

        @Override
        public void run() {
            while(isOn){
                if(binder == null){
                    continue;
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            timeText.setText(binder.getTime() + "분");
                        }catch(RemoteException e){
                            e.printStackTrace();
                        }
                    }
                });
                try{
                    Thread.sleep(500);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }


}
