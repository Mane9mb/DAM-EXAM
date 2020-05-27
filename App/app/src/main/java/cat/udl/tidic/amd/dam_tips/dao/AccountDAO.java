package cat.udl.tidic.amd.dam_tips.dao;

import com.google.gson.JsonObject;

import java.util.List;

import cat.udl.tidic.amd.dam_tips.models.Account;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface AccountDAO {

    @POST("account/create_token")
    Call<JsonObject> createTokenUser();

    @POST("account/delete_token")
    Call<Void> deleteTokenUser(@Body JsonObject token);

    @GET("/users/list")
    Call<List<Account>> getUsers();

    @GET("/users/show/{username}")
    Call<Account> getPerfilPublico(@Path("username") String username);

    @GET("/account/profile")
    Call<Account> getUserProfile();

    @Multipart
    @POST("/account/profile/update_profile_image")
    Call<ResponseBody>uploadImage(@Part  MultipartBody.Part image);



}
