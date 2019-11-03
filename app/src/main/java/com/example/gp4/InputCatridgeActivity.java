package com.example.gp4;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
    private Button[] buttons;
    private long lastTimeBackPressed; // 뒤로가기 버튼이 클릭된 시간

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_catridge);

        init(); // 기본 설정 및 버튼 클릭 시 다이얼로그 띄우기
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
        buttons = new Button[]{button1, button2, button3, button4, button5, button6};
    }

    // 확인(register) 버튼 클릭 시
    @SuppressLint("ResourceAsColor")
    public void OKregister(View view) {
        int stressNum = 0; // 사용자가 입력한 스트레스 향의 개수 저장할 변수
        int storeNum = 0; // 사용자가 입력한 스트레스 향의 위치 저장할 변수
        int noPut = 0; // 사용자가 입력하지 않은 향의 개수 저장할 변수

        // 입력하지 않은 버튼 개수 구하기
        for(int i=0; i<buttons.length; i++){
            if(buttons[i].getText().toString().equals("입력")){
                noPut++;
            }
        }

        if( noPut != 0){
            // 빈 칸이 있을 경우
            LayoutInflater inflater = getLayoutInflater();
            View toastDesign = inflater.inflate(R.layout.toast_design, (ViewGroup)findViewById(R.id.toast_design_root));
            TextView textView = toastDesign.findViewById(R.id.toast_design_textview); // 토스트 꾸미기 위함
            textView.setTextColor(R.color.colorPrimaryDark);

            textView.setText( String.valueOf(noPut) +"개의 빈 칸이 있습니다.");
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 30);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(toastDesign);
            toast.show();
        }else {
            // 빈 칸이 없는 경우 저장
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

            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("pushID", pushId);
            editor.commit(); // pushId 내부저장소에 저장 완료

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("inputCatridge", "yes");
            startActivity(intent);
            finish();
        }

    }

    class BtnOnClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View v) {
           CustomDialog customDialog = new CustomDialog(InputCatridgeActivity.this);
            final String[] tmpName = new String[1];
            switch (v.getId()){
                case R.id.input_catridge_button1:
                    customDialog.callFunction(button1, 1);
                    customDialog.setDialogListener(new CustomDialog.CustomDialogListener() {
                        @Override
                        public void onPositiveClicked(String name) {
                            tmpName[0] = name;
                        }

                        @Override
                        public void onNegativeClicked() {

                        }
                    });
                    break;
                case R.id.input_catridge_button2 :
                    customDialog.callFunction(button2, 2);
                    customDialog.setDialogListener(new CustomDialog.CustomDialogListener() {
                        @Override
                        public void onPositiveClicked(String name) {
                            tmpName[0] = name;
                        }

                        @Override
                        public void onNegativeClicked() {

                        }
                    });
                    break;
                case R.id.input_catridge_button3 :
                    customDialog.callFunction(button3, 3);
                    customDialog.setDialogListener(new CustomDialog.CustomDialogListener() {
                        @Override
                        public void onPositiveClicked(String name) {
                            tmpName[0] = name;
                        }

                        @Override
                        public void onNegativeClicked() {

                        }
                    });
                    break;
                case R.id.input_catridge_button4 :
                    customDialog.callFunction(button4, 4);
                    customDialog.setDialogListener(new CustomDialog.CustomDialogListener() {
                        @Override
                        public void onPositiveClicked(String name) {
                            tmpName[0] = name;
                        }

                        @Override
                        public void onNegativeClicked() {

                        }
                    });
                    break;
                case R.id.input_catridge_button5 :
                   customDialog.callFunction(button5, 5);
                    customDialog.setDialogListener(new CustomDialog.CustomDialogListener() {
                        @Override
                        public void onPositiveClicked(String name) {
                            tmpName[0] = name;
                        }

                        @Override
                        public void onNegativeClicked() {

                        }
                    });
                    break;
                case R.id.input_catridge_button6 :
                    customDialog.callFunction(button6, 6);
                    customDialog.setDialogListener(new CustomDialog.CustomDialogListener() {
                        @Override
                        public void onPositiveClicked(String name) {
                            tmpName[0] = name;
                        }

                        @Override
                        public void onNegativeClicked() {

                        }
                    });
                    break;

            }
        }
    }

    // 뒤로가기 2번 클릭 시 종료
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBackPressed() {

        // 2초 이내에 뒤로가기 버튼 재 클릭시 어플 종료
        if(System.currentTimeMillis() - lastTimeBackPressed < 1000) {
            finishAffinity();
            return;
        }

        // 뒤로 한번 클릭 시 메시지
        LayoutInflater inflater = getLayoutInflater();
        View toastDesign = inflater.inflate(R.layout.toast_design, (ViewGroup)findViewById(R.id.toast_design_root));
        TextView textView = toastDesign.findViewById(R.id.toast_design_textview); // 토스트 꾸미기 위함
        textView.setTextColor(R.color.colorPrimaryDark);

        textView.setText("'뒤로'버튼 한번 더 누르시면 앱이 종료됩니다.");
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 30);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastDesign);
        toast.show();

        lastTimeBackPressed = System.currentTimeMillis();
    }
}
