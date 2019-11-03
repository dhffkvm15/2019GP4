package com.example.gp4;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.UUID;

// 사용자 정보 (이름, 성별, 연령) 입력 받기
public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextAge;
    private RadioGroup radioGroup;
    private Button button;
    private String name;
    private int age;
    private Boolean sex = false; // 남자 false 여자 true
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        checkCAMERAPermission(); // 카메라 권한 획득

        // 성별 선택
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.register_radio_M)
                    sex = false;
                else
                    sex = true;
            }
        });

    }

    // 기본 설정
    private void init() {
        editTextName = (EditText)findViewById(R.id.register_edittext_name);
        editTextAge = (EditText)findViewById(R.id.register_edittext_age);
        radioGroup = (RadioGroup)findViewById(R.id.register_radiogroup_sex);
        button = (Button)findViewById(R.id.register_button_register);
    }

    // 등록 버튼 클릭 시 - 사용자 정보 확인 및 값 저장
    @SuppressLint("ResourceAsColor")
    public void register(View view) {

        LayoutInflater inflater = getLayoutInflater();
        View toastDesign = inflater.inflate(R.layout.toast_design, (ViewGroup)findViewById(R.id.toast_design_root));
        TextView textView = toastDesign.findViewById(R.id.toast_design_textview); // 토스트 꾸미기 위함
        textView.setTextColor(R.color.colorPrimaryDark);

        // 이름, 나이 중 입력한 것이 없을 때,
        if( editTextName.getText().toString().length() == 0
                || editTextAge.getText().toString().length() == 0){

            textView.setText("빈 칸이 있습니다.");
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 30);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastDesign);
            toast.show();
                //Toast.makeText(getApplicationContext(), "빈 칸이 있습니다.", Toast.LENGTH_LONG).show(); - 일반 토스트 상자
        }else{ //  빈 칸이 없을 때
            name = editTextName.getText().toString();
            age = Integer.parseInt(editTextAge.getText().toString());

            //나이 범위가 아닐 때
            if(age <=0 || age >= 150){
                textView.setText("나이를 잘못입력했습니다.");
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.BOTTOM, 0, 30);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastDesign);
                toast.show();
                //Toast.makeText(getApplicationContext(), "나이를 잘못입력했습니다.", Toast.LENGTH_LONG).show(); - 일반 토스트 상자
            }else{
                UserInfo userInfo = new UserInfo(name, age, sex );

                // Gson 인스턴스 생성
                gson = new GsonBuilder().create();
                String tmpInfo = gson.toJson(userInfo, UserInfo.class);

                // 저장하기
                SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Info", tmpInfo); // JSON 으로 변환한 객체 저장
                editor.commit(); // 저장 완료

                startActivity(new Intent(this, InputCatridgeActivity.class));
                finish();
             }

        }
    }

    // 카메라 권한 획득
    public void checkCAMERAPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    1001);
        } else {
            //Log.v("태그", "카메라 허가 받음");
        }
    }
}
