package android.android_5.touchlogger;

import android.inputmethodservice.KeyboardView;

public abstract class KeyboardActionListener implements KeyboardView.OnKeyboardActionListener {


    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }


    public abstract void onKey(int primaryCode, int[] keyCodes);

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
