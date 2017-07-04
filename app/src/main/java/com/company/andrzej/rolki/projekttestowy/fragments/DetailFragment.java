package com.company.andrzej.rolki.projekttestowy.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.andrzej.rolki.projekttestowy.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment {

    //zbindowanie wszelkich rzeczy z layout
    @BindView(R.id.id_tv)
    TextView id_tv;
    @BindView(R.id.login_tv)
    TextView login_tv;
    @BindView(R.id.url_avatar_iv)
    ImageView avatar_iv;
    @BindView(R.id.type_tv)
    TextView type_tv;

    //zmienne
    private String login, avatarUrl, type;
    private long id;

    //konstruktor bezargumentowy
    public DetailFragment() {
    }

    //onAttack wywoluje się wtedy kiedy fragment jest przypisany do aktywnosci
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        //metody
        getDataFromActivity();
        setDataOnFragment();
        return view;
    }

    private void getDataFromActivity() {
        //tutaj pobieramy dane z aktywnosci.
        DataProviderFromActivity myActivity = (DataProviderFromActivity) getActivity();
        login = myActivity.getLogin();
        avatarUrl = myActivity.getUrlAvatar();
        type = myActivity.getType();
        id = myActivity.getId();
    }

    private void setDataOnFragment() {
        //tutaj wyswietlamy nasze dane w plikach z layoutu
        id_tv.setText("ID: " + String.valueOf(id));
        login_tv.setText("Login: " + login);
        //pisaco! Zapamietaj, że jest to super hiper uper zajebistyczna biblioteka do zdjęc, dzięki czemu zamiast 30 linijek kody
        //w android sdk, mozemy to zrobić w jednej i wyswietlic zdjecie.
        Picasso.with(getContext()).load(avatarUrl).into(avatar_iv);
        type_tv.setText("Type: " + type);
    }
}

