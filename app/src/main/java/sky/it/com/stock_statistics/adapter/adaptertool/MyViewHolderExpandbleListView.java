package sky.it.com.stock_statistics.adapter.adaptertool;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by SYSTEM on 2017/4/29.
 */
public class MyViewHolderExpandbleListView {
    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    public MyViewHolderExpandbleListView(Context context,
                                         ViewGroup parent, int layoutId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static MyViewHolderExpandbleListView get(Context context, View convertView,
                                                    ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new MyViewHolderExpandbleListView(context, parent, layoutId, position);
        } else {
            MyViewHolderExpandbleListView holder = (MyViewHolderExpandbleListView) convertView.getTag();
            holder.mPosition = position;
            return holder;
        }
    }
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
    public View getConvertView() {
        return mConvertView;
    }
}
