package com.example.gp4;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

class CustomDialog {
    private Context context;
    private int num;

    public CustomDialog(Context context){
        this.context = context;
        num = -1;
    }

    public void callFunction(final Button button){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 바 없애기
        dialog.setContentView(R.layout.custom_dialog);
        dialog.show(); // 다이얼로그 보여주기

        final Spinner spinner = (Spinner)dialog.findViewById(R.id.custom_dialog_spinner);
        final Button okbutton = (Button)dialog.findViewById(R.id.custom_dialog_button_ok);
        final Button xbutton = (Button)dialog.findViewById(R.id.custom_dialog_button_cancel);


        // 스피너 클릭 시
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                num = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                num = 0; // 제일 첫 번째 배열 값으로
            }
        });

        // 확인 버튼 클릭 시
        okbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String[] tmpScent = context.getResources().getStringArray(R.array.scentname);

                button.setText(tmpScent[num].toString()); // 버튼 텍스트 변경

                Drawable drawable = context.getResources().getDrawable(R.drawable.circlebutton);

                 //todo 향 이름 변경하기 및 색상 변경하기
                switch (tmpScent[num].toString()){
                    case "A":
                        drawable.setColorFilter(0xFF9232CC, PorterDuff.Mode.SRC_ATOP);
                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            button.setBackgroundDrawable(drawable);
                        } else {
                            button.setBackground(drawable);
                        }
                        break;
                    case "B":
                        drawable.setColorFilter(0xFFADC8E6, PorterDuff.Mode.SRC_ATOP);
                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            button.setBackgroundDrawable(drawable);
                        } else {
                            button.setBackground(drawable);
                        }
                        break;
                    case "C":
                        drawable.setColorFilter(0xFFF08080, PorterDuff.Mode.SRC_ATOP);
                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            button.setBackgroundDrawable(drawable);
                        } else {
                            button.setBackground(drawable);
                        }
                        break;
                    case "D":
                        drawable.setColorFilter(0xFFFFB6C1, PorterDuff.Mode.SRC_ATOP);
                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            button.setBackgroundDrawable(drawable);
                        } else {
                            button.setBackground(drawable);
                        }
                        break;
                    case "E":
                        drawable.setColorFilter(0xFFFFFF00, PorterDuff.Mode.SRC_ATOP);
                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            button.setBackgroundDrawable(drawable);
                        } else {
                            button.setBackground(drawable);
                        }
                        break;
                    case "F":
                        drawable.setColorFilter(0xFFFF0000, PorterDuff.Mode.SRC_ATOP);
                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            button.setBackgroundDrawable(drawable);
                        } else {
                            button.setBackground(drawable);
                        }
                        break;

                }
                dialog.dismiss(); // 다이얼로그 종료
            }
        });

        // 취소 버튼 클릭 시
        xbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // 다이얼로그 종료
            }
        });
    }

}
