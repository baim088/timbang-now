package com.timbangnow.app.accessibility;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Singleton Text-to-Speech helper untuk aksesibilitas app-wide.
 * Menggunakan Application Context agar tidak bocor pada siklus hidup Activity.
 */
public class AudioAssistant {

    private static AudioAssistant instance;
    private TextToSpeech textToSpeech;
    private boolean isReady = false;

    private AudioAssistant(Context context) {
        // ponytail: pakai getApplicationContext() untuk mencegah memory leak Activity
        textToSpeech = new TextToSpeech(context.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(new Locale("id", "ID"));
                if (result != TextToSpeech.LANG_MISSING_DATA
                        && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isReady = true;
                    textToSpeech.setSpeechRate(0.85f);
                    textToSpeech.setPitch(1.0f);
                }
            }
        });
    }

    public static synchronized AudioAssistant getInstance(Context context) {
        if (instance == null) {
            instance = new AudioAssistant(context);
        }
        return instance;
    }

    /** Ucapkan pesan, menghentikan ucapan sebelumnya jika masih berlangsung. */
    public void speak(String message) {
        if (isReady && textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "TimbangNowTTS");
        }
    }

    /** Hentikan ucapan yang sedang berlangsung tanpa mematikan engine. */
    public void stop() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    /**
     * Panggil di Application.onTerminate() atau saat app benar-benar selesai.
     * Melepas semua resource TTS dan mereset singleton.
     */
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        isReady = false;
        instance = null;
    }
}
