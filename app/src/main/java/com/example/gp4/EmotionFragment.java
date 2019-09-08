package com.example.gp4;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;



public class EmotionFragment extends Fragment {

    private static final AtomicBoolean processing = new AtomicBoolean(false);
    private static SurfaceHolder surfaceHolder = null;
    private static Camera camera = null;

    @SuppressLint("StaticFieldLeak")
    private static TextView heartRate;
    @SuppressLint("StaticFieldLeak")
    private static TextView imgAvgText;
    @SuppressLint("StaticFieldLeak")
    private static TextView rollAvgText;

    private static SurfaceView surfaceView; // 카메라가 비추는 화면 표시
    private static PowerManager.WakeLock wakeLock = null;
    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    public static enum TYPE{
        GREEN, RED
    };

    private static TYPE currentType = TYPE.GREEN;
    public static TYPE getCurrentType(){
        return currentType;
    }
    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;
    private static int curX = 1;

    private static Button startButton;
    private static MyProgressBar myProgressBar;

    private LineChart lineChart; // 그래프

    public static EmotionFragment newInstance(){
        return new EmotionFragment();
    }

    @SuppressLint("InvalidWakeLockTag")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_emotion, container, false);
        startButton = (Button)viewGroup.findViewById(R.id.fragment_emotion_button_start);

        checkCAMERAPermission(); // 카메라 권한 획득

        surfaceView = (SurfaceView)viewGroup.findViewById(R.id.fragment_emotion_preview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceCallback);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        heartRate = (TextView)viewGroup.findViewById(R.id.fragment_emotion_textview_heart);
        imgAvgText = (TextView)viewGroup.findViewById(R.id.fragment_emotion_img_avg_text);
        //rollAvgText = (TextView)viewGroup.findViewById(R.id.fragment_emotion_rollavg_text);

        PowerManager powerManager = (PowerManager)getActivity().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

        myProgressBar = (MyProgressBar)viewGroup.findViewById(R.id.fragment_emotion_progressbar);

       // start 버튼 클릭했을 때, 측정 시작
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setVisibility(v.INVISIBLE); // 버튼 안보이게

                myProgressBar.setStart(true); // 프로그레스 바 작동하도록
                myProgressBar.setStartTime(System.currentTimeMillis());
                myProgressBar.setCurValue(0);
                myProgressBar.invalidate();
                myProgressBar.requestLayout();

                startTime = myProgressBar.getStartTime(); // 시작 시간 동일하도록 설정
            }
        } ) ;

        // 그래프 관련
        {   // // Chart Style // //
            lineChart = (LineChart)viewGroup.findViewById(R.id.fragment_emotion_linechart);
            // background color
            lineChart.setBackgroundColor(Color.WHITE);
            // disable description text
            lineChart.getDescription().setEnabled(false);
            // enable touch gestures
            lineChart.setTouchEnabled(false);
            lineChart.setDrawGridBackground(false);
            // enable scaling and dragging
            lineChart.setDragEnabled(false);
            lineChart.setScaleEnabled(false);
            // force pinch zoom along both axis
            lineChart.setPinchZoom(false);
        }

        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = lineChart.getXAxis();
            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f);
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = lineChart.getAxisLeft();
            // disable dual axis (only use LEFT axis)
            lineChart.getAxisRight().setEnabled(false);
            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);
            // axis range
            yAxis.setAxisMaximum(200f);
            yAxis.setAxisMinimum(-50f);
        }

       return viewGroup;
    }

    // 그래프 관련
    public void setData(int bpm, double time) { // int 값만 넘겨주면 될 것 같음
//        lineChart.invalidate(); // 차트 초기화
//        lineChart.clear();

        //Log.v("중요", "중요 셋 데이타 함수 bpm : "+bpm + " " +time);
        ArrayList<Entry> values = new ArrayList<>(); // 차트 데이터 셋에 담겨질 데이터
        values.add(new Entry((float) time, bpm));

        LineDataSet lineDataSet;

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            lineDataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            lineDataSet.setValues(values);
            lineDataSet.notifyDataSetChanged();
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            lineDataSet = new LineDataSet(values, "DataSet 1");

            lineDataSet.setDrawIcons(false);

            // draw dashed line
            lineDataSet.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            lineDataSet.setColor(Color.BLACK);
            lineDataSet.setCircleColor(Color.BLACK);

            // line thickness and point size
            lineDataSet.setLineWidth(1f);
            lineDataSet.setCircleRadius(3f);

            // draw points as solid circles
            lineDataSet.setDrawCircleHole(false);

            // customize legend entry
            lineDataSet.setFormLineWidth(1f);
            lineDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            lineDataSet.setFormSize(15.f);

            // text size of values
            lineDataSet.setValueTextSize(9f);

            // draw selection line as dashed
            lineDataSet.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return lineChart.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = getContext().getDrawable(R.drawable.fade_red);
                lineDataSet.setFillDrawable(drawable);
            } else {
                lineDataSet.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(lineDataSet); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            lineChart.setData(data);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire(10 * 60 * 1000L);
        camera = android.hardware.Camera.open();
        startTime = 0;
        Log.v("중요", "중요 이모션 시작시간1 : " + startTime);

    }


    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        startTime = 0;
        camera = null;
    }

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        /**
         * {@inheritDoc}
         */

        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {

            if(myProgressBar.isStart()) {  //////// 여기여기


                long endTime = System.currentTimeMillis();
                double totalTimeInSecs = (endTime - startTime) / 1000d;

                if (totalTimeInSecs >= curX && totalTimeInSecs < curX + 1) {
                    myProgressBar.setCurValue(curX++);
                    myProgressBar.invalidate();
                    myProgressBar.requestLayout();
                }

                if (myProgressBar.getCurValue() == 15) {
                    Log.v("중요", "15초 지남");

                    double bps = (beats / (totalTimeInSecs - 1));
                    int dpm = (int) (bps * 60d);
                    Log.v("중요", "중요 이모션 시작 시간2 : " + startTime);
                    Log.v("중요", "중요 총 시간 : " + totalTimeInSecs);
                    if (dpm < 50 || dpm > 180) {

                        Log.v("중요", "중요 다시 측정");
                        //Log.v("중요", "dpm : "+dpm);
                        //startTime = System.currentTimeMillis();
                        curX = 1;
                        myProgressBar.setStart(true); // 프로그레스 바 작동하도록
                        myProgressBar.setStartTime(System.currentTimeMillis());
                        myProgressBar.setCurValue(0);
                        myProgressBar.invalidate();
                        myProgressBar.requestLayout();

                        startTime = myProgressBar.getStartTime(); // 시작 시간 동일하도록 설정

                        Log.v("중요", "중요 이모션 시작시간3 : " + startTime);
                        beats = 0;
                        processing.set(false);
                        return;
                    }

                    // Log.d(TAG,
                    // "totalTimeInSecs="+totalTimeInSecs+" beats="+beats);

                    if (beatsIndex == beatsArraySize) beatsIndex = 0;
                    beatsArray[beatsIndex] = dpm;
                    beatsIndex++;

                    int beatsArrayAvg = 0;
                    int beatsArrayCnt = 0;
                    for (int i = 0; i < beatsArray.length; i++) {
                        if (beatsArray[i] > 0) {
                            beatsArrayAvg += beatsArray[i];
                            beatsArrayCnt++;
                        }
                    }
                    int beatsAvg = (beatsArrayAvg / beatsArrayCnt);

                    myProgressBar.setFinish(true); // 텍스트가 안보이게 끔
                    heartRate.setVisibility(View.VISIBLE);
                    heartRate.setText(String.valueOf(beatsAvg) + " bpm");

                    //startTime = System.currentTimeMillis();
                    //Log.v("중요", "중요 이모션 시작시간4 : "+startTime);
                    beats = 0;

                }

                Log.v("여기", "onPreviewFrame");
                if (data == null) throw new NullPointerException();
                Camera.Size size = cam.getParameters().getPreviewSize();
                if (size == null) throw new NullPointerException();

                if (!processing.compareAndSet(false, true)) return;

                int width = size.width;
                int height = size.height;

                int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
                // imgAvg = red pixel values / framesize

                if (imgAvg == 0 || imgAvg == 255) {
                    processing.set(false);
                    return;
                }

                int averageArrayAvg = 0;
                int averageArrayCnt = 0;
                for (int i = 0; i < averageArray.length; i++) {
                    if (averageArray[i] > 0) {
                        averageArrayAvg += averageArray[i];
                        averageArrayCnt++;
                    }
                }

                int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
                TYPE newType = currentType;

                imgAvgText.setText("측정중 ");
                // imgAvgText.setText("image average:"+ Integer.toString(imgAvg));
                //rollAvgText.setText("rolling average:"+ Integer.toString(rollingAverage));

                if (imgAvg < rollingAverage) {
                    newType = TYPE.RED;
                    if (newType != currentType) {
                        beats++;
                        //////여기 고침
                        double times = (System.currentTimeMillis() - startTime) / 1000d;
                        setData((int) (beats / times) * 60, (System.currentTimeMillis() - startTime) / 1000d);
                        // Log.d(TAG, "BEAT!! beats="+beats);
                    }
                } else if (imgAvg > rollingAverage) {
                    newType = TYPE.GREEN;
                }

                if (averageIndex == averageArraySize) averageIndex = 0;
                averageArray[averageIndex] = imgAvg;
                averageIndex++;

                // Transitioned from one state to another to the same
                if (newType != currentType) {
                    currentType = newType;
                    //image.postInvalidate();
                }

//            long endTime = System.currentTimeMillis();
//            double totalTimeInSecs = (endTime - startTime) / 1000d;
//
//            if( myProgressBar.getCurValue() == 15 ){
//                Log.v("중요", "15초 지남");
//
//
//                    double bps = (beats / (totalTimeInSecs-1));
//                    int dpm = (int) (bps * 60d);
//                    Log.v("중요", "중요 이모션 시작 시간2 : "+startTime);
//                    Log.v("중요", "중요 총 시간 : "+totalTimeInSecs);
//                    if (dpm < 50 || dpm > 180) {
//
//                        Log.v("중요", "중요 다시 측정");
//                        //Log.v("중요", "dpm : "+dpm);
//                        //startTime = System.currentTimeMillis();
//                        myProgressBar.setStart(true); // 프로그레스 바 작동하도록
//                        myProgressBar.setStartTime(System.currentTimeMillis());
//                        myProgressBar.setCurValue(0);
//                        myProgressBar.invalidate();
//                        myProgressBar.requestLayout();
//
//                        startTime = myProgressBar.getStartTime(); // 시작 시간 동일하도록 설정
//
//                        Log.v("중요", "중요 이모션 시작시간3 : "+startTime);
//                        beats = 0;
//                        processing.set(false);
//                        return;
//                    }
//
//                    // Log.d(TAG,
//                    // "totalTimeInSecs="+totalTimeInSecs+" beats="+beats);
//
//                    if (beatsIndex == beatsArraySize) beatsIndex = 0;
//                    beatsArray[beatsIndex] = dpm;
//                    beatsIndex++;
//
//
//                    int beatsArrayAvg = 0;
//                    int beatsArrayCnt = 0;
//                    for (int i = 0; i < beatsArray.length; i++) {
//                        if (beatsArray[i] > 0) {
//                            beatsArrayAvg += beatsArray[i];
//                            beatsArrayCnt++;
//                        }
//                    }
//                    int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
//
//                    myProgressBar.setFinish(true); // 텍스트가 안보이게 끔
//                    heartRate.setVisibility(View.VISIBLE);
//                    heartRate.setText(String.valueOf(beatsAvg)+" bpm");
//
//                    //startTime = System.currentTimeMillis();
//                    //Log.v("중요", "중요 이모션 시작시간4 : "+startTime);
//                    beats = 0;
//
//            }

                // 아래의 것이 원래 것
//            if (totalTimeInSecs >= 15 && totalTimeInSecs <16) {
//
//                double bps = (beats / totalTimeInSecs);
//                int dpm = (int) (bps * 60d);
//                Log.v("중요", "중요 이모션 시작 시간2 : "+startTime);
//                Log.v("중요", "중요 총 시간 : "+totalTimeInSecs);
//                if (dpm < 50 || dpm > 180) {
//
//                    Log.v("중요", "중요 다시 측정");
//                    //Log.v("중요", "dpm : "+dpm);
//                    //startTime = System.currentTimeMillis();
//                    myProgressBar.setStart(true); // 프로그레스 바 작동하도록
//                    myProgressBar.setStartTime(System.currentTimeMillis());
//                    myProgressBar.setCurValue(0);
//                    myProgressBar.invalidate();
//                    myProgressBar.requestLayout();
//
//                    startTime = myProgressBar.getStartTime(); // 시작 시간 동일하도록 설정
//
//                    Log.v("중요", "중요 이모션 시작시간3 : "+startTime);
//                    beats = 0;
//                    processing.set(false);
//                    return;
//                }
//
//                // Log.d(TAG,
//                // "totalTimeInSecs="+totalTimeInSecs+" beats="+beats);
//
//                if (beatsIndex == beatsArraySize) beatsIndex = 0;
//                beatsArray[beatsIndex] = dpm;
//                beatsIndex++;
//
//
//                int beatsArrayAvg = 0;
//                int beatsArrayCnt = 0;
//                for (int i = 0; i < beatsArray.length; i++) {
//                    if (beatsArray[i] > 0) {
//                        beatsArrayAvg += beatsArray[i];
//                        beatsArrayCnt++;
//                    }
//                }
//                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
//
//                myProgressBar.setFinish(true); // 텍스트가 안보이게 끔
//                heartRate.setVisibility(View.VISIBLE);
//                heartRate.setText(String.valueOf(beatsAvg)+" bpm");
//
//                //startTime = System.currentTimeMillis();
//                //Log.v("중요", "중요 이모션 시작시간4 : "+startTime);
//                beats = 0;
//            }
                processing.set(false);

            }
        } // onPreviewFrame
    };

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {



            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("Preview-surfaceCallback", "Exception in setPreviewDisplay()", t);
            }

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


            android.hardware.Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
            android.hardware.Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                //Log.d("HeartRateMonitor", "Using width=" + size.width + " height=" + size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();



        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }

    // 카메라 권한 획득
    public void checkCAMERAPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    1001);
        } else {
            Log.v("태그", "카메라 허가 받음");
        }
    }
}
