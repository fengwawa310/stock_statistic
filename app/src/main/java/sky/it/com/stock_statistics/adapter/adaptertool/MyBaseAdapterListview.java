package sky.it.com.stock_statistics.adapter.adaptertool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by SYSTEM on 2017/4/29.
 */
public abstract class MyBaseAdapterListview<T>  extends BaseAdapter {
    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    private int mLayoutId;
    public MyBaseAdapterListview(Context context, List<T> datas,int layoutId)
    {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mDatas = datas;
        this.mLayoutId = layoutId;
    }
    @Override
    public int getCount() {
        return mDatas.size();
    }
    @Override
    public T getItem(int i) {
        return mDatas.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MyViewHolderExpandbleListView holder = MyViewHolderExpandbleListView.get(
                mContext, view, viewGroup, mLayoutId, i);
        convert(holder, getItem(i),i);
        return holder.getConvertView();
    }
    public abstract void convert(MyViewHolderExpandbleListView holder, T t,int position);

}
