package me.doublekill.dao;

import me.doublekill.entity.User;
import me.doublekill.util.MongoUtil;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;

/**
 * Created by wangjunling on 2017/5/12.
 */
public class UserDao
{
    private MongoCollection<Document> collection = MongoUtil.getInstance().getCollection("github", "user");

    public void save(User user)
    {
        if (notExist(user.getLogin()))
        {
            Gson gson = new Gson();
            collection.insertOne(Document.parse(gson.toJson(user)));
            System.out.println("保存成功! user: " + user.getLogin());
        }
    }

    public Document find(String userName)
    {
        return collection.find(new Document("login", userName)).first();
    }

    public boolean notExist(String username)
    {
        return find(username) == null;
    }
}
