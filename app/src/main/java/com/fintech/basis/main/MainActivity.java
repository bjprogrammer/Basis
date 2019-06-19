package com.fintech.basis.main;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.andrognito.flashbar.Flashbar;
import com.fintech.basis.R;
import com.fintech.basis.databinding.ActivityMainBinding;
import com.fintech.basis.utils.ConnectivityReceiver;
import com.fintech.basis.utils.HelperFunctions;


import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class MainActivity extends AppCompatActivity implements  ConnectivityReceiver.ConnectivityReceiverListener {
    private ActivityMainBinding binding;
    private ConnectivityReceiver receiver;
    private boolean isNetworkAvailable = true;
    private Flashbar.Builder flashbarBuilder;
    private Flashbar flashbar;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);



        init();
    }

    private void init() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new ConnectivityReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (Exception e) {
            }
        }

        if (flashbar != null) {
            flashbar.dismiss();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isNetworkAvailable != isConnected) {
            String title, message;

            if (isConnected) {
                if (flashbar != null) {
                    flashbar.dismiss();
                }
            } else {
                title = getString(R.string.no_wifi);
                message = getString(R.string.no_wifi_message);

                flashbarBuilder = HelperFunctions.setSnackbar(title, message, R.drawable.nowifi, this, getResources().getColor(R.color.colorPrimaryDark));
                flashbarBuilder.showProgress(Flashbar.ProgressPosition.RIGHT);
                flashbar = flashbarBuilder.build();
                flashbar.show();
            }
        }
        isNetworkAvailable = (isConnected);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        flashbar = null;
        flashbarBuilder = null;
        intentFilter = null;
        binding = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
        ConnectivityReceiver.connectivityReceiverListener = this;
    }
}