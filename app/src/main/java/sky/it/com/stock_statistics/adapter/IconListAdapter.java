package sky.it.com.stock_statistics.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sky.it.com.stock_statistics.R;
import sky.it.com.stock_statistics.entity.IconSelectBean;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/5/27 6:42 PM
 * @className: IconListAdapter
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class IconListAdapter extends BaseAdapter implements SectionIndexer {

    private List<IconSelectBean> list;
    private Context context;

    public IconListAdapter(List<IconSelectBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.icon_select_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //记录点击状态
        holder.cbIcon.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                list.get(position).setSelected(true);
            }else{
                list.get(position).setSelected(false);
            }
        });

        Glide.with(context)
                .load(Uri.fromFile(new File(list.get(position).getIconPath())))
                .into(holder.ivIcon);
        holder.txtIconName.setText(list.get(position).getIconName());

        //根据记录的单机状态初始化列表
        if(list.get(position).isSelected()){
            holder.cbIcon.setChecked(true);
        }else{
            holder.cbIcon.setChecked(false);
        }

        return convertView;
    }

    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    /**
     * 更新视图
     * @param list
     */
    public void updateListView(List<IconSelectBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    public static class ViewHolder {
        @BindView(R.id.iv_icon)
        public ImageView ivIcon;
        @BindView(R.id.txt_icon_name)
        public TextView txtIconName;
        @BindView(R.id.cb_icon)
        public CheckBox cbIcon;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
