package com.fintech.basis.main;

import com.fintech.basis.model.BasisResponse;

public interface MainContract {

    interface MainView {
        void onSuccess(BasisResponse response);
        void onFailure(String error);
        void showWait();
        void removeWait();
    }

    interface Presenter{
        void checkCache();
        void fetchData();
        void cleanMemory();
    }
}
