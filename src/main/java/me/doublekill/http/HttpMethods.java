package me.doublekill.http;

import java.util.Date;

import me.doublekill.entity.User;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import org.apache.commons.lang3.time.FastDateFormat;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by wangjunling on 2017/5/10.
 */
public class HttpMethods
{

    private int DEFAULT_PAGE_SIZE = 30;

    private String authorization = "xxx";//身份基本认证：username:password形式拼接的字符串用base64编码，然后在前面加上Basic空格 .查看BaseAuthUtil

    private Retrofit retrofit;

    private GithubService githubService;

    public static HttpMethods getInstance()
    {
        return Singleton.INSTANCE.getInstance();
    }

    private HttpMethods()
    {
        OkHttpClient httpClient = new OkHttpClient()
                .newBuilder()
                .addInterceptor(
                        chain -> {
                            Request request = chain.request().newBuilder()
                                    .addHeader("Authorization", authorization)
                                    .build();
                            okhttp3.Response response = chain.proceed(request);
                            System.out.println(response.toString());
                            checkRemaining(response.headers());
                            return response;
                        }).build();

        retrofit = new Retrofit.Builder().baseUrl("https://api.github.com/").client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        githubService = retrofit.create(GithubService.class);
    }

    private enum Singleton
    {
        INSTANCE;

        private HttpMethods httpMethods;

        Singleton()
        {
            httpMethods = new HttpMethods();
        }

        public HttpMethods getInstance()
        {
            return httpMethods;
        }

    }

    public Observable<User> user(String username)
    {
        return githubService.user(username);
    }

    public Observable<User> followers(String username, int page)
    {
        return githubService.followers(username, page).flatMap(Observable::from);
    }

    public Observable<User> following(String username, int page)
    {
        return githubService.following(username, page).flatMap(Observable::from);
    }

    public Observable<User> follow(String username, int followers, int following)
    {
        System.out.println("查询关注user:" + username);
        Observable<User> followersUser = Observable.empty();
        Observable<User> followingUser = Observable.empty();
        if (following > 0)
        {
            int page = following / DEFAULT_PAGE_SIZE;
            page += (following - page * DEFAULT_PAGE_SIZE) > 0 ? 1 : 0;
            followingUser = Observable.range(1, page).flatMap(i -> following(username, i));
        }
        if (followers > 0)
        {
            int page = followers / DEFAULT_PAGE_SIZE;
            page += (followers - page * DEFAULT_PAGE_SIZE) > 0 ? 1 : 0;
            followersUser = Observable.range(1, page).flatMap(i -> followers(username, i));
        }
        return Observable.concat(followersUser, followingUser);
    }

    private void checkRemaining(Headers headers)
    {
        String remaining = headers.get("X-RateLimit-Remaining");
        String resetTime = headers.get("X-RateLimit-Reset");
        Date date = new Date(Long.valueOf(resetTime) * 1000);
        String resetTimeStr = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(date);

        System.out.println("在时间：" + resetTimeStr + "之前剩余次数：" + remaining);

        if (Integer.valueOf(remaining) == 0)
        {
            long l = date.getTime() - new Date().getTime();
            if (l > 0)
            {
                try
                {
                    System.out.println("程序睡眠！重新启动时间：" + resetTimeStr);
                    Thread.sleep(l + 1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
