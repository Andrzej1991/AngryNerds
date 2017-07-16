package com.company.andrzej.rolki.projekttestowy;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.company.andrzej.rolki.projekttestowy.component.DaggerServiceComponent;
import com.company.andrzej.rolki.projekttestowy.component.ServiceComponent;
import com.company.andrzej.rolki.projekttestowy.fragments.DataProviderFromActivity;
import com.company.andrzej.rolki.projekttestowy.fragments.DetailFragment;
import com.company.andrzej.rolki.projekttestowy.model.Repos;
import com.company.andrzej.rolki.projekttestowy.module.ServiceModule;
import com.company.andrzej.rolki.projekttestowy.service.ReposService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity implements DataProviderFromActivity {

    @BindView(R.id.list_view_reposNames)
    ListView listView;

    private ReposService.ReposApi reposApi;
    private List<String> reposNames = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private List<Repos> reposList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private String login, avatarUrl, type;
    private long id;
    private DetailFragment detailFragment;

    ServiceComponent serviceComponent;

    @Inject
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //nasze metody
        injectServiceComponent();
        fetchRepos();
        configureListView();
        configureListViewOnItemClick();
    }

    public void injectServiceComponent() {
        String url = "https://api.github.com/users/";
        serviceComponent = DaggerServiceComponent.builder()
                .serviceModule(new ServiceModule(url))
                .build();
        serviceComponent.inject(this);
        reposApi = retrofit.create(ReposService.ReposApi.class);
    }

    private void fetchRepos() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting informations...");
        progressDialog.show();
        reposApi.getRepos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Repos>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<Repos> reposes) {
                        for (int i = 0; i < reposes.size() - 1; i++) {
                            reposNames.add(reposes.get(i).getName());
                        }
                        reposList.addAll(reposes);
                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("onErrorLog", e.getMessage());
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        progressDialog.dismiss();
                    }
                });
    }

    private void configureListView() {
                arrayAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                R.layout.list_view_black_text, R.id.list_content,
                reposNames);
        listView.setAdapter(arrayAdapter);
    }

    private void configureListViewOnItemClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                login = reposList.get(i).getOwner().getLogin();
                id = reposList.get(i).getOwner().getId();
                avatarUrl = reposList.get(i).getOwner().getAvatarUrl();
                type = reposList.get(i).getOwner().getType();
                openDetailFragment();
            }
        });
    }

    private void openDetailFragment() {
        detailFragment = new DetailFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.head_container, detailFragment, "detail")
                .addToBackStack("")
                .commit();
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getUrlAvatar() {
        return avatarUrl;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public long getId() {
        return id;
    }
}
