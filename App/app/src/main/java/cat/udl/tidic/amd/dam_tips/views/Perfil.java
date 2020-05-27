package cat.udl.tidic.amd.dam_tips.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cat.udl.tidic.amd.dam_tips.R;
import cat.udl.tidic.amd.dam_tips.dao.AccountDAO;
import cat.udl.tidic.amd.dam_tips.models.Account;
import cat.udl.tidic.amd.dam_tips.network.RetrofitClientInstance;
import cat.udl.tidic.amd.dam_tips.preferences.PreferencesProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Perfil extends AppCompatActivity {
    public static final String EXTRA_USERNAME =
            "cat.udl.tidic.amd.dam_tips.EXTRA_USERNAME";

    private TextView name;
    private AccountDAO accountDAO;
    private SharedPreferences mPreferences;
    private TextView genere;
    private String TAG ="PUBLIC";
    private TextView points;
    private ImageView photo;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_USERNAME)) {
            setTitle("Perfil Publico");

        }
        String username;
        accountDAO = RetrofitClientInstance.
                getRetrofitInstance().create(AccountDAO.class);


        this.mPreferences = PreferencesProvider.providePreferences();
        token = this.mPreferences.getString("token", "");

        name = findViewById(R.id.text_username);
        genere = findViewById(R.id.text_genere);
        points = findViewById(R.id.text_punt);
        photo = findViewById(R.id.img_perf);

        name.setText(intent.getStringExtra(EXTRA_USERNAME));
        username = name.getText().toString();
        Call<Account> call_get = accountDAO.getPerfilPublico(username.trim());
        call_get.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                Log.d(TAG,""+response.code());
                if(response.code()==200) {
                    Account account = response.body();
                    if(account.getGenere().equals("M")){
                        Log.d("TAG","ENTRA");
                        genere.setText("Hombre");
                    }
                    else{
                        genere.setText("Mujer");
                    }
                    points.setText(account.getPoints());
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {

            }
        });


    }
}
