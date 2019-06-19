package com.fintech.basis.network;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


//Networking using RxJava
public class Service {
    public  static CompositeDisposable disposable;
    private NetworkService networkService;
    public Service(){
        networkService=NetworkAPI.getClient().create(NetworkService.class);
        if(disposable==null){
            disposable=new CompositeDisposable();
        }
    }

    public Observer getData(final GetDataCallback callback) {
        return NetworkAPI.getClient().create(NetworkService.class).getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext((Function) throwable -> {
                    return Observable.error((Throwable) throwable);
                })
                .subscribeWith(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if(d.isDisposed()){
                            disposable.add(d);
                        }
                    }

                    @Override
                    public void onComplete() { }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(String response) {
                        callback.onSuccess(response);
                    }
                });
    }



    public interface GetDataCallback{
        void onSuccess(String response);
        void onError(NetworkError networkError);
    }
}

