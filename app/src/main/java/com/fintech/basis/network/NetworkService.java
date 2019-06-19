package com.fintech.basis.network;


import retrofit2.http.GET;
import io.reactivex.Observable;

//ALL API calls endpoints
public interface NetworkService {

    @GET("fjaqJ")
    Observable<String> getData() ;
}

