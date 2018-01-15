package com.jookovjook.base.Dialog2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.jookovjook.base.R;
import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.SpeechKit;
import ru.yandex.speechkit.Synthesis;
import ru.yandex.speechkit.Vocalizer;
import ru.yandex.speechkit.VocalizerListener;

import static com.jookovjook.base.Utils.Config.API_KEY;

public class Dialog2VocalizerFragment extends Fragment implements VocalizerListener {

    private static final String API_KEY_FOR_TESTS_ONLY = API_KEY;
    //private TextView currentStateTv;
    private ProgressBar progress_bar;
    private RelativeLayout.LayoutParams pb_LP;
    private Vocalizer vocalizer;
    int LANG;
    int VOICE;

    public interface onEventListener2 {
        public void SendError2(String s);
    }

    onEventListener2 EventListener2;

    public Dialog2VocalizerFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            EventListener2 = (onEventListener2) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpeechKit.getInstance().configure(getContext(), API_KEY_FOR_TESTS_ONLY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog2_vocalizer_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progress_bar = (ProgressBar) view.findViewById(R.id.progress_bar2);
        pb_LP = (RelativeLayout.LayoutParams) progress_bar.getLayoutParams();
        //pb_LP.topMargin = -100;
        //progress_bar.setLayoutParams(pb_LP);
        progress_bar.setAlpha(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        resetVocalizer();
    }

    private void resetVocalizer() {
        if (vocalizer != null) {
            vocalizer.cancel();
            vocalizer = null;
        }
    }

    @Override
    public void onSynthesisBegin(Vocalizer vocalizer) {

    }

    @Override
    public void onSynthesisDone(Vocalizer vocalizer, Synthesis synthesis) {

    }

    @Override
    public void onPlayingBegin(Vocalizer vocalizer) {

    }

    @Override
    public void onPlayingDone(Vocalizer vocalizer) {
        //pb_LP.topMargin = -100;
        //progress_bar.setLayoutParams(pb_LP);
        progress_bar.setAlpha(0);
    }

    @Override
    public void onVocalizerError(Vocalizer vocalizer, ru.yandex.speechkit.Error error) {
        if (error.getCode() == Error.ERROR_CANCELED) {
            EventListener2.SendError2(getResources().getString(R.string.error_canceled));
            //updateStatus("Cancelled");
            //updateProgress(0);
        } else if (error.getCode() == Error.ERROR_API_KEY){
            EventListener2.SendError2(getResources().getString(R.string.error_api_key));
        }else if (error.getCode() == Error.ERROR_AUDIO){
            EventListener2.SendError2(getResources().getString(R.string.error_audio));
        }else if (error.getCode() == Error.ERROR_AUDIO_INTERRUPTED){
            EventListener2.SendError2(getResources().getString(R.string.error_audio_interrupted));
        }else if (error.getCode() == Error.ERROR_AUDIO_PERMISSIONS){
            EventListener2.SendError2(getResources().getString(R.string.error_audio_permissions));
        }else if (error.getCode() == Error.ERROR_AUDIO_PLAYER){
            EventListener2.SendError2(getResources().getString(R.string.error_audio_player));
        }else if (error.getCode() == Error.ERROR_BUSY){
            EventListener2.SendError2(getResources().getString(R.string.error_busy));
        }else if (error.getCode() == Error.ERROR_CANCELED){
            EventListener2.SendError2(getResources().getString(R.string.error_canceled));
        }else if (error.getCode() == Error.ERROR_ENCODING){
            EventListener2.SendError2(getResources().getString(R.string.error_encoding));
        }else if (error.getCode() == Error.ERROR_LANGUAGE_NOT_SUPPORTED_FOR_MODEL){
            EventListener2.SendError2(getResources().getString(R.string.error_language_not_supported_for_model));
        }else if (error.getCode() == Error.ERROR_NETWORK){
            EventListener2.SendError2(getResources().getString(R.string.error_busy));
        }else if (error.getCode() == Error.ERROR_NO_SPEECH){
            EventListener2.SendError2(getResources().getString(R.string.error_no_speech));
        }else if (error.getCode() == Error.ERROR_NO_TEXT_TO_SYNTHESIZE){
            EventListener2.SendError2(getResources().getString(R.string.error_no_text_to_synthesize));
        }else if (error.getCode() == Error.ERROR_NOT_AVAILABLE){
            EventListener2.SendError2(getResources().getString(R.string.error_not_available));
        }else if (error.getCode() == Error.ERROR_OK){
            EventListener2.SendError2(getResources().getString(R.string.error_ok));
        }else if (error.getCode() == Error.ERROR_SERVER){
            EventListener2.SendError2(getResources().getString(R.string.error_server));
        }else if (error.getCode() == Error.ERROR_UNKNOWN){
            EventListener2.SendError2(getResources().getString(R.string.error_unknown));
        }
        //pb_LP.topMargin = -100;
        //progress_bar.setLayoutParams(pb_LP);
        progress_bar.setAlpha(0);
        resetVocalizer();
    }

    public void Vocalize(String text){
        //pb_LP.topMargin = -13;
        //progress_bar.setLayoutParams(pb_LP);
        progress_bar.setAlpha(1);
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(getContext(), getResources().getString(R.string.error_no_text), Toast.LENGTH_SHORT).show();
        } else {
            // Reset the current vocalizer.
            resetVocalizer();
            // To create a new vocalizer, specify the language, the text to be vocalized, the auto play parameter
            // and the voice.
            if (LANG == 1) {
                if (VOICE == 1) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.ENGLISH, text, true, Vocalizer.Voice.ALYSS);
                }else if (VOICE == 3) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.ENGLISH, text, true, Vocalizer.Voice.JANE);
                }else if (VOICE == 4) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.ENGLISH, text, true, Vocalizer.Voice.OMAZH);
                }else if (VOICE == 5) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.ENGLISH, text, true, Vocalizer.Voice.ZAHAR);
                }else {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.ENGLISH, text, true, Vocalizer.Voice.ERMIL);
                }
            }else if (LANG == 3) {
                if (VOICE == 1) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.TURKISH, text, true, Vocalizer.Voice.ALYSS);
                }else if (VOICE == 3) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.TURKISH, text, true, Vocalizer.Voice.JANE);
                }else if (VOICE == 4) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.TURKISH, text, true, Vocalizer.Voice.OMAZH);
                }else if (VOICE == 5) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.TURKISH, text, true, Vocalizer.Voice.ZAHAR);
                }else {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.TURKISH, text, true, Vocalizer.Voice.ERMIL);
                }
            }else if (LANG == 4) {
                if (VOICE == 1) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.UKRAINIAN, text, true, Vocalizer.Voice.ALYSS);
                }else if (VOICE == 3) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.UKRAINIAN, text, true, Vocalizer.Voice.JANE);
                }else if (VOICE == 4) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.UKRAINIAN, text, true, Vocalizer.Voice.OMAZH);
                }else if (VOICE == 5) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.UKRAINIAN, text, true, Vocalizer.Voice.ZAHAR);
                }else {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.UKRAINIAN, text, true, Vocalizer.Voice.ERMIL);
                }
            }else {
                if (VOICE == 1) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, text, true, Vocalizer.Voice.ALYSS);
                }else if (VOICE == 3) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, text, true, Vocalizer.Voice.JANE);
                }else if (VOICE == 4) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, text, true, Vocalizer.Voice.OMAZH);
                }else if (VOICE == 5) {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, text, true, Vocalizer.Voice.ZAHAR);
                }else {
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, text, true, Vocalizer.Voice.ERMIL);
                }
            }

            // Set the listener.
            vocalizer.setListener(Dialog2VocalizerFragment.this);
            // Don't forget to call start.
            vocalizer.start();
        }

    }

    public void setLANG(int lang){
        LANG = lang;
    }

    public void setVOICE(int voice){
        VOICE = voice;
    }
}
