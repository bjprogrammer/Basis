package com.fintech.basis.onBoarding;

public interface onBoardingContract {

    interface onBoardingView {
        void directMainScreen();
        void renderView();
    }

    interface Presenter{
        void checkFirstTimeLaunch();
        void setFirstTimeLaunch();
        void cleanMemory();
    }
}
