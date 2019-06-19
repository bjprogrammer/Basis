package com.fintech.basis.main;

import android.content.SharedPreferences;

import com.fintech.basis.model.BasisResponse;
import com.fintech.basis.network.NetworkError;
import com.fintech.basis.network.Service;
import com.fintech.basis.utils.Constants;
import com.google.gson.Gson;

public class MainPresenter implements  MainContract.Presenter{
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private MainContract.MainView view;
    private Gson gson;

    MainPresenter(SharedPreferences pref, SharedPreferences.Editor editor, MainContract.MainView view) {
        this.editor = editor;
        this.pref = pref;
        this.view = view;
        gson = new Gson();
    }


    @Override
    public void checkCache() {

        //Checking local storage for data else fetching it from server
        String response=pref.getString(Constants.DATA,null);
        if( response!=null){
            view.onSuccess(gson.fromJson(response, BasisResponse.class));
        }
        else
        {
            fetchData();
        }
    }

    @Override
    public void fetchData() {
        view.showWait();

        //Getting data from remote server
        new Service().getData(new Service.GetDataCallback() {
            @Override
            public void onSuccess(String response) {

                //Parsing response string and storing it locally
                String json=response.split("/")[1];
                editor.putString(Constants.DATA, json);
                editor.commit();

                view.removeWait();
                view.onSuccess(gson.fromJson(json, BasisResponse.class));
            }

            @Override
            public void onError(NetworkError networkError) {
               view.removeWait();
               view.onFailure(networkError.getAppErrorMessage());
            }
        });
    }

    @Override
    public void cleanMemory(){
         pref=null;
         editor=null;
         view=null;
    }
}

