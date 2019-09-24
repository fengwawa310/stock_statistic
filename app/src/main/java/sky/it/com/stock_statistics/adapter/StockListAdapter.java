package sky.it.com.stock_statistics.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sky.it.com.stock_statistics.R;
import sky.it.com.stock_statistics.database.bean.StockBean;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/3/31 11:14 AM
 * @className: StockListAdapter
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class StockListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<StockBean> list;
    private Context context;

    public StockListAdapter(Context context, List<StockBean> list) {
        this.context = context;
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
            convertView = mInflater.inflate(R.layout.stock_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //ID
        holder.itemId.setText(list.get(position).getId() + "");
        //仓库名称
        holder.itemName.setText(list.get(position).getName());
        //仓库嫌疑人
        holder.itemManager.setText(list.get(position).getManager());
        //编号
        holder.itemNumber.setText(list.get(position).getNumber() + "");
        //名称
        holder.itemProductName.setText(list.get(position).getProduct_name());
        //数量
        holder.itemAmount.setText(list.get(position).getAmount() + " " + list.get(position).getUnit());
        //特征
        holder.itemType.setText(list.get(position).getType());

        holder.itemMark.removeAllViews();
        String mark = list.get(position).getMark();
        if (!TextUtils.isEmpty(mark)) {
            String[] split = mark.split(",");
            for (String imageSrc : split) {
                addImg(imageSrc, holder.itemMark);
            }
        }

        return convertView;
    }

    /**
     * 动态添加imageView视图
     *
     * @param iconPath
     * @param itemMark
     */
    public void addImg(String iconPath, LinearLayout itemMark) {
        ImageView newImg = new ImageView(context);
        //设置想要的图片，相当于android:src="@drawable/image"
        Glide.with(context)
                .load(Uri.fromFile(new File(iconPath)))
                .into(newImg);
        //设置子控件在父容器中的位置布局，wrap_content,match_parent
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 也可以自己想要的宽度，参数（int width, int height）均表示px
        // 如dp单位，首先获取屏幕的分辨率在求出密度，根据屏幕ppi=160时，1px=1dp
        //则公式为 dp * ppi / 160 = px ——> dp * dendity = px
        //如设置为48dp：1、获取屏幕的分辨率 2、求出density 3、设置
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                (int) (17 * density),
                (int) (17 * density));

        //相当于android:layout_marginLeft="4dp"
        params1.leftMargin = 5;


        //addView(View child, LayoutParams params)，往已有的view后面添加，后插入,并设置布局
        itemMark.addView(newImg, params1);
    }


    static class ViewHolder {
        @BindView(R.id.item_id)
        TextView itemId;
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.item_manager)
        TextView itemManager;
        @BindView(R.id.item_number)
        TextView itemNumber;
        @BindView(R.id.item_product_name)
        TextView itemProductName;
        @BindView(R.id.item_amount)
        TextView itemAmount;
        @BindView(R.id.item_type)
        TextView itemType;
        @BindView(R.id.item_mark)
        LinearLayout itemMark;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
