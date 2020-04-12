package org.tensorflow.lite.examples.detection;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import java.util.Locale;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;
import java.util.ArrayList;
import java.util.UUID;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;
import io.reactivex.disposables.Disposable;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class SpeechService extends Service implements TextToSpeech.OnInitListener {

    public static final String EXTRA_WORD = "word";
    public static final String EXTRA_MEANING = "meaning";

    private TextToSpeech tts;
    private String word, meaning;
    private boolean isInit;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(getApplicationContext(), this);
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessageDelayed(1,5000);
        word = intent.getStringExtra(SpeechService.EXTRA_WORD);
        meaning = intent.getStringExtra(SpeechService.EXTRA_MEANING);

        if (isInit) {
            try {
                speak();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                stopSelf();
            }
        }, 15*1000);

        return SpeechService.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                try {
                    speak();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                isInit = true;
            }
        }
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 1: {
                stopSelf();
            }break;
        }
    }
    private void speak() throws InterruptedException {
        if (tts != null) {
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null,null);
            tts.speak(meaning, TextToSpeech.QUEUE_ADD, null,null);
            Thread.sleep(2500);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}