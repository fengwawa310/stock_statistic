package sky.it.com.stock_statistics.database.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import sky.it.com.stock_statistics.database.DatabaseHelper;
import sky.it.com.stock_statistics.database.bean.IconBean;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/3/30 9:28 PM
 * @className: IconDao
 * @description:* 操作Icon数据表的Dao类，封装这操作Icon表的所有操作
 *  * 通过DatabaseHelper类中的方法获取ORMLite内置的DAO类进行数据库中数据的操作
 *  * <p>
 *  * 调用dao的create()方法向表中添加数据
 *  * 调用dao的delete()方法删除表中的数据
 *  * 调用dao的update()方法修改表中的数据
 *  * 调用dao的queryForAll()方法查询表中的所有数据
 * @modified By:
 * @modifyDate:
 */
public class IconDao {

    private Context context;
    /**
     * ORMLite提供的DAO类对象，第一个泛型是要操作的数据表映射成的实体类；
     * 第二个泛型是这个实体类中ID的数据类型
     */
    private Dao<IconBean, Integer> dao;

    public IconDao(Context context) {
        this.context = context;
        try {
            this.dao = DatabaseHelper.getInstance(context).getDao(IconBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向icon表中添加一条数据
     * @param data
     */
    public void insert(IconBean data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除icon表中的一条数据
     * @param data
     */
    public void delete(IconBean data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除icon表中的所有数据
     */
    public void deleteAll() {
        try {
            dao.delete(selectAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改icon表中的一条数据
     * @param data
     */
    public void update(IconBean data) {
        try {
            dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询icon表中的所有数据
     * @return
     */
    public List<IconBean> selectAll() {
        List<IconBean> icons = null;
        try {
            icons = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return icons;
    }

    /**
     * 根据ID取出用户信息
     * @param id
     * @return
     */
    public IconBean queryById(int id) {
        IconBean icon = null;
        try {
            icon = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return icon;
    }


}
