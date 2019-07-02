package com.example.gp4;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

/* 카트리지에 향 종류를 입력하는 다이얼 로그 */
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
        dialog.setCancelable(false); // 백버튼 막기
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
                String[] scentColor = context.getResources().getStringArray(R.array.scentcolor); // 컬러 색상 가져오기

                button.setText(tmpScent[num].toString()); // 버튼 텍스트 변경

                Drawable drawable = context.getResources().getDrawable(R.drawable.circlebutton);

                 //todo 향 이름 변경하기 및 색상 변경하기
                // 향 이름에 따라 버튼 색상 변경
                String tmpColor = "#FF";
                switch (tmpScent[num].toString()){
                    case "A":
                        tmpColor += scentColor[0];
                        break;
                    case "B":
                        tmpColor += scentColor[1];
                        break;
                    case "C":
                        tmpColor += scentColor[2];
                        break;
                    case "D":
                        tmpColor += scentColor[3];
                        break;
                    case "E":
                        tmpColor += scentColor[4];
                        break;
                    case "F":
                        tmpColor += scentColor[5];
                        break;

                }
                drawable.setColorFilter(Color.parseColor(tmpColor), PorterDuff.Mode.SRC_ATOP);
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    button.setBackgroundDrawable(drawable);
                } else {
                    button.setBackground(drawable);
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
