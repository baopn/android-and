package Adapter;


import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class SwipeDetect implements View.OnTouchListener {

    public static enum Action {
        LR, // Left to Right
        RL, // Right to Left
        TB, // Top to bottom
        BT, // Bottom to Top
        None, // when no action was detected
        Click
    }

    private static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;
    private Action mSwipeDetected = Action.None;

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.None;
                // Log.i(logTag, "Click On List" );
                return false; // allow other events like Click to be processed
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // horizontal swipe detection
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {

                        mSwipeDetected = Action.LR;
                        return false;
                    }
                    if (deltaX > 0) {

                        mSwipeDetected = Action.RL;
                        return false;
                    }
                }
                if (Math.abs(deltaY) > MIN_DISTANCE) {
                    if (deltaY < 0) {
                        Log.i("Room", "Swipe Top to Bottom");
                        mSwipeDetected = Action.TB;
                        return false;
                    }
                    if (deltaY > 0) {
                        Log.i("Room", "Swipe Bottom to Top");
                        mSwipeDetected = Action.BT;
                        return false;
                    }
                }
                mSwipeDetected = Action.Click;
                return false;
            }
        }
        return false;
    }
}
