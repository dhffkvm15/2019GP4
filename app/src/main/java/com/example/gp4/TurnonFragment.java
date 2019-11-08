package com.example.gp4;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

// 디퓨저 동작할 때
public class TurnonFragment extends Fragment {

    private Button stop; // 멈춤 버튼
    private Button save; // 저장 버튼

    private TextView timeText; // 몇 분 남았는지 보여주는 텍스트 뷰

    private Boolean isOn = true; // 켜져 있는지 확인
    private int time = 0; // 디퓨저 동작하는 시간

    private CatridgeInfo catridgeInfo1;
    private CatridgeInfo catridgeInfo2;
    private CatridgeInfo catridgeInfo3;
    private CatridgeInfo catridgeInfo4;
    private CatridgeInfo catridgeInfo5;
    private CatridgeInfo catridgeInfo6;
    private CatridgeInfo[] catridgeInfos = {};

    private String pushId; // 사용자 키 값
    private List<TotalInfo> totalInfoList;

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
        final ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_turnon, container, false);

        stop = (Button) viewGroup.findViewById(R.id.turnon_fragment_button_stop);
        save = (Button) viewGroup.findViewById(R.id.turnon_fragment_button_save);
        timeText = (TextView)viewGroup.findViewById(R.id.turnon_fragment_textview);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        Boolean turnOn = sharedPreferences.getBoolean("turnOn", false); // 디퓨저 작동하는지 가져오기

        // todo 서비스 지우기 intent = new Intent(getActivity(), MyService.class);
        pushId = sharedPreferences.getString("pushID", ""); // 저장되어 있는 pushId 불러오기

        totalInfoList = new ArrayList<>();
        bringScent(); // 저장되어 있는 향 정보

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
        }else{
            // 플로팅 버튼을 통해 바로 들어왔을 때 catridge 정보 받아오기

            String temp = sharedPreferences.getString("before", "2/2/2/2/2/2");
            String[] arr = temp.split("/"); // 이전에 켰던 향의 세기 정보 가져오기

            String temp1 = sharedPreferences.getString("beforeName", "2/2/2/2/2/2");
            String[] arr1 = temp1.split("/"); // 이전에 켰던 향의 세기 정보 가져오기

            catridgeInfo1 = new CatridgeInfo(arr1[0], Integer.valueOf( arr[0] ));
            catridgeInfo2 = new CatridgeInfo(arr1[1], Integer.valueOf( arr[1] ));
            catridgeInfo3 = new CatridgeInfo(arr1[2], Integer.valueOf( arr[2] ));
            catridgeInfo4 = new CatridgeInfo(arr1[3], Integer.valueOf( arr[3] ));
            catridgeInfo5 = new CatridgeInfo(arr1[4], Integer.valueOf( arr[4] ));
            catridgeInfo6 = new CatridgeInfo(arr1[5], Integer.valueOf( arr[5] ));
            catridgeInfos = new CatridgeInfo[]{catridgeInfo1, catridgeInfo2, catridgeInfo3, catridgeInfo4, catridgeInfo5, catridgeInfo6};

        }

        startPlaying();

        // 동작 멈춤 버튼 클릭 시
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOn == false){
                    stop.setText("STOP");
                    isOn = true;

                    startPlaying();
                }else{
                    stop.setText("REPLAY");
                    isOn = false;

                    stopPlaying();
                }
            }
        });

        // 향 남기기 버튼 클릭했을 때
        save.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                String temp = isSave();

                if( temp.equals("0")){
                    SaveDialog saveDialog = new SaveDialog(getActivity());
                    saveDialog.saveInfo(catridgeInfos);
                }else{
                    // 이미 저장되어 있으면 알려주기!
                    LayoutInflater inflater = getLayoutInflater();
                    View toastDesign = inflater.inflate(R.layout.toast_design, (ViewGroup)viewGroup.findViewById(R.id.toast_design_root));
                    TextView textView = toastDesign.findViewById(R.id.toast_design_textview); // 토스트 꾸미기 위함
                    textView.setTextColor(R.color.colorPrimaryDark);

                    textView.setText(temp + "으로 이미 저장되어 있어요");
                   Toast toast = new Toast(getContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 30);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(toastDesign);
                    toast.show();

                }

            }
        });

        return viewGroup;
    }

    // 저장되어 있는 향 정보 저장하는 코드
    public void bringScent(){
        FirebaseDatabase.getInstance().getReference("storage").child(pushId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalInfoList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    TotalInfo totalInfo = snapshot.getValue(TotalInfo.class);
                    totalInfoList.add(totalInfo);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String isSave() {

        final String[] name = new String[1];
        name[0] = "0";

        for(int i=0; i<totalInfoList.size(); i++ ) {
            // 이미 동일한 정보가 저장되어 있으면
            if (totalInfoList.get(i).getCatridgeInfo1().getName().equals(catridgeInfo1.getName()) && totalInfoList.get(i).getCatridgeInfo1().getRest() == catridgeInfo1.getRest()
                    && totalInfoList.get(i).getCatridgeInfo2().getName().equals(catridgeInfo2.getName()) && totalInfoList.get(i).getCatridgeInfo2().getRest() == catridgeInfo2.getRest()
                    && totalInfoList.get(i).getCatridgeInfo3().getName().equals(catridgeInfo3.getName()) && totalInfoList.get(i).getCatridgeInfo3().getRest() == catridgeInfo3.getRest()
                    && totalInfoList.get(i).getCatridgeInfo4().getName().equals(catridgeInfo4.getName()) && totalInfoList.get(i).getCatridgeInfo4().getRest() == catridgeInfo4.getRest()
                    && totalInfoList.get(i).getCatridgeInfo5().getName().equals(catridgeInfo5.getName()) && totalInfoList.get(i).getCatridgeInfo5().getRest() == catridgeInfo5.getRest()
                    && totalInfoList.get(i).getCatridgeInfo6().getName().equals(catridgeInfo6.getName()) && totalInfoList.get(i).getCatridgeInfo6().getRest() == catridgeInfo6.getRest()) {

                return totalInfoList.get(i).getName();
            }
        }

        return "0";
    }

    // 디퓨저 동작하는 코드
    private void startPlaying() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        Map<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put("Motor", 1);
        taskMap.put("1Status", catridgeInfo1.getRest());
        taskMap.put("2Status", catridgeInfo2.getRest());
        taskMap.put("3Status", catridgeInfo3.getRest());
        taskMap.put("4Status", catridgeInfo4.getRest());
        taskMap.put("5Status", catridgeInfo5.getRest());
        taskMap.put("6Status", catridgeInfo6.getRest());
        databaseReference.updateChildren(taskMap);

//        intent.putExtra("time", time);
//        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
//        new Thread(new getTimeThread()).start();

        // 디퓨저가 켜져 있음을 저장하기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("turnOn", true);
        editor.commit(); // 저장 완료

    }

    // 디퓨저 작동 멈추는 코드
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

        //getActivity().unbindService(connection);

        // 디퓨저가 꺼져 있음을 저장하기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("turnOn", false);
        editor.commit(); // 저장 완료

    }

//    private class getTimeThread implements Runnable{
//        private Handler handler = new Handler();
//
//        @Override
//        public void run() {
//            while(isOn){
//                if(binder == null){
//                    continue;
//                }
//
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            timeText.setText(binder.getTime() + "분");
//                        }catch(RemoteException e){
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                try{
//                    Thread.sleep(500);
//                }catch(InterruptedException e){
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


}
