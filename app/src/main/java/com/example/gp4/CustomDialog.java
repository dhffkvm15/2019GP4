package com.example.gp4;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/* 카트리지에 향 종류를 입력하는 다이얼 로그 */
class CustomDialog {
    private Context context;
    private int num;
    String[] scentName = null;

    private CustomDialogListener customDialogListener;


    public CustomDialog(Context context){
        this.context = context;
        num = -1;
    }

    interface CustomDialogListener{
        void onPositiveClicked(String name);
        void onNegativeClicked();
    }

    public void setDialogListener(CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }

    public void callFunction(final Button button, final int index){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 바 없애기
        dialog.setCancelable(false); // 백버튼 막기
        dialog.setContentView(R.layout.custom_dialog);
        dialog.show(); // 다이얼로그 보여주기

        final Spinner spinner = (Spinner)dialog.findViewById(R.id.custom_dialog_spinner);
        final Button okbutton = (Button)dialog.findViewById(R.id.custom_dialog_button_ok);
        final Button xbutton = (Button)dialog.findViewById(R.id.custom_dialog_button_cancel);
        final ArrayAdapter<CharSequence> adapter;

        switch (index){
            case 1:
                adapter = ArrayAdapter.createFromResource(context, R.array.stress, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                scentName = context.getResources().getStringArray(R.array.stress);
                break;
            case 2:
                adapter = ArrayAdapter.createFromResource(context, R.array.joy, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                scentName = context.getResources().getStringArray(R.array.joy);
                break;
            case 3:
                adapter = ArrayAdapter.createFromResource(context, R.array.sad, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                scentName = context.getResources().getStringArray(R.array.sad);
                break;
            case 4:
                adapter = ArrayAdapter.createFromResource(context, R.array.soso, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                scentName = context.getResources().getStringArray(R.array.soso);
                break;
            case 5:
                adapter = ArrayAdapter.createFromResource(context, R.array.angry, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                scentName = context.getResources().getStringArray(R.array.angry);
                break;
            case 6:
                adapter = ArrayAdapter.createFromResource(context, R.array.fear, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                scentName = context.getResources().getStringArray(R.array.fear);
                break;
        }


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
//                String[] tmpScent = context.getResources().getStringArray(R.array.scentname);
                String[] scentColor = context.getResources().getStringArray(R.array.scentcolor); // 컬러 색상 가져오기

                button.setText(scentName[num].toString()); // 버튼의 텍스트 변경

                Drawable drawable = context.getResources().getDrawable(R.drawable.circlebutton);

                // 향 이름에 따라 버튼 색상 변경
                String tmpColor = "#FF";
                switch (index){
                    case 1:
                        tmpColor += scentColor[0];
                        break;
                    case 2:
                        tmpColor += scentColor[1];
                        break;
                    case 3:
                        tmpColor += scentColor[2];
                        break;
                    case 4:
                        tmpColor += scentColor[3];
                        break;
                    case 5:
                        tmpColor += scentColor[4];
                        break;
                    case 6:
                        tmpColor += scentColor[5];
                        break;

                }
//                String tmpColor = "#FF";
//                switch (tmpScent[num].toString()){
//                    case "라벤더":
//                        tmpColor += scentColor[0];
//                        break;
//                    case "레몬":
//                        tmpColor += scentColor[1];
//                        break;
//                    case "프랑킨센스":
//                        tmpColor += scentColor[2];
//                        break;
//                    case "페퍼민트":
//                        tmpColor += scentColor[3];
//                        break;
//                    case "자스민":
//                        tmpColor += scentColor[4];
//                        break;
//                    case "로즈마리":
//                        tmpColor += scentColor[5];
//                        break;
//
//                }
                drawable.setColorFilter(Color.parseColor(tmpColor), PorterDuff.Mode.SRC_ATOP);
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    button.setBackgroundDrawable(drawable);
                } else {
                    button.setBackground(drawable);
                }

                customDialogListener.onPositiveClicked(scentName[num].toString()); // 값 전달
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
