package sky.it.com.stock_statistics.adapter.adaptertool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.List;

/**
 * Created by SYSTEM on 2017/4/29.
 */
public abstract class MyBaseAdapterExpandbleListview<T>  extends BaseExpandableListAdapter {
    protected Context mContext;
    protected List<T> mDatasParent;
    protected List<T> mDatasChild;
    protected LayoutInflater mInflater;
    private int mLayoutIdParent;
    private int mLayoutIdChild;
    public MyBaseAdapterExpandbleListview(Context context, List<T> datasParent,  List<T> datasChild, int layoutIdParent,int layoutIdChild) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mDatasParent = datasParent;
        this.mDatasChild = datasChild;
        this.mLayoutIdParent = layoutIdParent;
        this.mLayoutIdChild = layoutIdChild;
    }
    @Override
    public int getGroupCount() {
        return mDatasParent.size();
    }
    @Override
    public int getChildrenCount(int i) {
        return 1;
    }
    @Override
    public T getGroup(int i) {
        return mDatasParent.get(i);
    }
    @Override
    public T getChild(int i, int i1) {
        return mDatasChild.get(i);
    }
    @Override
    public long getGroupId(int i) {
        return i;
    }
    @Override
    public long getChildId(int i, int i1) {
        return i;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        MyViewHolderExpandbleListView holder = MyViewHolderExpandbleListView.get(mContext,view,viewGroup,mLayoutIdParent,i);
        convertParent(holder, getGroup(i),i);
        return holder.getConvertView();
    }
    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        MyViewHolderExpandbleListView holder = MyViewHolderExpandbleListView.get(mContext,view,viewGroup,mLayoutIdChild,i);
        convertChild(holder, getChild(i,i1),i,i1);
        return holder.getConvertView();
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
    public abstract void convertParent(MyViewHolderExpandbleListView holder, T t,int parentPosition);
    public abstract void convertChild(MyViewHolderExpandbleListView holder, T t,int parentPosition,int childPosition);
}
