package com.fintech.basis.onBoarding;

import android.content.SharedPreferences;

import com.fintech.basis.utils.Constants;


public class onBoardingPresenter implements  onBoardingContract.Presenter{
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private onBoardingContract.onBoardingView view;

    onBoardingPresenter(SharedPreferences pref, SharedPreferences.Editor editor, onBoardingContract.onBoardingView view) {
        this.editor = editor;
        this.pref = pref;
        this.view = view;
    }

    /*
       Checking whether if app is launched for first time or not
       First time user - Continue with OnBoarding activity
       Not a first time user - Show main screen
       */

    @Override
    public void checkFirstTimeLaunch() {
        if(!pref.getBoolean(Constants.IS_FIRST_TIME_LAUNCH, true)){
            view.directMainScreen();     //Not a first time user and already logged in - move to main screen
        }
        else
        {
            view.renderView();                //First time user - Render onBoarding screen view
        }
    };

    //Setting first time launch  boolean value in shared preference
    @Override
    public void setFirstTimeLaunch() {
        editor.putBoolean(Constants.IS_FIRST_TIME_LAUNCH, false);
        editor.commit();
    }

    @Override
    public void cleanMemory(){
         pref=null;
         editor=null;
         view=null;
    }
}

