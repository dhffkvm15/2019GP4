package com.example.gp4;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.UUID;

// 로딩 화면
public class SplashActivity extends Activity {

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("확인", "확인 스플래시 액티비티");

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        // 정보 삭제를 위한 코드
      //  SharedPreferences.Editor editor = sharedPreferences.edit();
     //   editor.clear();
      //  editor.commit();
      //  Log.v("확인", "정보 삭제 확인");
        String tmpInfo = sharedPreferences.getString("Info", ""); // UserInfo 파일에서 Info에 저장되어 있는 값 불러오기

        if(tmpInfo.equals("")){ // 저장된 정보가 없으면
            startActivity(new Intent(this, RegisterActivity.class));
        }else {
            gson = new GsonBuilder().create();
            UserInfo userInfo = gson.fromJson(tmpInfo, UserInfo.class);
            Log.v("확인", "저장된 이름 : " + userInfo.getName());
            Log.v("확인", "저장된 나이 : " + userInfo.getAge());
            Log.v("확인", "저장된 성별 : " + userInfo.getSex());

            startActivity(new Intent(this, MainActivity.class));
        }

        finish();
    }
}
