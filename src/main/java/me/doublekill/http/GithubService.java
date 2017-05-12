package me.doublekill.http;

import java.util.List;

import me.doublekill.entity.User;
import retrofit2.Response;
import retrofit2.http.*;
import rx.Observable;

/**
 * Created by wangjunling on 2017/5/8.
 */
public interface GithubService
{

    @GET("/users/{user}")
    Observable<User> user( @Path("user") String user);

    @GET("/users/{user}/followers")
    Observable<List<User>> followers(@Path("user") String user,@Query("page") int page);

    @GET("/users/{user}/following")
    Observable<List<User>> following(@Path("user") String user,@Query("page") int page);
}
