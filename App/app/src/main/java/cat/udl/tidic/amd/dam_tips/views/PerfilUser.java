package cat.udl.tidic.amd.dam_tips.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cat.udl.tidic.amd.dam_tips.R;
import cat.udl.tidic.amd.dam_tips.dao.AccountDAO;
import cat.udl.tidic.amd.dam_tips.models.Account;
import cat.udl.tidic.amd.dam_tips.network.RetrofitClientInstance;
import cat.udl.tidic.amd.dam_tips.preferences.PreferencesProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilUser extends AppCompatActivity {
    private TextView name;
    private TextView surnam;
    private TextView mail;
    private TextView phone;
    private TextView generes;
    private TextView pointss;
    private TextView usernames;
    private AccountDAO accountDAO;
    private SharedPreferences mPreferences;
    private String token;
    private String TAG;
    private ImageView img_perfs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_user);
        Intent intent = getIntent();
        readPermission();
        name = findViewById(R.id.text_names);
        surnam = findViewById(R.id.text_surname);
        mail = findViewById(R.id.text_email);
        phone = findViewById(R.id.text_phone);
        generes = findViewById(R.id.text_generos);
        pointss = findViewById(R.id.text_puntoss);
        usernames = findViewById(R.id.text_usern);
        img_perfs = findViewById(R.id.img_perfils);

        img_perfs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openDialog();
            }
        });
        readPermission();

        getUserProfile();

    }

    public void getUserProfile() {
        accountDAO = RetrofitClientInstance.
                getRetrofitInstance().create(AccountDAO.class);

        this.mPreferences = PreferencesProvider.providePreferences();
        token = this.mPreferences.getString("token", "");
        // @JordiMateoUdl -> Podem posar el enque per obtenir usuari
        System.out.print(this.accountDAO);
        Call<Account> call_get = this.accountDAO.getUserProfile();
        call_get.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {

                if (response.code() == 200) {

                    Account u = response.body();
                    name.setText(u.getName());
                    surnam.setText(u.getSurname());
                    usernames.setText(u.getUsername());
                    mail.setText(u.getEmail());
                    phone.setText(u.getPhone());
                    pointss.setText(u.getPoints());
                    if(u.getGenere().equals("M")){
                        Log.d("TAG","ENTRA");
                        generes.setText("Hombre");
                    }
                    else{
                        generes.setText("Mujer");
                    }


                } else {

                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {

            }
        });
    }
    public void openDialog() {
        DialogImage dialogImage = new DialogImage().newInstance(this);
        dialogImage.show(getSupportFragmentManager(),"Profile Photo Dialog");
    }
    private boolean readPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getApplicationContext(),"I need access to images", Toast.LENGTH_LONG);
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    21);
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Dialog result: " + resultCode);

        if (resultCode == RESULT_OK) {
            Uri path = data.getData();
            img_perfs.setImageURI(path);

            File file1 = new File(getRealPathFromURI(path, this));
            RequestBody requestFile2 = RequestBody.create(MediaType.parse(getContentResolver().getType(data.getData())), file1);
            MultipartBody.Part body2 = MultipartBody.Part.createFormData("image_file", file1.getName(), requestFile2);

            Call<ResponseBody> call_update = this.accountDAO.uploadImage(body2);
            call_update.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d(TAG, "Success " + response.code());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(TAG, "Error " + t.getMessage());
                }
            });

        }


    }

    public String getRealPathFromURI(Uri uri, Activity activity) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }
    public void cargarImagen() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent, 10);
    }

}
