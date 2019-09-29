package com.example.gp4;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* 사용자의 음성 받아들이기 및 음성 감정 분석 */
public class Emotion2Fragment extends Fragment {

    private boolean stress = false; // 스트레스 유무
    private Button talk;
    private Button play;

    private final int mBufferSize = 1024;
    private final int mBytesPerElement = 2;

    // 설정할 수 있는 sampleRate, AudioFormat, channelConfig 값들을 정의
    private final int[] mSampleRates = new int[]{44100, 22050, 11025, 8000};
    private final short[] mAudioFormats = new short[]{AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT};
    private final short[] mChannelConfigs = new short[]{AudioFormat.CHANNEL_IN_STEREO, AudioFormat.CHANNEL_IN_MONO};

    // 위의 값들 중 실제 녹음 및 재생 시 선택된 설정값들을 저장
    private int mSampleRate;
    private short mAudioFormat;
    private short mChannelConfig;

    private AudioRecord mRecorder = null;
    private Thread mRecordingThread = null;

    private boolean IsRecording = false; // 녹음 중인지에 대한 상태값
    private String mPath = ""; // 녹음한 파일을 저장할 경로

    // private WavWriter wavWriter = null;
    private int BUFFER_SIZE = 0;
    final String relativeDir = "/Recorder";
    private BufferedOutputStream bufferedOutputStream;
    private BufferedInputStream bufferedInputStream;
    private int mAudioLen = 0;
    private final int HEADER_SIZE = 0x2c;
    private final int WAVE_CHANNEL_MONO = 1;  //wav 파일 헤더 생성시 채널 상수값
    private final int RECORDER_BPP = 16;
    private final int RECORDER_SAMPLERATE = 0xac44;
    private File outPath;

    public static Emotion2Fragment newInstance(){
        return new Emotion2Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_emotion2, container, false);

        talk = (Button)viewGroup.findViewById(R.id.emotion2_fragment_button_talk);
        play = (Button)viewGroup.findViewById(R.id.emotion2_fragment_button_play);

        checkRecordPermission(); // 마이크 허가 받기
        checkStoragePermission(); // 저장 허가 받기

        init(); //기본 설정

        // talk 버튼 클릭했을 때 - 음성 녹음
        talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (IsRecording == false) {
                    startRecording();
                    IsRecording = true;
                    talk.setText("Stop");
                    //Log.v("태그", "녹음 중");
                } else {
                    stopRecording();
                    IsRecording = false;
                    talk.setText("Talk");
                    //Log.v("태그", "녹음 중단");
                }

            }
        });

        // play 버튼 클릭했을 때 - 음성 플레이
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // play 재생하기
                if (outPath.length() == 0 || IsRecording) {
                    Log.v("태그", "녹음 없음");
                    return;
                }

                // 녹음된 파일이 있는 경우 해당 파일 재생
                playWaveFile();
            }
        });

        return viewGroup;
    }

    private void init() {
        Bundle bundle = getArguments();
        stress = bundle.getBoolean("stress");
        Log.v("전달", "전달 받은 stress : "+ stress);
    }

    // 녹음을 수행할 Thread를 생성하여 녹음을 수행하는 함수
    public void startRecording() {

        mRecorder = findAudioRecord();
        //Log.v("태그", "mRecorder : " + mRecorder);
        mRecorder.startRecording();
        IsRecording = true;

        mRecordingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                //writeAudioDataToFile();
                writeWavAudioDataToFile();
            }

        }, "AudioRecorder Thread");

        mRecordingThread.start();

        // wavWriter = new WavWriter(mAudioFormat);
        //wavWriter.start();


    }

    // 녹음을 하기 위한 sampleRate, audioFormat, channelConfig 값들을 설정
    public AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short format : mAudioFormats) {
                for (short channel : mChannelConfigs) {
                    try {
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channel, format);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            mSampleRate = rate;
                            mAudioFormat = format;
                            mChannelConfig = channel;

                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, mSampleRate, mChannelConfig, mAudioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                                return recorder;    // 적당한 설정값들로 생성된 Recorder 반환
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //BUFFER_SIZE = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
//        int bufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannelConfig, mAudioFormat);
//        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, mSampleRate, mChannelConfig, mAudioFormat, bufferSize);
//
//        return recorder;
        return null;  // 적당한 설정값들을 찾지 못한 경우 Recorder를 찾지 못하고 null 반환
    }

    // 실제 녹음한 data를 file에 쓰는 함수
    public void writeAudioDataToFile() {

        String sd = Environment.getExternalStorageDirectory().getAbsolutePath();

        mPath = sd + "/record_audiorecord.pcm";
        Log.v("태그", "녹음 경로 : " + mPath);

        short sData[] = new short[mBufferSize];

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mPath);

            while (IsRecording) {
                mRecorder.read(sData, 0, mBufferSize);
                byte bData[] = short2byte(sData);
                fos.write(bData, 0, mBufferSize * mBytesPerElement);
            }
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeWavAudioDataToFile() {
        BUFFER_SIZE = AudioRecord.getMinBufferSize(mSampleRate, mChannelConfig, mAudioFormat);
        byte[] buffer = new byte[BUFFER_SIZE];
        byte[] data = new byte[BUFFER_SIZE];

        File waveFile = new File(Environment.getExternalStorageDirectory().getPath() + relativeDir);
        File tempFile = new File(Environment.getExternalStorageDirectory().getPath() + relativeDir + "tempFile");

        waveFile.mkdirs();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH'h'mm'm'ss.SSS's'", Locale.US);
        String nowStr = df.format(new Date());
        outPath = new File(waveFile, "rec" + nowStr + ".wav");
        Log.v("태그", "write 안 녹음 경로 : " + outPath);
        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(tempFile));
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        int read = 0;
        int len = 0;
        if (null != bufferedOutputStream) {
            try {
                while (IsRecording) {
                    read = mRecorder.read(data, 0, BUFFER_SIZE);
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        bufferedOutputStream.write(data);
                    }
                }

                bufferedOutputStream.flush();
                mAudioLen = (int) tempFile.length();
                bufferedInputStream = new BufferedInputStream(new FileInputStream(tempFile));
                bufferedOutputStream.close();
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outPath));
                bufferedOutputStream.write(getFileHeader());
                len = HEADER_SIZE;
                while ((read = bufferedInputStream.read(buffer)) != -1) {
                    bufferedOutputStream.write(buffer);
                }
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                bufferedOutputStream.close();
            } catch (IOException e1) {
// TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    private byte[] getFileHeader() {
        byte[] header = new byte[HEADER_SIZE];
        int totalDataLen = mAudioLen + 40;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * WAVE_CHANNEL_MONO / 8;
        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = (byte) 1;  // format = 1 (PCM방식)
        header[21] = 0;
        header[22] = WAVE_CHANNEL_MONO;
        header[23] = 0;
        header[24] = (byte) (RECORDER_SAMPLERATE & 0xff);
        header[25] = (byte) ((RECORDER_SAMPLERATE >> 8) & 0xff);
        header[26] = (byte) ((RECORDER_SAMPLERATE >> 16) & 0xff);
        header[27] = (byte) ((RECORDER_SAMPLERATE >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) RECORDER_BPP * WAVE_CHANNEL_MONO / 8;  // block align
        header[33] = 0;
        header[34] = RECORDER_BPP;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (mAudioLen & 0xff);
        header[41] = (byte) ((mAudioLen >> 8) & 0xff);
        header[42] = (byte) ((mAudioLen >> 16) & 0xff);
        header[43] = (byte) ((mAudioLen >> 24) & 0xff);
        return header;
    }


    // short array형태의 data를 byte array형태로 변환하여 반환하는 함수
    public byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    // 녹음을 중지하는 함수
    public void stopRecording() {
        if (mRecorder != null) {
            IsRecording = false;
            mRecorder.stop();
            mRecorder.release();

            // wavWriter.stop();
            // Log.v("태그", "wav 주소 :" + wavWriter.getPath());

            mRecorder = null;
            mRecordingThread = null;
        }
    }

    // 녹음할 때 설정했던 값과 동일한 설정값들로 해당 파일을 재생하는 함수
    public void playWaveFile() {

        int minBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelConfig, mAudioFormat);
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, mSampleRate, mChannelConfig, mAudioFormat, minBufferSize, AudioTrack.MODE_STREAM);

        int count = 0;

        byte[] data = new byte[mBufferSize];

        try {

            FileInputStream fis = new FileInputStream(outPath);
            // FileInputStream fis = new FileInputStream(mPath);
            // FileInputStream fis = new FileInputStream(wavWriter.getPath());
            Log.v("태그", "play 안 경로 : "+outPath);
            DataInputStream dis = new DataInputStream(fis);

            audioTrack.play();

            while ((count = dis.read(data, 0, mBufferSize)) > -1) {

                audioTrack.write(data, 0, count);

            }

            audioTrack.stop();
            audioTrack.release();

            dis.close();
            fis.close();

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

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
