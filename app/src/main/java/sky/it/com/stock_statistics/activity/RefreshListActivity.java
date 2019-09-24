package sky.it.com.stock_statistics.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sky.it.com.stock_statistics.R;
import sky.it.com.stock_statistics.adapter.RefreshListAdapter;
import sky.it.com.stock_statistics.adapter.StockListAdapter;
import sky.it.com.stock_statistics.entity.PullToRefreshBean;
import sky.it.com.stock_statistics.entity.SpinnerOption;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/3/31 11:55 AM
 * @className: RefreshListActivity
 * @description: 参考链接：  https://www.cnblogs.com/jshen/p/4097445.html
 * @modified By:
 * @modifyDate:
 */
public class RefreshListActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.wel_top_layout)
    RelativeLayout welTopLayout;
    @BindView(R.id.sp_stock_number)
    Spinner spStockNumber;
    @BindView(R.id.iv_excel)
    ImageView ivExcel;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    private List<PullToRefreshBean> data = new ArrayList<PullToRefreshBean>();
    private RefreshListAdapter stockListAdapter;


    @BindView(R.id.pullToRefresh)
    PullToRefreshListView pullToRefresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_list);
        ButterKnife.bind(this);

        initSpinner();

        data = getData();
        stockListAdapter = new RefreshListAdapter(this, data);
        pullToRefresh.setAdapter(stockListAdapter);

        pullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        init();


        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                // TODO Auto-generated method stub
                PullToRefreshBean bean = new PullToRefreshBean();
                bean.setNumber("下拉");
                bean.setStockNum("库房001");
                bean.setTotal(3);
                bean.setLeft(1);
                data.add(0, bean);

                new FinishRefresh().execute();
                stockListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                // TODO Auto-generated method stub
                PullToRefreshBean bean = new PullToRefreshBean();
                bean.setNumber("上拉");
                bean.setStockNum("库房011");
                bean.setTotal(30);
                bean.setLeft(13);
                data.add(bean);

                new FinishRefresh().execute();
                stockListAdapter.notifyDataSetChanged();
            }
        });
    }


    private void initSpinner() {
        ArrayList<SpinnerOption> towns;

        //准备好下拉框内容
        towns = new ArrayList<SpinnerOption>();
        for (int i = 0; i < 5; i++) {
            SpinnerOption c = new SpinnerOption("第 "+(i+1) + "", (i+1) + " 库房");
            towns.add(c);
        }

        //设置Adapter
        ArrayAdapter<SpinnerOption> townAdapter = new ArrayAdapter<SpinnerOption>
                (this, android.R.layout.simple_spinner_item, towns);
        townAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStockNumber.setAdapter(townAdapter);
        //监听
        spStockNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast toast = Toast.makeText(getApplicationContext(), towns.get(position).toString(), Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void init() {
        ILoadingLayout startLabels = pullToRefresh
                .getLoadingLayoutProxy(true, false);
        // 刚下拉时，显示的提示
        startLabels.setPullLabel("下拉刷新...");
        // 刷新时
        startLabels.setRefreshingLabel("正在载入...");
        // 下来达到一定距离时，显示的提示
        startLabels.setReleaseLabel("放开刷新...");

        ILoadingLayout endLabels = pullToRefresh.getLoadingLayoutProxy(
                false, true);
        // 刚下拉时，显示的提示
        endLabels.setPullLabel("上拉刷新...");
        // 刷新时
        endLabels.setRefreshingLabel("正在载入...");
        // 下来达到一定距离时，显示的提示
        endLabels.setReleaseLabel("放开刷新...");
    }

    private List<PullToRefreshBean> getData() {
        List<PullToRefreshBean> list = new ArrayList<PullToRefreshBean>();
        for (int i = 0; i < 20; i++) {
            PullToRefreshBean bean = new PullToRefreshBean();
            bean.setNumber(i + "");
            bean.setStockNum("库房" + i);
            bean.setTotal(3 * i);
            bean.setLeft(1 * i);
            list.add(bean);
        }

        return list;
    }


    private class FinishRefresh extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//          adapter.notifyDataSetChanged();
            pullToRefresh.onRefreshComplete();
        }
    }

}
