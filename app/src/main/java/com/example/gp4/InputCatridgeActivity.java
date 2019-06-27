package com.example.gp4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;

import java.util.ArrayList;

/* 카트리지에 향 종류 입력하기 */
public class InputCatridgeActivity extends AppCompatActivity {

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_catridge);

        init(); //버튼 클릭 시 다이얼로그 띄우기
    }

    private void init(){
        BtnOnClickListener onClickListener = new BtnOnClickListener();

        button1 = (Button)findViewById(R.id.input_catridge_button1);
        button1.setOnClickListener(onClickListener);
        button2 = (Button)findViewById(R.id.input_catridge_button2);
        button2.setOnClickListener(onClickListener);
        button3 = (Button)findViewById(R.id.input_catridge_button3);
        button3.setOnClickListener(onClickListener);
        button4 = (Button)findViewById(R.id.input_catridge_button4);
        button4.setOnClickListener(onClickListener);
        button5 = (Button)findViewById(R.id.input_catridge_button5);
        button5.setOnClickListener(onClickListener);
        button6 = (Button)findViewById(R.id.input_catridge_button6);
        button6.setOnClickListener(onClickListener);

        register = (Button)findViewById(R.id.input_catridge_register_button);
    }

    // 확인(register) 버튼 클릭 시
    public void OKregister(View view) {
        if( button1.getText().toString().equals("z") || button2.getText().toString().equals("z") || button3.getText().toString().equals("z")
        || button4.getText().toString().equals("z") || button5.getText().toString().equals("z") || button6.getText().toString().equals("z")){
            // 선택 안 한 창이 있으면
            Toast.makeText(getApplicationContext(), "빈 칸이 있습니다.", Toast.LENGTH_LONG).show();
        }else{
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(); // 파이어베이스 불러오기
            DatabaseReference databaseReference = firebaseDatabase.getReference("catridge");
            DatabaseReference pushReference = databaseReference.push();
            pushReference.child("1").setValue(new CatridgeInfo(button1.getText().toString(), 100));
            pushReference.child("2").setValue(new CatridgeInfo(button2.getText().toString(), 100));
            pushReference.child("3").setValue(new CatridgeInfo(button3.getText().toString(), 100));
            pushReference.child("4").setValue(new CatridgeInfo(button4.getText().toString(), 100));
            pushReference.child("5").setValue(new CatridgeInfo(button5.getText().toString(), 100));
            pushReference.child("6").setValue(new CatridgeInfo(button6.getText().toString(), 100));

            String pushId = pushReference.getKey(); // key 값 가져오기
            Log.v("태그", "푸시아이디 확인 : " + pushId);

            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("pushID", pushId);
            editor.commit(); // pushId 내부저장소에 저장 완료

            startActivity(new Intent(this, MainActivity.class));
        }

    }

    class BtnOnClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View v) {
           CustomDialog customDialog = new CustomDialog(InputCatridgeActivity.this);
            switch (v.getId()){
                case R.id.input_catridge_button1:
                    customDialog.callFunction(button1);
                    break;
                case R.id.input_catridge_button2 :
                    customDialog.callFunction(button2);
                    break;
                case R.id.input_catridge_button3 :
                    customDialog.callFunction(button3);
                    break;
                case R.id.input_catridge_button4 :
                    customDialog.callFunction(button4);
                    break;
                case R.id.input_catridge_button5 :
                    customDialog.callFunction(button5);
                    break;
                case R.id.input_catridge_button6 :
                    customDialog.callFunction(button6);
                    break;

            }
        }
    }

}


// 내부저장소에 카트리지 정보 저장하기
//        ArrayList<String> arrayList = new ArrayList<String>();
//            arrayList.add(button1.getText().toString());
//            arrayList.add(button2.getText().toString());
//            arrayList.add(button3.getText().toString());
//            arrayList.add(button4.getText().toString());
//            arrayList.add(button5.getText().toString());
//            arrayList.add(button6.getText().toString()); // arrayList 배열에 각각 설정한 향 이름 넣기
//
//            if(arrayList.get(0).equals("z") || arrayList.get(1).equals("z") || arrayList.get(2).equals("z")
//                    || arrayList.get(3).equals("z") || arrayList.get(4).equals("z") || arrayList.get(5).equals("z")){ // 선택 안 한 창이 있으면
//                Toast.makeText(getApplicationContext(), "빈 칸이 있습니다.", Toast.LENGTH_LONG).show();
//            }else{
//            JSONArray jsonArray = new JSONArray();
//            for(int i=0; i<arrayList.size(); i++){
//                jsonArray.put(arrayList.get(i));
//            }
//            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("scent", jsonArray.toString());
//            editor.commit(); // 저장 완료
//
//            startActivity(new Intent(this, MainActivity.class));
//
//        }