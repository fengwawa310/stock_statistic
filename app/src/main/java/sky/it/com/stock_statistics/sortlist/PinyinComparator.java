package sky.it.com.stock_statistics.sortlist;

import java.util.Comparator;

import sky.it.com.stock_statistics.entity.IconSelectBean;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/7/28 11:54 AM
 * @className: PinyinComparator
 * @description:
 * @modified By:
 * @modifyDate:
 */

public class PinyinComparator implements Comparator<IconSelectBean> {

    @Override
    public int compare(IconSelectBean o1, IconSelectBean o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }

}

