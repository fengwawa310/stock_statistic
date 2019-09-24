package sky.it.com.stock_statistics.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sky.it.com.stock_statistics.R;
import sky.it.com.stock_statistics.adapter.IconListAdapter;
import sky.it.com.stock_statistics.entity.IconSelectBean;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/5/27 5:57 PM
 * @className: TestActivity
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class TestActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.lv_test)
    ListView lvTest;
    @BindView(R.id.btn_confirm)
    TextView btnConfirm;

    private List<IconSelectBean> list = new ArrayList<>();
    private IconListAdapter adapter;
    private List<Integer> iconPaths = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        initData();
        initView();

//        lvTest.setOnItemClickListener((parent, view, position, id) -> {
//            IconListAdapter.ViewHolder viewHolder = (IconListAdapter.ViewHolder) view.getTag();
//            // 改变CheckBox的状态
//            viewHolder.cbIcon.toggle();
//            // 将CheckBox的选中状况记录下来
//            list.get(position).setSelected(viewHolder.cbIcon.isChecked());
//        });

    }

    //初始化数据
    private void initData() {
        List<Integer> iconPaths = new ArrayList<Integer>() {
            {
                add(R.mipmap.banner_001);
                add(R.mipmap.banner_002);
                add(R.mipmap.banner_003);
                add(R.mipmap.banner_004);

                add(R.mipmap.banner_004);
                add(R.mipmap.banner_004);
                add(R.mipmap.banner_004);
                add(R.mipmap.banner_004);
                add(R.mipmap.banner_004);
                add(R.mipmap.banner_004);
                add(R.mipmap.banner_004);
                add(R.mipmap.banner_004);
            }
        };

        List<String> iconNames = new ArrayList<String>() {
            {
                add("大众");
                add("本田");
                add("奔驰");
                add("宝马");

                add("宝马1");
                add("宝马2");
                add("宝马3");
                add("宝马4");
                add("宝马5");
                add("宝马6");
                add("宝马7");
                add("宝马8");
            }
        };

        for (int i = 0; i < iconNames.size(); i++) {
            IconSelectBean iconSelectBean = new IconSelectBean();
            iconSelectBean.setIconName(iconNames.get(i));
            iconSelectBean.setSelected(false);
            list.add(iconSelectBean);
        }
    }

    private void initView() {
        btnConfirm.setOnClickListener(this::onClick);
        adapter = new IconListAdapter(list, TestActivity.this);
        lvTest.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }




}
