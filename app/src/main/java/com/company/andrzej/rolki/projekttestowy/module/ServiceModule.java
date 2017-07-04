package com.company.andrzej.rolki.projekttestowy.module;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Andrzej on 2017-06-26.
 */

//tutaj jest nasz ServiceModule
    //czyli moduly jakie chcemy dodać
@Module
public class ServiceModule {
    //strona internetowa z api w konstruktorze
    private String mBaseURL;
    public ServiceModule(String mBaseURL) {
        this.mBaseURL = mBaseURL;
    }
    @Provides
    @Singleton
    //gson converter z automaty zamienia nam obiekty JSON W TĄ I DRUGĄ STRONĄ
    //bez tego byśmy musieli pisać z 500 linijek kodu, serio!
    GsonConverterFactory provideGson() {
        return GsonConverterFactory.create();
    }
    @Provides
    @Singleton
    //kazde zapytanie do servera musi być w tzw AsyncTasku czyli tzw praca w tle. RXJAVA,
    //jest swietnym narzedziem do robienia takich rzeczy
    RxJava2CallAdapterFactory provideAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }
    @Provides
    @Singleton
    //retrofit ogolna komunikacja z serwerem
    //te moduly są bardzo podstawowe, ale warto je pamietać, bo bardzo pomagają
    //sprawdzue cu zbahdyhe się w konstruktorze.
    //na koncu build czyli zbudowanie naszego retrofita z gsonem i rxjava
    Retrofit provideRetrofit(GsonConverterFactory gson, RxJava2CallAdapterFactory rxJava2CallAdapterFactory) {
        return new Retrofit.Builder()
                .addConverterFactory(gson)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .baseUrl(mBaseURL)
                .build();
    }
}
