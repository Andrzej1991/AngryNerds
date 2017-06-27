package com.company.andrzej.rolki.projekttestowy.service;

import com.company.andrzej.rolki.projekttestowy.model.Repos;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public class ReposService {
    public interface ReposApi {
        @GET("google/repos")
        Observable<List<Repos>> getRepos();
    }
}
