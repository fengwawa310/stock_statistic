package sky.it.com.stock_statistics.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sky.it.com.stock_statistics.R;
import sky.it.com.stock_statistics.entity.PullToRefreshBean;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/5/28 11:41 AM
 * @className: RefreshListAdapter
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class RefreshListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<PullToRefreshBean> list;

    public RefreshListAdapter(Context context, List<PullToRefreshBean> list) {
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
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
            convertView = mInflater.inflate(R.layout.refresh_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtNumber.setText(list.get(position).getNumber());
        holder.txtStockNum.setText(list.get(position).getStockNum());
        holder.txtTotal.setText(list.get(position).getTotal() + "");
        holder.txtLeft.setText(list.get(position).getLeft() + "");

        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.txt_number)
        TextView txtNumber;
        @BindView(R.id.txt_stockNum)
        TextView txtStockNum;
        @BindView(R.id.txt_total)
        TextView txtTotal;
        @BindView(R.id.txt_type)
        TextView txtType;
        @BindView(R.id.txt_unit)
        TextView txtUnit;
        @BindView(R.id.txt_amount)
        TextView txtAmount;
        @BindView(R.id.txt_left)
        TextView txtLeft;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
