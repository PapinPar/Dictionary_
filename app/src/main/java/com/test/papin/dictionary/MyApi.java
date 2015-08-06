package com.test.papin.dictionary;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;


public interface MyApi {

@GET("/get")
    void getWord(
        @Query("q")String Word,
        @Query("langpair")String lang,
        Callback<NewsArchive> callback);

}
