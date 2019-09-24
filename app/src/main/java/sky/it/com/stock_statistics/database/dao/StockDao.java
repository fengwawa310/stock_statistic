package sky.it.com.stock_statistics.database.dao;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sky.it.com.stock_statistics.database.DatabaseHelper;
import sky.it.com.stock_statistics.database.bean.StockBean;
import sky.it.com.stock_statistics.entity.SpinnerOption;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/3/30 9:28 PM
 * @className: StockDao
 * @description:* 操作Stock数据表的Dao类，封装这操作Stock表的所有操作
 * * 通过DatabaseHelper类中的方法获取ORMLite内置的DAO类进行数据库中数据的操作
 * * <p>
 * * 调用dao的create()方法向表中添加数据
 * * 调用dao的delete()方法删除表中的数据
 * * 调用dao的update()方法修改表中的数据
 * * 调用dao的queryForAll()方法查询表中的所有数据
 * @modified By:
 * @modifyDate:
 */
public class StockDao {

    private Context context;
    /**
     * ORMLite提供的DAO类对象，第一个泛型是要操作的数据表映射成的实体类；
     * 第二个泛型是这个实体类中ID的数据类型
     */
    private Dao<StockBean, Integer> dao;

    public StockDao(Context context) {
        this.context = context;
        try {
            this.dao = DatabaseHelper.getInstance(context).getDao(StockBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向stock表中添加一条数据
     *
     * @param data
     */
    public void insert(StockBean data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除stock表中的一条数据
     *
     * @param data
     */
    public void delete(StockBean data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除stock表中的所有数据
     */
    public void deleteAll() {
        try {
            dao.delete(selectAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改stock表中的一条数据
     *
     * @param data
     */
    public void update(StockBean data) {
        try {
            dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询stock表中的所有数据
     *
     * @return
     */
    public List<StockBean> selectAll() {
        List<StockBean> stocks = null;
        try {
            stocks = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stocks;
    }

    /**
     * 根据ID取出用户信息
     *
     * @param id
     * @return
     */
    public StockBean queryById(int id) {
        StockBean stock = null;
        try {
            stock = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stock;
    }


    /**
     * 根据名称查找列表
     *
     * @param product_name 名称
     * @return
     */
    public List<StockBean> queryByStockNumber(String product_name) {
        List<StockBean> beanList = new ArrayList<>();
        try {
            beanList =
                    dao.queryBuilder().where().eq("product_name", product_name).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return beanList;
    }


    /**
     * 初始化spinner数据
     *
     * @param type
     * @return
     */
    public List<SpinnerOption> queryForSpinner(String type) {

        List<SpinnerOption> options = new ArrayList<>();
        QueryBuilder<StockBean, Integer> queryBuilder = dao.queryBuilder();
        if ("type".equals(type)) {
            //查询特征列表
            try {
                List<StockBean> beans = queryBuilder
                        .selectColumns("type").distinct().query();
                for (StockBean bean : beans) {
                    SpinnerOption option = new SpinnerOption();
                    option.setValue(bean.getId() + "");
                    option.setText(bean.getType());
                    options.add(option);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if ("name".equals(type)) {
            //查询仓库列表
            try {
                List<StockBean> beans = queryBuilder
                        .selectColumns("name").distinct().query();
                for (StockBean bean : beans) {
                    SpinnerOption option = new SpinnerOption();
                    option.setValue(bean.getId() + "");
                    option.setText(bean.getName());
                    options.add(option);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if ("product_name".equals(type)) {
            //查询名称列表
            try {
                List<StockBean> beans = queryBuilder
                        .selectColumns("product_name").distinct().query();
                for (StockBean bean : beans) {
                    SpinnerOption option = new SpinnerOption();
                    option.setValue(bean.getId() + "");
                    option.setText(bean.getProduct_name());
                    options.add(option);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return options;

    }


    /**
     * 通过查询条件查询  相等条件
     *
     * @param name         仓库名称
     * @param product_name 名称
     * @param type         特征
     * @return
     */
    public List<StockBean> queryByCondition(String name, String product_name, String type) {

        String defaultStr = "请选择";

        List<StockBean> beanList = new ArrayList<>();

        try {
            //创建查询条件
            Where<StockBean, Integer> where = dao.queryBuilder().where().isNotNull("id");
            if (!defaultStr.equals(name)) {
                where.and().eq("name", name);
            }
            if (!defaultStr.equals(product_name)) {
                where.and().eq("product_name", product_name);
            }
            if (!defaultStr.equals(type)) {
                where.and().eq("type", type);
            }
            beanList = where.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return beanList;
    }

    /**
     * 通过查询条件查询  模糊查询
     *
     * @param name         仓库名称
     * @param product_name 名称
     * @param type         特征
     * @return
     */
    public List<StockBean> queryDimByCondition(String name, String product_name, String type) {

        List<StockBean> beanList = new ArrayList<>();

        try {
            //创建查询条件
            Where<StockBean, Integer> where = dao.queryBuilder().where().isNotNull("id");
            if (!TextUtils.isEmpty(name)) {
                where.and().like("name", "%" + name + "%");
            }
            if (!TextUtils.isEmpty(product_name)) {
                where.and().like("product_name", "%" + product_name + "%");
            }
            if (!TextUtils.isEmpty(type)) {
                where.and().like("type", "%" + type + "%");
            }
            beanList = where.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return beanList;
    }


    /**
     * 通过特征查询  相等条件
     *
     * @param stockType
     * @return
     */
    public List<StockBean> queryByStockType(String stockType) {
        List<StockBean> beanList = new ArrayList<>();
        try {
            beanList =
                    dao.queryBuilder().where()
                            .eq("type", stockType)
                            .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return beanList;
    }


    /**
     * 通过仓库查询以名称字段分组，查询分组总数  相等条件 自定义sql
     * @param stockName 仓库名称
     * @return
     */
    public List<Map<String, String>> queryByStockName(String stockName) {
        List<Map<String, String>> strList = new ArrayList<>();
        try {
            String sql = "select product_name,sum(amount) from stock where name = '" + stockName + "' group by product_name";
            GenericRawResults<Map<String, String>> stockBeans = dao.queryRaw(sql, (columnNames, resultColumns) -> {
                for (int i = 0; i < resultColumns.length; i++) {
                    System.out.println(resultColumns[i]);
                }
                Map<String, String> map = new HashMap<>();
                map.put("product_name", resultColumns[0]);
                map.put("sum", resultColumns[1]);
                return map;
            });

            Iterator<Map<String, String>> iterator = stockBeans.iterator();
            while (iterator.hasNext()) {
                Map<String, String> map = iterator.next();
                strList.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return strList;
    }

}
