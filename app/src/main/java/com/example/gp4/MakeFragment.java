package com.example.gp4;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.ArrayTypeAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/* 커스터마이징 */
public class MakeFragment extends Fragment {

    private Button[] buttons = {};
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Gson gson;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_make, container, false);
        button1 = (Button) viewGroup.findViewById(R.id.fragment_make_button1);
        button2 = (Button) viewGroup.findViewById(R.id.fragment_make_button2);
        button3 = (Button) viewGroup.findViewById(R.id.fragment_make_button3);
        button4 = (Button) viewGroup.findViewById(R.id.fragment_make_button4);
        button5 = (Button) viewGroup.findViewById(R.id.fragment_make_button5);
        button6 = (Button) viewGroup.findViewById(R.id.fragment_make_button6);
        buttons = new Button[]{button1, button2, button3, button4, button5, button6};

        settingButton();

        return viewGroup;

    }

    private void settingButton() {

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String tmp = sharedPreferences.getString("scent", ""); // 저장되어 있는 향 정보 불러오기

        if(tmp.equals("")){ // 저장된 정보가 없으면
            Log.v("태그", "태그 확인 : 저장 정보 없음");
        }else{
            gson = new GsonBuilder().create();
            ArrayList<String> datas = new ArrayList<String>();

                try {
                    JSONArray jsonArray = new JSONArray(tmp);
                    for(int i=0; i < jsonArray.length(); i++){
                        String scent = jsonArray.optString(i);
                        datas.add(scent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } // 향 정보 받아오기

            // 향에 따른 버튼 색 변경
            Drawable drawable;
            for(int i=0; i<datas.size(); i++){
                switch (datas.get(i)){
                    case "A":
                        drawable = getContext().getResources().getDrawable(R.drawable.circlebutton);
                        drawable.setColorFilter(0xFF9232CC, PorterDuff.Mode.SRC_ATOP);
                        buttons[i].setText("A");
                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            buttons[i].setBackgroundDrawable(drawable);
                        } else {
                            buttons[i].setBackground(drawable);
                        }
                        break;
                    case "B":
                        drawable = getContext().getResources().getDrawable(R.drawable.circlebutton);
                        drawable.setColorFilter(0xFFADC8E6, PorterDuff.Mode.SRC_ATOP);
                        buttons[i].setText("B");
                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            buttons[i].setBackgroundDrawable(drawable);
                        } else {
                            buttons[i].setBackground(drawable);
                        }
                        break;
                    case "C":
                        drawable = getContext().getResources().getDrawable(R.drawable.circlebutton);
                        drawable.setColorFilter(0xFFF08080, PorterDuff.Mode.SRC_ATOP);
                        buttons[i].setText("C");
                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            buttons[i].setBackgroundDrawable(drawable);
                        } else {
                            buttons[i].setBackground(drawable);
                        }
                        break;
                    case "D":
                        drawable = getContext().getResources().getDrawable(R.drawable.circlebutton);
                        drawable.setColorFilter(0xFFFFB6C1, PorterDuff.Mode.SRC_ATOP);
                        buttons[i].setText("D");
                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            buttons[i].setBackgroundDrawable(drawable);
                        } else {
                            buttons[i].setBackground(drawable);
                        }
                        break;
                    case "E":
                        drawable = getContext().getResources().getDrawable(R.drawable.circlebutton);
                        drawable.setColorFilter(0xFFFFFF00, PorterDuff.Mode.SRC_ATOP);
                        buttons[i].setText("E");
                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            buttons[i].setBackgroundDrawable(drawable);
                        } else {
                            buttons[i].setBackground(drawable);
                        }
                        break;
                    case "F":
                        drawable = getContext().getResources().getDrawable(R.drawable.circlebutton);
                        drawable.setColorFilter(0xFFFF0000, PorterDuff.Mode.SRC_ATOP);
                        buttons[i].setText("F");
                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            buttons[i].setBackgroundDrawable(drawable);
                        } else {
                            buttons[i].setBackground(drawable);
                        }
                        break;

                }
            }


        }

    }
}
