package com.fintech.basis.utils;

import android.app.Activity;
import android.graphics.Color;
import android.widget.ImageView;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;



public class HelperFunctions {

    //Configuring bottom snackbar to show notifications
    public static Flashbar.Builder setSnackbar(String title, String message, int icon, Activity activity, int background) {

        return new Flashbar.Builder(activity)
                .gravity(Flashbar.Gravity.BOTTOM)
                .title(title)
                .titleColor(Color.WHITE)
                .message(message)
                .messageColor(Color.WHITE)
                .enterAnimation(FlashAnim.with(activity)
                        .animateBar()
                        .duration(900)
                        .slideFromLeft()
                        .overshoot())
                .exitAnimation(FlashAnim.with(activity)
                        .animateBar()
                        .duration(250)
                        .slideFromLeft()
                        .accelerate())
                .backgroundColor(background)
                .showOverlay()
                .overlayBlockable()
                .showIcon(1.0f, ImageView.ScaleType.CENTER_CROP)
                .icon(icon);
    }

}
