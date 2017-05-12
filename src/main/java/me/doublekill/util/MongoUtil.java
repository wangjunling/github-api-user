package me.doublekill.util;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.assertions.Assertions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Created by wangjunling on 2017/5/8.
 */
public class MongoUtil
{
    private MongoClient mongoClient;

    private MongoUtil()
    {
        this.mongoClient = new MongoClient("localhost", 27017);
    }

    public static MongoUtil getInstance()
    {
        return MongoUtil.Singleton.INSTANCE.getInstance();
    }

    private enum Singleton
    {
        INSTANCE;
        private MongoUtil mongoUtil;

        Singleton()
        {
            this.mongoUtil = new MongoUtil();
        }

        public MongoUtil getInstance()
        {
            return mongoUtil;
        }
    }

    public MongoCollection<Document> getCollection(String dbName, String collectionName)
    {
        Assertions.notNull("dbName", dbName);
        Assertions.notNull("collectionName", collectionName);
        MongoDatabase database = mongoClient.getDatabase(dbName);
        return database.getCollection(collectionName);
    }

}
