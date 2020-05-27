package cat.udl.tidic.amd.dam_tips.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cat.udl.tidic.amd.dam_tips.R;
import cat.udl.tidic.amd.dam_tips.dao.AccountDAO;
import cat.udl.tidic.amd.dam_tips.models.Account;
import cat.udl.tidic.amd.dam_tips.network.RetrofitClientInstance;
import cat.udl.tidic.amd.dam_tips.preferences.PreferencesProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ranking extends AppCompatActivity {
    private RecyclerView playersListView;
    private AccountDAO accountDao;
    private UserAdapter userAdapter;
    private String TAG="RANGKING";
    public static final int EDIT_EVENT = 2;
    ArrayList<Account> players_data = new ArrayList<>();
    private SharedPreferences mPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        Intent intent = getIntent();

        playersListView = findViewById(R.id.userlist);
        playersListView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(new cat.udl.tidic.amd.dam_tips.views.UserDiffCallback());
        playersListView.setAdapter(userAdapter);

        userAdapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Account user) {
                Log.d(TAG, user.getUsername());
                Intent intent = new Intent(Ranking.this, Perfil.class);
                intent.putExtra(Perfil.EXTRA_USERNAME, user.getUsername());
                startActivityForResult(intent, EDIT_EVENT);
            }
        });

        accountDao = RetrofitClientInstance.
                getRetrofitInstance().create(AccountDAO.class);

        this.mPreferences = PreferencesProvider.providePreferences();
        token = this.mPreferences.getString("token", "");

        populateList();

    }


    public void populateList(){

       // Log.d(TAG,""+genere);
        Call<List<Account>> call_get_players = accountDao.getUsers();
        call_get_players.enqueue(new Callback<List<Account>>() {
            @Override
            public void onResponse(Call<List<Account>> call, Response<List<Account>> response) {
                Log.d(TAG,"code:"+response.code());
                if (response.code() == 200){
                    //Log.d(TAG,"code:"+response.code());

                    // Obtenim les dades de la consulta
                    players_data = (ArrayList<Account>) response.body();
                    userAdapter.submitList(players_data);
                } else{
                    // notificar problemes amb la consulta
                }
            }

            @Override
            public void onFailure(Call<List<Account>> call, Throwable t) {
                // Notificar problemes amb la red
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item= menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (newText.equals("") ){
                    userAdapter.submitList(players_data);
                    Log.d(TAG, "|Players| = " + players_data.size());
                }
                else {
                    ArrayList<Account> filteredModelList = (ArrayList<Account>) players_data.stream()
                            .filter(player -> player.getName().contains(newText))
                            .collect(Collectors.toList());

                    Log.d(TAG, "|Players| = " + filteredModelList.size());

                    userAdapter.submitList(filteredModelList);
                }
                playersListView.scrollToPosition(0);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
