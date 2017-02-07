package com.example.ryosuke.nakagawa.speaklab;

import android.content.Context;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ryosuke on 2017/01/28.
 */
public class SpeechListener implements RecognitionListener {
    private Context context;

    public SpeechListener(Context context){
        this.context = context;
    };

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Toast.makeText(context,"音声認識開始",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBeginningOfSpeech() {
        Toast.makeText(context,"認識中",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        Toast.makeText(context,"認識終了",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onResults(Bundle bundle) {
        String getList = "";
        ArrayList<String> datalist = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for(String s : datalist){
            getList += s + ",";
        }
        Toast.makeText(context,getList,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }
}
