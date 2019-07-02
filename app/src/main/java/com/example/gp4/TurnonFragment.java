package com.example.gp4;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// 디퓨저 동작할 때
public class TurnonFragment extends Fragment {

    private Button stop; // 멈춤 버튼
    private Button save; // 저장 버튼

    private Boolean isOn = true; // 켜져 있는지 확인
    private int time = 0; // 디퓨저 동작하는 시간

    private CatridgeInfo[] catridgeInfos = {};
    private CatridgeInfo catridgeInfo1;
    private CatridgeInfo catridgeInfo2;
    private CatridgeInfo catridgeInfo3;
    private CatridgeInfo catridgeInfo4;
    private CatridgeInfo catridgeInfo5;
    private CatridgeInfo catridgeInfo6;


    public static TurnonFragment newInstance(){
        return new TurnonFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_turnon, container, false);

        stop = (Button) viewGroup.findViewById(R.id.turnon_fragment_button_stop);
        save = (Button) viewGroup.findViewById(R.id.turnon_fragment_button_save);

        Bundle bundle = getArguments();
        catridgeInfo1 = (CatridgeInfo) bundle.getSerializable("obj1");
        catridgeInfo2 = (CatridgeInfo) bundle.getSerializable("obj2");
        catridgeInfo3 = (CatridgeInfo) bundle.getSerializable("obj3");
        catridgeInfo4 = (CatridgeInfo) bundle.getSerializable("obj4");
        catridgeInfo5 = (CatridgeInfo) bundle.getSerializable("obj5");
        catridgeInfo6 = (CatridgeInfo) bundle.getSerializable("obj6");
        time = bundle.getInt("time");
        catridgeInfos = new CatridgeInfo[]{catridgeInfo1, catridgeInfo2, catridgeInfo3, catridgeInfo4, catridgeInfo5, catridgeInfo6};

//        Log.v("태그", "전달된 것 1 : " + catridgeInfo1.getName() + " " + catridgeInfo1.getRest());
//        Log.v("태그", "전달된 것 2 : " + catridgeInfo2.getName() + " " + catridgeInfo2.getRest());
//        Log.v("태그", "전달된 것 3 : " + catridgeInfo3.getName() + " " + catridgeInfo3.getRest());
//        Log.v("태그", "전달된 것 4 : " + catridgeInfo4.getName() + " " + catridgeInfo4.getRest());
//        Log.v("태그", "전달된 것 5 : " + catridgeInfo5.getName() + " " + catridgeInfo5.getRest());
//        Log.v("태그", "전달된 것 6 : " + catridgeInfo6.getName() + " " + catridgeInfo6.getRest());
//        Log.v("태그", "전달된 시간 : "+ time);

        // TODO 디퓨저 동작
        startPlaying();

        ((MainActivity)getActivity()).getBottomNavigationView().setEnabled(false);

        // 동작 멈춤 버튼 클릭 시
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOn == false){
                    stop.setText("STOP");
                    isOn = true;
                    // TODO 다시 작동하는 코드 쓰기
                    startPlaying();
                }else{
                    ((MainActivity)getActivity()).getBottomNavigationView().setEnabled(true);
                    stop.setText("REPLAY");
                    isOn = false;
                    //Todo 작동 멈추는 코드
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
        Log.v("디퓨저", "작동");

        // 작동 멈춤 작동 했을 때, 작동한 시간만큼 원래 작동하고자 했던 시간에서 빼야하는지
    }

    // 디퓨저 작동 멈추는 코드
    private void stopPlaying() {
        Log.v("디퓨저", "작동 안함");
    }


}
