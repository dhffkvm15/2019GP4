package com.example.gp4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

// 디퓨저 작동
public class PlayDiffuserActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView time;
    private Button play;
    private int howlong = 120; // 디퓨저 작동 시간
    private boolean isplay = false; // 현재 작동하고 있는지 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_diffuser);

        seekBar = (SeekBar)findViewById(R.id.play_diffuser_seekbar_time);
        time = (TextView)findViewById(R.id.play_diffuser_textview_time);
        play = (Button)findViewById(R.id.play_diffuser_button_play);

        Intent intent = getIntent();
        TotalInfo totalInfo = (TotalInfo) intent.getSerializableExtra("val"); // 데이터 받아오기
//        Log.v("태그확인", "확인0 " + totalInfo.getName() );
//        Log.v("태그확인", "확인1 " + totalInfo.getCatridgeInfo1() );
//        Log.v("태그확인", "확인2 " + totalInfo.getCatridgeInfo2() );
//        Log.v("태그확인", "확인3 " + totalInfo.getCatridgeInfo3() );
//        Log.v("태그확인", "확인4 " + totalInfo.getCatridgeInfo4() );
//        Log.v("태그확인", "확인5 " + totalInfo.getCatridgeInfo5() );
//        Log.v("태그확인", "확인6 " + totalInfo.getCatridgeInfo6() );


        // 시크바 동작할 때
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                howlong = progress;
                time.setText(String.valueOf(howlong) + "분");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Play 버튼 누를 때
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(howlong == 0){
                    Toast.makeText(getApplicationContext(), "시간을 잘못입력하셨습니다.", Toast.LENGTH_LONG).show();
                }else{


                if(isplay == false){
                    play.setText("STOP");
                    isplay = true;
                    startPlaying();
                }else{
                    play.setText("PLAY");
                    isplay = false;
                    stopPlaying();
                }

                }
            }
        });
    }

    // TODO 디퓨저 멈추는 코드
    private void stopPlaying() {
        Log.v("디퓨저", "멈춤");
    }

    // TODO 디퓨저 작동하는 코드, 잔량 계산 필요
    private void startPlaying() {
        Log.v("디퓨저", "작동");
    }

    @Override
    public void onBackPressed() {
        stopPlaying(); // 뒤로 버튼 클릭 시 디퓨저 멈추도록
        super.onBackPressed();
    }
}
