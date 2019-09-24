package sky.it.com.stock_statistics.database.bean;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/3/30 9:16 PM
 * @className: UserBean
 * @description:* UserBean实体类，存储数据库中user表中的数据
 *  * <p>
 *  * 注解：
 *  * DatabaseTable：通过其中的tableName属性指定数据库名称
 *  * DatabaseField：代表数据表中的一个字段
 *  * ForeignCollectionField：一对多关联，表示一个UserBean关联着多个ArticleBean（必须使用ForeignCollection集合）
 *  * <p>
 *  * 属性：
 *  * id：当前字段是不是id字段（一个实体类中只能设置一个id字段）
 *  * columnName：表示当前属性在表中代表哪个字段
 *  * generatedId：设置属性值在数据表中的数据是否自增
 *  * useGetSet：是否使用Getter/Setter方法来访问这个字段
 *  * canBeNull：字段是否可以为空，默认值是true
 *  * unique：是否唯一
 *  * defaultValue：设置这个字段的默认值
 * @modified By:
 * @modifyDate:
 */

/**
 * 指定数据表的名称
 */
@DatabaseTable(tableName = "user")
public class UserBean {

    // 定义字段在数据库中的字段名
    public static final String COLUMNNAME_ID = "id";
    public static final String COLUMNNAME_NAME = "name";
    public static final String COLUMNNAME_SEX = "sex";
    public static final String COLUMNNAME_BIRTHDAY = "birthday";
    public static final String COLUMNNAME_ADDRESS = "address";

    @DatabaseField(generatedId = true, columnName = COLUMNNAME_ID, useGetSet = true)
    private int id;
    @DatabaseField(columnName = COLUMNNAME_NAME, useGetSet = true, canBeNull = false, unique = true)
    private String name;
    @DatabaseField(columnName = COLUMNNAME_SEX, useGetSet = true, defaultValue = "1")
    private char sex;
    @DatabaseField(columnName = COLUMNNAME_BIRTHDAY, useGetSet = true)
    private Date birthday;
    @DatabaseField(columnName = COLUMNNAME_ADDRESS, useGetSet = true)
    private String address;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<ArticleBean> articles;

    public UserBean() {
    }

    public UserBean(String name, char sex, Date birthday, String address) {
        this.name = name;
        this.sex = sex;
        this.birthday = birthday;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ForeignCollection<ArticleBean> getArticles() {
        return articles;
    }

    public void setArticles(ForeignCollection<ArticleBean> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", birthday=" + birthday +
                ", address='" + address + '\'' +
                ", articles=" + articles +
                '}';
    }

}
