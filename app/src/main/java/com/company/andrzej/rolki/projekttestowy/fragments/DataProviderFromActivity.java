package com.company.andrzej.rolki.projekttestowy.fragments;


//prosty interface, który ma zadanie pobierać dane
public interface DataProviderFromActivity {
    String getLogin();
    String getUrlAvatar();
    String getType();
    long getId();
}
