package com.fintech.basis.main;

import android.animation.ArgbEvaluator;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.andrognito.flashbar.Flashbar;
import com.fintech.basis.R;
import com.fintech.basis.databinding.ActivityMainBinding;
import com.fintech.basis.databinding.AppBarBinding;
import com.fintech.basis.model.BasisResponse;
import com.fintech.basis.utils.ConnectivityReceiver;
import com.fintech.basis.utils.Constants;
import com.fintech.basis.utils.HelperFunctions;
import com.github.loadingview.LoadingView;
import com.muddzdev.styleabletoast.StyleableToast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class MainActivity extends AppCompatActivity implements  ConnectivityReceiver.ConnectivityReceiverListener, MainContract.MainView {
    private ActivityMainBinding binding;
    private ConnectivityReceiver receiver;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private MainPresenter presenter;
    private boolean isNetworkAvailable = true;
    private Flashbar.Builder flashbarBuilder;
    private Flashbar flashbar;
    private IntentFilter intentFilter;
    private LoadingView progress;
    private List<BasisResponse.BasisData> data;
    private TextView emptyMessage;
    private ViewPager viewPager;
    private Integer[] colors = null;
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private  TextView pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        renderView();
        init();

        pref = getSharedPreferences(Constants.MAIN_SCREEN, 0);
        editor = pref.edit();

        presenter = new MainPresenter(pref, editor,this);
        presenter.checkCache();
    }

    private void init() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new ConnectivityReceiver();

        getSupportActionBar().setTitle("Basis App");
    }


    private void renderView() {
        progress = binding.loadingView;
        emptyMessage = binding.emptyMessage;
        viewPager = binding.viewPager;
        pager = binding.pager;
        AppBarBinding appBarBinding = binding.appBar;
        setSupportActionBar(appBarBinding.toolbar);

    }


    @Override
    protected void onRestart() {
        super.onRestart();

        presenter.checkCache();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (Exception e) { }
        }

        if (flashbar != null) {
            flashbar.dismiss();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.sync:
                    viewPager.setCurrentItem(0,true);
                    return  true;

                default:
                    return super.onOptionsItemSelected(item);
            }

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

        presenter.cleanMemory();
        flashbar = null;
        flashbarBuilder = null;
        intentFilter = null;
        binding = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityReceiver.connectivityReceiverListener = this;
    }

    @Override
    public void onSuccess(BasisResponse response) {
         data = response.getData();

         if(data.size()>0) {
             int lastIndex = pref.getInt(Constants.LAST_ID, 0);
             emptyMessage.setVisibility(View.GONE);


             MainAdapter adapter=new MainAdapter(data,this);
             viewPager.setAdapter(adapter);
             viewPager.setPadding(130, 60, 130, 20);


             viewPager.setCurrentItem(lastIndex);
             viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                 @Override
                 public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                     int index = position + 1;
                     pager.setText("Page:"+ index+"/"+data.size());

                     editor.putInt(Constants.LAST_ID,position);
                     editor.commit();
                 }

                 @Override
                 public void onPageSelected(int position) { }

                 @Override
                 public void onPageScrollStateChanged(int state) { }
             });

         }
         else {
             emptyMessage.setVisibility(View.VISIBLE);
         }
    }

    @Override
    public void onFailure(String error) {
        flashbarBuilder = HelperFunctions.setSnackbar("Something went wrong", error, R.drawable.error,this, getResources().getColor(R.color.colorPrimaryDark));
        flashbarBuilder.positiveActionText("TRY AGAIN")
                .negativeActionText("CANCEL")
                .positiveActionTextColorRes(R.color.colorAccent)
                .negativeActionTextColorRes(R.color.colorAccent)
                .positiveActionTapListener(new Flashbar.OnActionTapListener() {
                    @Override
                    public void onActionTapped(@NotNull Flashbar flashbar) {
                        flashbar.dismiss();
                        presenter.checkCache();
                    }
                })
                .negativeActionTapListener(new Flashbar.OnActionTapListener() {
                    @Override
                    public void onActionTapped(@NotNull Flashbar flashbar) {
                        flashbar.dismiss();
                        StyleableToast.makeText(getApplicationContext(), "Data unavailable. Try after sometime", Toast.LENGTH_LONG, R.style.warningToast).show();
                        System.exit(0);
                    }
                });
    }

    @Override
    public void showWait() {
        registerReceiver(receiver, intentFilter);
        progress.start();
    }

    @Override
    public void removeWait() {
        if(progress!=null){
            progress.stop();
        }

        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (Exception e) { }
        }
    }
}