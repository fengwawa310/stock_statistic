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
@DatabaseTable(tableName = "stock")
public class StockBean {

    /**
     * 定义字段在数据库中的字段名
     */
    public static final String COLUMNNAME_ID = "id";
    /**
     * 仓库管理人
     */
    public static final String COLUMNNAME_STOCK_MANAGER = "manager";
    /**
     * 仓库名称
     */
    public static final String COLUMNNAME_STOCK_NAME = "name";
    /**
     * 产品编码
     */
    public static final String COLUMNNAME_STOCK_NUMBER = "number";
    /**
     * 产品名称
     */
    public static final String COLUMNNAME_STOCK_PRODUCT_NAME = "product_name";
    /**
     * 产品单位
     */
    public static final String COLUMNNAME_STOCK_UNIT = "unit";
    /**
     * 产品数量
     */
    public static final String COLUMNNAME_STOCK_AMOUNT = "amount";
    /**
     * 产品特征
     */
    public static final String COLUMNNAME_STOCK_TYPE = "type";
    /**
     * 带有图标
     */
    public static final String COLUMNNAME_STOCK_MARK = "mark";

    @DatabaseField(generatedId = true, columnName = COLUMNNAME_ID, useGetSet = true)
    private int id;
    @DatabaseField(columnName = COLUMNNAME_STOCK_MANAGER, useGetSet = true)
    private String manager;
    @DatabaseField(columnName = COLUMNNAME_STOCK_NAME, useGetSet = true)
    private String name;
    @DatabaseField(columnName = COLUMNNAME_STOCK_NUMBER, useGetSet = true)
    private int number;
    @DatabaseField(columnName = COLUMNNAME_STOCK_PRODUCT_NAME, useGetSet = true)
    private String product_name;
    @DatabaseField(columnName = COLUMNNAME_STOCK_UNIT, useGetSet = true)
    private String unit;
    @DatabaseField(columnName = COLUMNNAME_STOCK_AMOUNT, useGetSet = true)
    private int amount;
    @DatabaseField(columnName = COLUMNNAME_STOCK_TYPE, useGetSet = true)
    private String type;
    @DatabaseField(columnName = COLUMNNAME_STOCK_MARK, useGetSet = true)
    private String mark;

    public StockBean() {
    }

    public StockBean(int id, String manager, String name, int number,
                     String product_name, String unit, int amount,
                     String type, String mark) {
        this.id = id;
        this.manager = manager;
        this.name = name;
        this.number = number;
        this.product_name = product_name;
        this.unit = unit;
        this.amount = amount;
        this.type = type;
        this.mark = mark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

}
