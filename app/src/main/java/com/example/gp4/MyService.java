package com.example.gp4;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

public class MyService extends Service {
    private int time;
    private boolean isStop;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Thread timer = new Thread(new Timer());
        timer.start();
    }

    IMyTimerService.Stub binder = new IMyTimerService.Stub() {
        @Override
        public int getTime() throws RemoteException {
            return time;
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isStop = true;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStop = true;
    }

    private class Timer implements Runnable{

      private Handler handler = new Handler();

        @Override
        public void run() {
            
            for(time=0; time<10; time++){
                if(isStop) break;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), time+"", Toast.LENGTH_SHORT).show();
                    }
                });

                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "서비스 종료", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
