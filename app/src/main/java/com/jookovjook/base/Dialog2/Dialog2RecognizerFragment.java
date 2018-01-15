package com.jookovjook.base.Dialog2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jookovjook.base.R;

import java.util.Objects;

import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.Recognition;
import ru.yandex.speechkit.Recognizer;
import ru.yandex.speechkit.RecognizerListener;
import ru.yandex.speechkit.SpeechKit;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.jookovjook.base.Utils.Config.API_KEY;

public class Dialog2RecognizerFragment extends Fragment implements RecognizerListener {
    private static final String API_KEY_FOR_TESTS_ONLY = API_KEY;
    private static final int REQUEST_PERMISSION_CODE = 1;
    private Recognizer recognizer;
    private boolean full_screen;
    int LANG;

    public interface onEventListener {
        public void SendResult(String s);
        public void SendPartitialResults(String s);
        public void SendPower(float v);
        public void SendError(String s);
    }

    onEventListener EventListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            EventListener = (onEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    public Dialog2RecognizerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpeechKit.getInstance().configure(getContext(), API_KEY_FOR_TESTS_ONLY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog2_recognizer_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        full_screen = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        resetRecognizer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_PERMISSION_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PERMISSION_GRANTED) {
            createAndStartRecognizer();
        }
    }

    @Override
    public void onRecordingBegin(Recognizer recognizer) {

    }

    @Override
    public void onSpeechDetected(Recognizer recognizer) {
    }

    @Override
    public void onSpeechEnds(Recognizer recognizer) {

    }

    @Override
    public void onRecordingDone(Recognizer recognizer) {

    }

    @Override
    public void onSoundDataRecorded(Recognizer recognizer, byte[] bytes) {}

    @Override
    public void onPowerUpdated(Recognizer recognizer, float v) {
        EventListener.SendPower(v);
    }

    @Override
    public void onPartialResults(Recognizer recognizer, Recognition recognition, boolean b) {
        final String part_res = recognition.getBestResultText();
        int count = part_res.length() - part_res.replace(".","").length();
        count = count + part_res.length() - part_res.replace("!","").length();
        count = count +part_res.length() - part_res.replace("?","").length();
        if(full_screen) {
            if (count > 0) {
                EventListener.SendResult(part_res);
            } else {
                EventListener.SendPartitialResults(part_res);
            }
        }else{
            EventListener.SendPartitialResults(part_res);
        }

    }

    @Override
    public void onRecognitionDone(Recognizer recognizer, Recognition recognition) {
        String text = recognition.getBestResultText();
        int count_dot = text.length() - text.replace(".","").length();
        int count_vosc = text.length() - text.replace("!","").length();
        int count_ques = text.length() - text.replace("?","").length();
        if(full_screen) {
            if(count_dot > 0) {
                text = text.substring(text.lastIndexOf(".") + 1);
            }else if(count_ques > 0){
                text = text.substring(text.lastIndexOf("?") + 1);
            }else if(count_vosc > 0){
                text = text.substring(text.lastIndexOf("!") + 1);
            }
        }
        recognizer.finishRecording();
        //full_screen = true;
        if(!full_screen) {
            if (!Objects.equals(text, "")) {
                EventListener.SendResult(text.substring(0, 1).toUpperCase() + text.substring(1));
            }
        }
    }

    @Override
    public void onError(Recognizer recognizer, Error error) {
        if (error.getCode() == Error.ERROR_CANCELED) {
            EventListener.SendError(getResources().getString(R.string.error_canceled));
        } else if (error.getCode() == Error.ERROR_API_KEY){
            EventListener.SendError(getResources().getString(R.string.error_api_key));
        }else if (error.getCode() == Error.ERROR_AUDIO){
            EventListener.SendError(getResources().getString(R.string.error_audio));
        }else if (error.getCode() == Error.ERROR_AUDIO_INTERRUPTED){
            EventListener.SendError(getResources().getString(R.string.error_audio_interrupted));
        }else if (error.getCode() == Error.ERROR_AUDIO_PERMISSIONS){
            EventListener.SendError(getResources().getString(R.string.error_audio_permissions));
        }else if (error.getCode() == Error.ERROR_AUDIO_PLAYER){
            EventListener.SendError(getResources().getString(R.string.error_audio_player));
        }else if (error.getCode() == Error.ERROR_BUSY){
            EventListener.SendError(getResources().getString(R.string.error_busy));
        }else if (error.getCode() == Error.ERROR_CANCELED){
            EventListener.SendError(getResources().getString(R.string.error_canceled));
        }else if (error.getCode() == Error.ERROR_ENCODING){
            EventListener.SendError(getResources().getString(R.string.error_encoding));
        }else if (error.getCode() == Error.ERROR_LANGUAGE_NOT_SUPPORTED_FOR_MODEL){
            EventListener.SendError(getResources().getString(R.string.error_language_not_supported_for_model));
        }else if (error.getCode() == Error.ERROR_NETWORK){
            EventListener.SendError(getResources().getString(R.string.error_busy));
        }else if (error.getCode() == Error.ERROR_NO_SPEECH){
            EventListener.SendError(getResources().getString(R.string.error_no_speech));
        }else if (error.getCode() == Error.ERROR_NO_TEXT_TO_SYNTHESIZE){
            EventListener.SendError(getResources().getString(R.string.error_no_text_to_synthesize));
        }else if (error.getCode() == Error.ERROR_NOT_AVAILABLE){
            EventListener.SendError(getResources().getString(R.string.error_not_available));
        }else if (error.getCode() == Error.ERROR_OK){
            EventListener.SendError(getResources().getString(R.string.error_ok));
        }else if (error.getCode() == Error.ERROR_SERVER){
            EventListener.SendError(getResources().getString(R.string.error_server));
        }else if (error.getCode() == Error.ERROR_UNKNOWN){
            EventListener.SendError(getResources().getString(R.string.error_unknown));
        }
    }

    private void resetRecognizer(){
        if (recognizer != null) {recognizer.cancel();
            recognizer = null;}
    }

    public void createAndStartRecognizer() {
        final Context context = getContext();
        if (context == null) {return;}
        if (ContextCompat.checkSelfPermission(context, RECORD_AUDIO) != PERMISSION_GRANTED) {
            requestPermissions(new String[]{RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
        } else {
            resetRecognizer();
            if(LANG == 1) {
                recognizer = Recognizer.create(Recognizer.Language.ENGLISH, Recognizer.Model.NOTES, Dialog2RecognizerFragment.this, full_screen);
            }else if(LANG == 3){
                recognizer = Recognizer.create(Recognizer.Language.TURKISH, Recognizer.Model.NOTES, Dialog2RecognizerFragment.this, full_screen);
            }else if(LANG == 4){
                recognizer = Recognizer.create(Recognizer.Language.UKRAINIAN, Recognizer.Model.NOTES, Dialog2RecognizerFragment.this, full_screen);
            }else {
                recognizer = Recognizer.create(Recognizer.Language.RUSSIAN, Recognizer.Model.NOTES, Dialog2RecognizerFragment.this, full_screen);
            }
            recognizer.start();
        }
    }

    public void fullScreen(boolean fs){
        full_screen = fs;
    }

    public void setLANG(int lang){
        LANG = lang;
    }

    public void stopRecognizer(){
        if(recognizer != null){
            recognizer.finishRecording();
        }
    }
}
