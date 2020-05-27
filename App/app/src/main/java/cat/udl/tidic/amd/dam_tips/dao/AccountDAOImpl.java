package cat.udl.tidic.amd.dam_tips.dao;

import com.google.gson.JsonObject;

import java.util.List;

import cat.udl.tidic.amd.dam_tips.models.Account;
import cat.udl.tidic.amd.dam_tips.network.RetrofitClientInstance;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class AccountDAOImpl implements AccountDAO{

    private Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();

    @Override
    public Call<JsonObject> createTokenUser() {
        return  retrofit.create(AccountDAO.class).createTokenUser();
    }

    @Override
    public Call<Void> deleteTokenUser(JsonObject token) {
        return  retrofit.create(AccountDAO.class).deleteTokenUser(token);
    }


    @Override
    public Call<List<Account>> getUsers() {
        return  retrofit.create(AccountDAO.class).getUsers();
    }

    @Override
    public Call<Account> getPerfilPublico(String username) {
        return retrofit.create(AccountDAO.class).getPerfilPublico(username);
    }
    @Override
    public Call<Account> getUserProfile(){

        return retrofit.create(AccountDAO.class).getUserProfile();
    }
    @Override
    public Call<ResponseBody>uploadImage(MultipartBody.Part image){
        return retrofit.create(AccountDAO.class).uploadImage(image);
    }
}
