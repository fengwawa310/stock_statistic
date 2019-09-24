package sky.it.com.stock_statistics.entity;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/3/31 3:33 PM
 * @className: SpinnerOption
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class SpinnerOption {

    private String value = "";
    private String text = "";

    public SpinnerOption() {
        value = "";
        text = "";
    }

    public SpinnerOption(String value, String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
    public String getValue() {
        return value;
    }
    public String getText() {
        return text;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setText(String text) {
        this.text = text;
    }
}
