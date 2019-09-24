package sky.it.com.stock_statistics.entity;

import java.io.Serializable;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/5/27 6:19 PM
 * @className: IconSelectBean
 * @description: 图标选择实体
 * @modified By:
 * @modifyDate:
 */
public class IconSelectBean implements Serializable {

    private String iconPath;

    private String iconName;

    private boolean isSelected;

    private String sortLetters;

    public IconSelectBean() {
    }

    public IconSelectBean(String iconPath, String iconName,
                          boolean isSelected, String sortLetters) {
        this.iconPath = iconPath;
        this.iconName = iconName;
        this.isSelected = isSelected;
        this.sortLetters = sortLetters;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
