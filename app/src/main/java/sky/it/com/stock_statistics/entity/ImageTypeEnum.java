package sky.it.com.stock_statistics.entity;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/5/26 12:23 PM
 * @className: ImageTypeEnum
 * @description: 图片类型的枚举值
 * @modified By:
 * @modifyDate:
 */
public enum ImageTypeEnum {

    STOCK_MANAGER(1),STOCK_NAME(2),
    STOCK_NUMBER(3),STOCK_PRODUCT_NAME(4),
    STOCK_UNIT(5),STOCK_AMOUNT(6),
    STOCK_TYPE(7),STOCK_MARK(8),
    STOCK_ICON(9);

    private int imageType;

    ImageTypeEnum(int imageType) {
        this.imageType = imageType;
    }

    public int getImageType() {
        return imageType;
    }
}
