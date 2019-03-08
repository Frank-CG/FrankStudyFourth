package com.example.frankstudyfourth;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.exoplayer2.ui.PlayerView;

public class MyExoPlayerView extends PlayerView {
    private View decorView;

    public MyExoPlayerView(Context context) {
        this(context,null);
    }

    public MyExoPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyExoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void setDecorView(View decorView) {
        this.decorView = decorView;
//        this.decorView.setOnSystemUiVisibilityChangeListener
//                (new View.OnSystemUiVisibilityChangeListener() {
//                    @Override
//                    public void onSystemUiVisibilityChange(int visibility) {
//                        Log.d("FullScreenDialog","onSystemUiVisibilityChange:"+visibility);
//                        // Note that system bars will only be "visible" if none of the
//                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
//                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                            // TODO: The system bars are visible. Make any desired
//                            // adjustments to your UI, such as showing the action bar or
//                            // other navigational controls.
//                            enableFullScreenMode();
//                        } else {
//                            // TODO: The system bars are NOT visible. Make any desired
//                            // adjustments to your UI, such as hiding the action bar or
//                            // other navigational controls.
//                        }
//                    }
//                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d("MyExoPlayerView","onTouchEvent");
        boolean result = super.onTouchEvent(ev);
//        enableFullScreenMode();
        return result;
    }

    private void enableFullScreenMode() {
        Log.d("MyExoPlayerView","enableFullScreenMode");
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
