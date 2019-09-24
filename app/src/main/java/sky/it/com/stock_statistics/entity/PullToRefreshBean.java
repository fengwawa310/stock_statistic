package sky.it.com.stock_statistics.entity;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/3/31 11:24 AM
 * @className: PullToRefreshBean
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class PullToRefreshBean {

    private String number;
    private int total;
    private int left;
    private String stockNum;

    public PullToRefreshBean() {
    }

    public PullToRefreshBean(String number, int total, int left, String stockNum) {
        this.number = number;
        this.total = total;
        this.left = left;
        this.stockNum = stockNum;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public String getStockNum() {
        return stockNum;
    }

    public void setStockNum(String stockNum) {
        this.stockNum = stockNum;
    }
}
