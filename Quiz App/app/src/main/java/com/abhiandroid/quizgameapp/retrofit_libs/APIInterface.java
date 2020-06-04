package com.abhiandroid.quizgameapp.retrofit_libs;

import com.abhiandroid.quizgameapp.model.Category_Pojo;
import com.abhiandroid.quizgameapp.model.AddFavourite_Pojo;
import com.abhiandroid.quizgameapp.model.Faq_Pojo;
import com.abhiandroid.quizgameapp.model.Favourite_Pojo;
import com.abhiandroid.quizgameapp.model.Info_Pojo;
import com.abhiandroid.quizgameapp.model.PushNotification_Pojo;
import com.abhiandroid.quizgameapp.model.Questions_Pojo;
import com.abhiandroid.quizgameapp.model.QuizList_Pojo;
import com.abhiandroid.quizgameapp.model.SubCategory_Pojo;
import com.abhiandroid.quizgameapp.model.Terms_pojo;
import com.abhiandroid.quizgameapp.model.UpdateScorePojo;
import com.abhiandroid.quizgameapp.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Developed by AbhiAndroid.com
 */

public interface APIInterface {



    @POST("student/register")
    Call<User> createUser(@Body User.Credentials credentials);


    @POST("subcategories")
    Call<SubCategory_Pojo> getSubcategories(@Query("cat_id") String cat_id,@Query("page") String page);

    @POST("search?")
    Call<Category_Pojo> searchCategories(@Query("title") String title,@Query("page") String page);

    @POST("searchsubcat?")
    Call<SubCategory_Pojo> searchSubCategories(@Query("title") String title,@Query("page") String page, @Query("cat_id") String cat_id);

    @POST("searchquizz?")
    Call<QuizList_Pojo> searchQuizes(@Query("title") String title,@Query("page") String page,@Query("subcat_id") String subcat_id);

    @GET("categories")
    Call<Category_Pojo> getCategories(@Query("page") String page);

    @POST("quizzes?")
    Call<QuizList_Pojo> getQuizes(@Query("subcat_id") String subcat_id,@Query("page") String page);

    @POST("questions?")
    Call<Questions_Pojo> getQuestions(@Query("quiz_id") String quiz_id,@Query("student_id") String student_id);

    @POST("userfavourites?")
    Call<QuizList_Pojo> userFavourites(@Query("student_id") String student_id);

    @POST("favourites?")
    Call<AddFavourite_Pojo> addFavourite(@Query("student_id") String student_id, @Query("quiz_id") String quiz_id, @Query("quiz_name") String quiz_name);

    @POST("token?")
    Call<PushNotification_Pojo> sendToken(@Query("student_id") String student_id, @Query("access_token") String access_token);

    @POST("scores?")
    Call<UpdateScorePojo> updateScore(@Query("student_id") String student_id, @Query("quiz_id") String quiz_id, @Query("quiz_name") String quiz_name
            , @Query("quiz_image") String quiz_image , @Query("score") String score);
    @GET("getrules")
    Call<Info_Pojo> getInfo();
    @GET("getterms")
    Call<Terms_pojo> getTerms();
    @GET("getfaqs")
    Call<Faq_Pojo> getFaq();
    /*@FormUrlEncoded
    @GET("categories")
    Call<Quiz> doCreateUserWithField(@Field("name") String name, @Field("job") String job);*/
}


/**
 * Developed by AbhiAndroid.com
 */