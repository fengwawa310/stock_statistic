package sky.it.com.stock_statistics.database.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import sky.it.com.stock_statistics.database.DatabaseHelper;
import sky.it.com.stock_statistics.database.bean.ArticleBean;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/3/30 9:30 PM
 * @className: ArticleDao
 * @description:操作article表的DAO类
 * @modified By:操作article表的DAO类
 * 参考链接
 * https://blog.csdn.net/industriously/article/details/50790624
 * @modifyDate:
 */
public class ArticleDao {

    private Context context;
    /**
     * ORMLite提供的DAO类对象，第一个泛型是要操作的数据表映射成的实体类；第二个泛型是这个实体类中ID的数据类型
     */
    private Dao<ArticleBean, Integer> dao;

    public ArticleDao(Context context) {
        this.context = context;
        try {
            this.dao = DatabaseHelper.getInstance(context).getDao(ArticleBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加数据
     * @param data
     */
    public void insert(ArticleBean data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除数据
     * @param data
     */
    public void delete(ArticleBean data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改数据
     * @param data
     */
    public void update(ArticleBean data) {
        try {
            dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过ID查询一条数据
     * @param id
     * @return
     */
    public ArticleBean queryById(int id) {
        ArticleBean article = null;
        try {
            article = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return article;
    }

    /**
     * 通过条件查询文章集合（通过用户ID查找）
     * @param user_id
     * @return
     */
    public List<ArticleBean> queryByUserId(int user_id) {
        try {
            return dao.queryBuilder().where().eq(ArticleBean.COLUMNNAME_USER, user_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
