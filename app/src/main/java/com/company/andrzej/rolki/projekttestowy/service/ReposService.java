package com.company.andrzej.rolki.projekttestowy.service;

import com.company.andrzej.rolki.projekttestowy.model.Repos;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;


//tutaj tworzymy nasze zapytania
public class ReposService {
    public interface ReposApi {
        //get oznacza pobierz coś
        //argumenty  czyli odpowiednie galeze w drzewie
        //np mamy google jako glowne drzewo
        //i są np /repos ,  /users , /itd
        @GET("google/repos")
        //obserwujemy list z repos
        Observable<List<Repos>> getRepos();
    }
}
