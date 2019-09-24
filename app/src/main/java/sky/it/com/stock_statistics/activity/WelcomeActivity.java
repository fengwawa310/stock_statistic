package sky.it.com.stock_statistics.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.liyi.sutils.utils.SUtils;
import com.liyi.sutils.utils.io.SPUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import sky.it.com.stock_statistics.R;
import sky.it.com.stock_statistics.database.dao.StockDao;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/5/12 1:56 PM
 * @className: WelcomeActivity
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class WelcomeActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.wel_top_layout)
    RelativeLayout welTopLayout;
    @BindView(R.id.iv_stock_search)
    ImageView ivStockSearch;
    @BindView(R.id.layout_stock_search)
    RelativeLayout layoutStockSearch;
    @BindView(R.id.iv_stock_in)
    ImageView ivStockIn;
    @BindView(R.id.layout_stock_in)
    RelativeLayout layoutStockIn;
    @BindView(R.id.iv_stock_statistic)
    ImageView ivStockStatistic;
    @BindView(R.id.layout_stock_statistic)
    RelativeLayout layoutStockStatistic;
    @BindView(R.id.iv_stock_upload)
    ImageView ivStockUpload;
    @BindView(R.id.layout_stock_upload)
    RelativeLayout layoutStockUpload;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.iv_question)
    ImageView ivQuestion;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        layoutStockSearch.setOnClickListener(this::onClick);
        layoutStockIn.setOnClickListener(this::onClick);
        layoutStockStatistic.setOnClickListener(this::onClick);
        layoutStockUpload.setOnClickListener(this::onClick);
        ivClear.setOnClickListener(this::onClick);
        ivQuestion.setOnClickListener(this::onClick);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_stock_search:
                startActivity(new Intent(this, StockListActivity.class));
                break;
            case R.id.layout_stock_in:
                startActivity(new Intent(this, StockInfoActivity.class));
                break;
            case R.id.layout_stock_statistic:
                startActivity(new Intent(this, StockStatisticActivity.class));
                break;
            case R.id.layout_stock_upload:
                startActivity(new Intent(this, IconUploadActivity.class));
                break;
            case R.id.iv_question:
                startActivity(new Intent(this, QuestionActivity.class));
                break;
            case R.id.iv_clear:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                        //设置title
                        .setTitle("提示")
                        //设置要显示的message
                        .setMessage("操作后无法恢复，确定是否清空数据？")
                        //表示点击dialog其它部分不能取消(除了“取消”，“确定”按钮)
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //清除SharedPreferences的内容
                                SUtils.initialize(getApplication());
                                SPUtil.getInstance().clear();
                                //清除stock表的信息
                                StockDao stockDao = new StockDao(WelcomeActivity.this);
                                stockDao.deleteAll();
                                //清除icon表的信息
//                                IconDao iconDao = new IconDao(WelcomeActivity.this);
//                                iconDao.deleteAll();

                                Toast.makeText(WelcomeActivity.this, "清理成功！", Toast.LENGTH_LONG).show();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                alertDialog.show();
                break;
            default:
                break;
        }
    }
}
