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


//zobaczcie na interface DataProviderFromActivity. dostarcza nam metody w ktorych przekazemy
//dane do detail fragment
public class MainActivity extends AppCompatActivity implements DataProviderFromActivity {

    //bindowanie naszego ListView za pomocą butterknife, oszczedza czas i ulatwia prace
    @BindView(R.id.list_view_reposNames)
    ListView listView;
    //odwołanie do naszej klasy, gdzie przechowujemy zmienne, przeważnie wszystkie zapytania
    //są w jednym miejscu ze względu na łatwość dostępu
    private ReposService.ReposApi reposApi;
    //zadeklarowana lista z nazwami repos.
    private List<String> reposNames = new ArrayList<>();
    //nasz standardowy adapter do uzycia w listview
    private ArrayAdapter<String> arrayAdapter;
    //List<REPOS> lista naszych repozytorium, jest ich 29 na stronie
    private List<Repos> reposList = new ArrayList<>();
    //progress dialog czyli informacja dla uzytkownika, ze coś się dzieje w programie
    private ProgressDialog progressDialog;
    //wartosci String
    private String login, avatarUrl, type;
    //wartosc long
    private long id;
    //fragment z detalami z danego repos
    private DetailFragment detailFragment;

    //serviceComponent czyli miejsce w daggerze, gdzie definiujemy miejsce do ktorego go wstrzykniemy
    ServiceComponent serviceComponent;

    //infterfejs dodania Inject po polsku wstrzykiwać
    @Inject
    //Retrofit. mega framework do kontaktowania się z serwerem danych, warto o tym pamiętać
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
        //metoda ma za zadanie zainijować daggera z swoimi modulami
        String url = "https://api.github.com/users/";
        serviceComponent = DaggerServiceComponent.builder()
                //klasa serviceModule posiada moduły, które chcemy dodać
                .serviceModule(new ServiceModule(url))
                //build, zbudowanie serviceComponentu z modulami
                .build();
        //inicjacja
        serviceComponent.inject(this);
        //odwołanie do miejsca, gdzie są nasze zapytania
        reposApi = retrofit.create(ReposService.ReposApi.class);
    }

    private void fetchRepos() {
        //tutaj jest nasze zapytanie do servera tzn pobranie danych. Mamy progressdialog, który
        //informuje użytkownika, że coś się w aplikacji dzieje
        progressDialog = new ProgressDialog(this);
        //informacje w srodku progressDialogu
        progressDialog.setMessage("Getting informations...");
        //show wyświetla progreessdialog, trzeba pamietac o zamknieciu czyli progressdialog.dissmiis()
        progressDialog.show();
        //tutaj jest odwolanie do miejsca, gdzie znajduje się nasze zapytania do servera,
        //getRepos czyli nasza metoda do pobrania listy<Repos>
        reposApi.getRepos()
                //wskazujemy nasze I/O, to jest RxJava, bardzo ważne narzędzie bo jest x razy szybsza
                //od standarowych AsyncTaskow z javy. pamiętajcie, że coś takiego jest i że fajnie działa
                //do tego jest zdecydowanie latwiejsze do napisania.
                .subscribeOn(Schedulers.io())
                //wskazujemy na jakim watku ma dzialac
                .observeOn(AndroidSchedulers.mainThread())
                //tutaj tworzymy subskrypcje na naszej listcie<Repos>
                //dziala to o tyle fajnie, ze jak nie bedziemy pamietac parametrow
                // to kod zwroci nam blad. Czyli piszemy tutaj Observer<TO CO MAMY W NASZYM ZAPYTANIE>  czyli w getRepos
                .subscribe(new Observer<List<Repos>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<Repos> reposes) {
                        //petla w ktorej pobieramy name z repooses i dodajemy do listy Stringow
                        for (int i = 0; i < reposes.size() - 1; i++) {
                            reposNames.add(reposes.get(i).getName());
                        }
                        //dodaje do listy<Repos> wszystkie dane z reposes czyli z argumento onNext
                        reposList.addAll(reposes);
                        //bardzo wazne
                        //Niestety nic nie odświeża się automaycznie, tutaj jest coś takiego jak notifyDataSetCHanged
                        //czyli działa to tak, że odświeża nam nasz arrayAdapter, który jest przypisany do ListView
                        // zawsze wtedy gdy coś w środku się zmieni, np usunie się rekord, lub doda nowy
                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        //BARDZ WAŻNE Logi pomagają w naprawianiu czegoś warto je stosować
                        Log.d("SCIAGANIEE", e.getMessage());
                        //robimy bye bye naszemu progressDialog czyli znika, kiedy wykona się zapytanie i zwróci
                        //nam nasze dane
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        progressDialog.dismiss();
                    }
                });
    }

    private void configureListView() {
        //configurejymy nasz listView, czyli tworzymy adapter z customowym layoutem, gdzie definiumey jak nasz
        //textView wygląda. Zwróć uwagę na konstruktor ArrayAdaptera
        arrayAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                R.layout.list_view_black_text, R.id.list_content,
                reposNames);
        //przypisujemy listView do adaptera
        listView.setAdapter(arrayAdapter);
    }

    private void configureListViewOnItemClick() {
        //configurejmy w liscie setOnItemClickListener
        //pobieramy dane z kolejnych pozycja takie jak login, id, avatar, type
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                login = reposList.get(i).getOwner().getLogin();
                id = reposList.get(i).getOwner().getId();
                avatarUrl = reposList.get(i).getOwner().getAvatarUrl();
                type = reposList.get(i).getOwner().getType();

                //tutaj po kliknieciu rowniez otwiera się fragment z dokladniejszymi informacjami
                //ownere, zwróć uwagę, że wszystkie dane odwołują się do getOwner(), czyli do modelu
                //danych Owner
                openDetailFragment();
            }
        });
    }

    private void openDetailFragment() {
        //otwieramy fragment, to trzeba znać na pamięc, nie jest dużo.
        detailFragment = new DetailFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                //tutaj zobaczcie na head_container, gdzie on się znajduje.
                //w main_activity. Wskazujemy, gdzie owy fragment ma się wyświetlić
                //kliknij prawym na head_container Go To > Declaration
                .add(R.id.head_container, detailFragment, "detail")
                //addToBackStack oznacza, ze dodajemy nasz fragment do tzw Stosu, dzieki czemu po kliknieciu
                //onBackPressed czyli przycisku do cofania zamyka nam go
                .addToBackStack("")
                //commit w wolnym skrocie oznacza otworz fragment
                .commit();
    }


    //tu są gettery informacji z Interface, pobierzemy dzieki temu login, type i tak dalej w gfragmencie
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
