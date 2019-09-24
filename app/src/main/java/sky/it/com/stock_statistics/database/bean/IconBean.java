package sky.it.com.stock_statistics.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/5/26 4:46 PM
 * @className: StockBean
 * @description: 关于仓库实体的表
 * @modified By:
 * @modifyDate:
 */
@DatabaseTable(tableName = "icon")
public class IconBean {

    /**
     * 定义字段在数据库中的字段名
     */
    public static final String COLUMNNAME_ID = "id";
    /**
     * 品牌名称
     */
    public static final String COLUMNNAME_BANNER = "banner";
    /**
     * icon路径
     */
    public static final String COLUMNNAME_PATH = "path";


    @DatabaseField(generatedId = true, columnName = COLUMNNAME_ID, useGetSet = true)
    private int id;
    @DatabaseField(columnName = COLUMNNAME_BANNER, useGetSet = true)
    private String banner;
    @DatabaseField(columnName = COLUMNNAME_PATH, useGetSet = true)
    private String path;

    public IconBean() {
    }

    public IconBean(int id, String banner, String path) {
        this.id = id;
        this.banner = banner;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
