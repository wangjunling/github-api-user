package me.doublekill;

import java.util.Arrays;
import java.util.List;

import me.doublekill.dao.UserDao;
import me.doublekill.entity.User;
import me.doublekill.http.HttpMethods;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by wangjunling on 2017/5/10.
 */
public class GetUserMain
{
    private static UserDao userDao = new UserDao();

    private static HttpMethods httpMethods = HttpMethods.getInstance();

    public static void main(String[] args)
    {
        ex(Arrays.asList("tj"));
    }

    public static void ex(List<String> users)
    {
        Observable.from(users)
                .flatMap(s -> httpMethods.user(s))
                .doOnNext(userDao::save)
                .flatMap(user -> httpMethods.follow(user.getLogin(), user.getFollowers(), user.getFollowing()))
                .map(User::getLogin)
                .distinct()
                .filter(userDao::notExist)
                .toList()
                .subscribe(new Subscriber<List<String>>()
                {
                    @Override
                    public void onCompleted()
                    {
                        System.out.println("onCompleted");
                    }

                    @Override
                    public void onError(Throwable throwable)
                    {
                        System.out.println("onError" + throwable);
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onNext(List<String> strings)
                    {
                        System.out.println("本次查询到的用户：" + strings);
                        if (strings != null && strings.size() > 0)
                        {
                            GetUserMain.ex(strings);// 尾递归
                        }
                    }
                });
    }
}