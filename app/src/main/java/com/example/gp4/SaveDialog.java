package com.example.gp4;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// 향 정보 저장하는 다이얼로그
public class SaveDialog {

    private Context context;

    public SaveDialog(Context context){
        this.context = context;
    }

    public void saveInfo(final CatridgeInfo[] catridgeInfos){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.save_dialog);
        dialog.show(); // 다이얼로그 보여주기

        final EditText name = (EditText)dialog.findViewById(R.id.save_dialog_edittext_name);
        final TextView info = (TextView)dialog.findViewById(R.id.save_dialog_textview_info);
        final Button save = (Button)dialog.findViewById(R.id.save_dialog_save_button);
        final Button cancel = (Button)dialog.findViewById(R.id.save_dialog_cancel_button);

        // TextView에 정보 표시하기
        String string ="";
        for(int i=0; i<catridgeInfos.length; i++){
            string += catridgeInfos[i].getName();
            string += " : ";

            switch (catridgeInfos[i].getRest()){
                case 0:
                    string += "0% ";
                    break;
                case 1:
                    string += "30% ";
                    break;
                case 2:
                    string += "60% ";
                    break;
                case 3:
                    string += "100% ";
                    break;
            }
        }
        info.setText(string);

        // 저장 버튼 누를 시
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().length() == 0 ){ // 이름 입력 안했을 때
                    Toast.makeText(context, "이름 입력 필요", Toast.LENGTH_LONG).show();
                }else {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                    String key = sharedPreferences.getString("pushID", ""); // 저장되어 있는 키 값 불러오기

                    TotalInfo totalInfo = new TotalInfo(name.getText().toString(), catridgeInfos[0], catridgeInfos[1], catridgeInfos[2],
                            catridgeInfos[3], catridgeInfos[4], catridgeInfos[5]);
                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(); // 파이어베이스 불러오기
                                    DatabaseReference databaseReference = firebaseDatabase.getReference("storage");
                                    databaseReference.child(key).push().setValue(totalInfo);
//                                    databaseReference.child(key).child(name.getText().toString()).child("1").setValue(catridgeInfos[0]);
//                                    databaseReference.child(key).child(name.getText().toString()).child("2").setValue(catridgeInfos[1]);
//                                    databaseReference.child(key).child(name.getText().toString()).child("3").setValue(catridgeInfos[2]);
//                                    databaseReference.child(key).child(name.getText().toString()).child("4").setValue(catridgeInfos[3]);
//                                    databaseReference.child(key).child(name.getText().toString()).child("5").setValue(catridgeInfos[4]);
//                                    databaseReference.child(key).child(name.getText().toString()).child("6").setValue(catridgeInfos[5]);

                                    Toast.makeText(context, "저장완료", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }



            }
        });


        // 취소 버튼 누를 시
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
