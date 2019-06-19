package com.fintech.basis.onBoarding;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.fintech.basis.R;
import com.fintech.basis.databinding.ActivityOnBoardingBinding;
import com.fintech.basis.main.MainActivity;
import com.fintech.basis.utils.Constants;
import com.fintech.basis.utils.Paths;
import com.github.jorgecastillo.FillableLoader;
import com.github.jorgecastillo.State;
import com.github.jorgecastillo.listener.OnStateChangeListener;

import io.codetail.animation.ViewAnimationUtils;


public class onBoardingActivity extends AppCompatActivity implements OnStateChangeListener, onBoardingContract.onBoardingView {
    private onBoardingPresenter presenter;
    private View onBoardingCard;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private FillableLoader fillableLoader;
    private TextView subText;
    private Animation slideUpAnimation;
    private ActivityOnBoardingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding);

        pref = getSharedPreferences(Constants.ONBOARDING_SCREEN, 0);
        editor = pref.edit();


        presenter = new onBoardingPresenter(pref, editor,this);

        //Check whether app is launched for the first time or not ?
        presenter.checkFirstTimeLaunch();
    }

    @Override
    public void renderView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;     //Hide status and navigation bar for splash screen animation
        decorView.setSystemUiVisibility(uiOptions);

        onBoardingCard = binding.awesomeCard;
        fillableLoader = binding.fillableLoader;
        subText = binding.subtext;

        init();
    }


    private void init() {
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "bullet.otf");    //Setting custom font for subtext
        subText.setTypeface(custom_font);

        fillableLoader.setSvgPath(Paths.SVGLOGO);                                //SVG path to animate logo
        fillableLoader.setFillColor(Color.TRANSPARENT);
        fillableLoader.setOnStateChangeListener(this);                          // Listener to detect end of logo animation

        onBoardingCard.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        onBoardingCard.post(new Runnable() {
            @Override
            public void run() {
                circularAnimation().start();                                     //Starting circular animation
            }});

        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),  //Slide up animation
                R.anim.slide_up_animation);

        slideUpAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                subText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        launchNextScreen();                      //Starting next screen after slide up animation ends
                    }},700);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }

    /*
      This method generates circular animation starting from bottom right of screen
   */
    private Animator circularAnimation() {
        int cx = (onBoardingCard.getLeft() + onBoardingCard.getRight());
        int cy = (onBoardingCard.getTop() + onBoardingCard.getBottom());
        int dx = Math.max(cx, onBoardingCard.getWidth() - cx);
        int dy = Math.max(cy, onBoardingCard.getHeight() - cy);
        float radius = (float) Math.hypot(dx, dy);

        Animator animator = ViewAnimationUtils.createCircularReveal(onBoardingCard, cx, cy, 0, radius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1100);                         //Duration for circular animation
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                fillableLoader.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fillableLoader.start();
                    }},100);            //Start SVG logo animation after circular reveal animation
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        return animator;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.cleanMemory();
        onBoardingCard =null;
        subText=null;
        fillableLoader=null;
        slideUpAnimation=null;
        presenter=null;
        binding=null;
    }

    @Override
    public void onStateChange(int state) {
        if(state == State.FINISHED){                              //Detecting end of SVG logo animation and making subtext visible to user with bottom up animation
            subText.setVisibility(View.VISIBLE);
            subText.startAnimation(slideUpAnimation);
        }
    }


    /*
        This method starts Main activity from onBoarding Activity with right to left animation
        (Used by first time users)
     */

    public void launchNextScreen() {
        presenter.setFirstTimeLaunch();
        startActivity(new Intent(onBoardingActivity.this, MainActivity.class));
        finish();
        this.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    /*
         This method starts Splash screen directly without making onBoarding screen visible to user
         (Not for first time users)
     */
    @Override
    public void directMainScreen() {
        startActivity(new Intent(onBoardingActivity.this, MainActivity.class));
        finish();
    }
}
