package com.example.gp4;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.writer.WriterProcessor;

public class record_playFragment extends Fragment {

    private Button record;
    private Button play;
    private TextView textView;
    private TextView pitchTextView;
    private TarsosDSPAudioFormat tarsosDSPAudioFormat;
    private AudioDispatcher dispatcher;
    private File file;
    private boolean isRecording = false;
    private String filename = "recorded_sound.wav";

    public static record_playFragment newInstance(){
        return new record_playFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_record_play, container, false);

        record = (Button)viewGroup.findViewById(R.id.record_play_fragment_recordButton);
        play = (Button)viewGroup.findViewById(R.id.record_play_fragment_playButton);
        textView = (TextView)viewGroup.findViewById(R.id.record_play_fragment_textView);
        pitchTextView = (TextView)viewGroup.findViewById(R.id.record_play_fragment_pitchTextView);

        checkRecordPermission(); // 마이크 허가
        checkStoragePermission(); // 저장 허가

        File sdCard = Environment.getExternalStorageDirectory();
        file = new File(sdCard, filename);

        String filePath = file.getAbsolutePath();
        Log.v("태그", "저장파일경로 : " +filePath);

        tarsosDSPAudioFormat = new TarsosDSPAudioFormat(TarsosDSPAudioFormat.Encoding.PCM_SIGNED,
                22050,
                2*8,
                1,
                2*1,
                22050,
                ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder()));

        // 녹음 버튼 눌렀을 때
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecording){
                    recordAudio();
                    isRecording = true;
                    record.setText("중지");
                }else{
                    stopRecording();
                    isRecording = false;
                    record.setText("녹음");
                }
            }
        });

        // 재생 버튼 눌렀을 때
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });

        return viewGroup;
    }

    public void playAudio(){
        try{
            releaseDispatcher();

            FileInputStream fileInputStream = new FileInputStream(file);
            dispatcher = new AudioDispatcher(new UniversalAudioInputStream(fileInputStream, tarsosDSPAudioFormat), 1024, 0);

            AudioProcessor playerProcessor = new AndroidAudioPlayer(tarsosDSPAudioFormat, 2048, 0);
            dispatcher.addAudioProcessor(playerProcessor);

            PitchDetectionHandler pitchDetectionHandler = new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                    final float pitchInHz = pitchDetectionResult.getPitch();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pitchTextView.setText(pitchInHz + "");
                        }
                    });
                }
            };

            AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pitchDetectionHandler);
            dispatcher.addAudioProcessor(pitchProcessor);

            Thread audioThread = new Thread(dispatcher, "Audio Thread");
            audioThread.start();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void recordAudio()
    {
        releaseDispatcher();
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file,"rw");
            AudioProcessor recordProcessor = new WriterProcessor(tarsosDSPAudioFormat, randomAccessFile);
            dispatcher.addAudioProcessor(recordProcessor);

            PitchDetectionHandler pitchDetectionHandler = new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult res, AudioEvent e){
                    final float pitchInHz = res.getPitch();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pitchTextView.setText(pitchInHz + "");
                        }
                    });
                }
            };

            AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pitchDetectionHandler);
            dispatcher.addAudioProcessor(pitchProcessor);

            Thread audioThread = new Thread(dispatcher, "Audio Thread");
            audioThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording()
    {
        releaseDispatcher();
    }

    public void releaseDispatcher()
    {
        if(dispatcher != null)
        {
            if(!dispatcher.isStopped())
                dispatcher.stop();
            dispatcher = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseDispatcher();
    }

    /* 마이크 허가 받기 */
    public void checkRecordPermission() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO},
                    123);
        } else {
            Log.v("태그", "마이크 허가 받음");
        }
    }

    /* 저장 권한 허가 요청 */
    public void checkStoragePermission() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        } else {
            Log.v("태그", "저장 허가 받음");
        }
    }

}
