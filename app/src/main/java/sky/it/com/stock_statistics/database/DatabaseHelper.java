package sky.it.com.stock_statistics.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.HashMap;
import java.util.Map;

import sky.it.com.stock_statistics.database.bean.ArticleBean;
import sky.it.com.stock_statistics.database.bean.IconBean;
import sky.it.com.stock_statistics.database.bean.StockBean;
import sky.it.com.stock_statistics.database.bean.UserBean;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/3/30 9:10 PM
 * @className: DatabaseHelper
 * @description: 数据库操作管理工具类
 * * <p>
 * * 我们需要自定义一个类继承自ORMlite给我们提供的OrmLiteSqliteOpenHelper，创建一个构造方法，重写两个方法onCreate()和onUpgrade()
 * * 在onCreate()方法中使用TableUtils类中的createTable()方法初始化数据表
 * * 在onUpgrade()方法中我们可以先删除所有表，然后调用onCreate()方法中的代码重新创建表
 * * <p>
 * * 我们需要对这个类进行单例，保证整个APP中只有一个SQLite Connection对象
 * * <p>
 * * 这个类通过一个Map集合来管理APP中所有的DAO，只有当第一次调用这个DAO类时才会创建这个对象（并存入Map集合中）
 * * 其他时候都是直接根据实体类的路径从Map集合中取出DAO对象直接调用
 * @modified By:
 * @modifyDate:
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    /**
     * 数据库名称
     */
    public static final String DATABASE_NAME = "stock.db";

    /**
     * 本类的单例实例
     */
    private static DatabaseHelper instance;

    /**
     * 存储APP中所有的DAO对象的Map集合
     */
    private Map<String, Dao> daos = new HashMap<>();

    /**
     * 获取本类单例对象的方法
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(context);
                }
            }
        }
        return instance;
    }

    /**
     * 私有的构造方法
     *
     * @param context
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,
                null, 1);
    }


    /**
     * 根据传入的DAO的路径获取到这个DAO的单例对象（要么从daos这个Map中获取，要么新创建一个并存入daos）
     *
     * @param clazz
     * @return
     * @throws SQLException
     */
    @Override
    public synchronized Dao getDao(Class clazz) throws java.sql.SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao((Class<Object>) clazz);
            daos.put(className, dao);
        }
        return dao;
    }


    /**
     * 创建数据库时调用的方法
     * @param database
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, UserBean.class);
            TableUtils.createTable(connectionSource, ArticleBean.class);
            TableUtils.createTable(connectionSource, StockBean.class);
            TableUtils.createTable(connectionSource, IconBean.class);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据库版本更新时调用的方法
     * @param database
     * @param connectionSource
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, UserBean.class, true);
            TableUtils.dropTable(connectionSource, ArticleBean.class, true);
            TableUtils.dropTable(connectionSource, StockBean.class, true);
            TableUtils.dropTable(connectionSource, IconBean.class, true);
            onCreate(database, connectionSource);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }

}
