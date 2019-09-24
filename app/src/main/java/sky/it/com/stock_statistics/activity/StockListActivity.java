package sky.it.com.stock_statistics.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import sky.it.com.stock_statistics.R;
import sky.it.com.stock_statistics.adapter.StockListAdapter;
import sky.it.com.stock_statistics.database.bean.StockBean;
import sky.it.com.stock_statistics.database.dao.StockDao;
import sky.it.com.stock_statistics.entity.SpinnerOption;
import sky.it.com.stock_statistics.utils.ExcelUtil;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/3/30 8:55 PM
 * @className: StockListActivity
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class StockListActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_filter)
    ImageView ivFilter;
    @BindView(R.id.wel_top_layout)
    RelativeLayout welTopLayout;
    @BindView(R.id.sp_name)
    Spinner spName;
    @BindView(R.id.sp_product_name)
    Spinner spProductName;
    @BindView(R.id.sp_type)
    Spinner spType;
    @BindView(R.id.iv_search_white)
    ImageView ivSearchWhite;
    @BindView(R.id.layout_search)
    RelativeLayout layoutSearch;
    @BindView(R.id.iv_excel_white)
    ImageView ivExcelWhite;
    @BindView(R.id.layout_excel)
    RelativeLayout layoutExcel;
    @BindView(R.id.layout_filter)
    RelativeLayout layoutFilter;
    @BindView(R.id.lv_stock_info)
    ListView lvStockInfo;
    @BindView(R.id.txt_sum)
    TextView txtSum;
    private List<StockBean> data = new ArrayList<StockBean>();
    private StockListAdapter stockListAdapter;

    private boolean canSeeFilter = false;
    /**
     * 条件-特征
     */
    private String type;
    /**
     * 条件-仓库
     */
    private String name;
    /**
     * 条件-名称
     */
    private String product_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_list);
        ButterKnife.bind(this);

        initSpinner();

        //数据准备
        data = getData();
        stockListAdapter = new StockListAdapter(this, data);
        lvStockInfo.setAdapter(stockListAdapter);

        lvStockInfo.setOnItemLongClickListener((parent, view, position, id) -> {

            //获取数据行的ID
            int itemId = data.get(position).getId();

            //将本行的数据传递到编辑页面
            Intent intent = new Intent(StockListActivity.this, StockEditActivity.class);
            intent.putExtra("itemId", itemId);
            startActivity(intent);
            return false;
        });


        //等list有数据后初始化视图
        initView();
    }


    /**
     * 恢复视图时刷新数据
     */
    @Override
    protected void onResume() {
        super.onResume();

        //重新获取数据，刷新列表
        data.clear();
        data.addAll(getData());
        stockListAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        //初始化总计
        initSumText();

        ivBack.setOnClickListener(this::onClick);
        ivFilter.setOnClickListener(this::onClick);
        layoutSearch.setOnClickListener(this::onClick);
        layoutExcel.setOnClickListener(this::onClick);
    }


    /**
     * 初始化总计数字
     */
    private void initSumText() {
        int sum = 0;
        for (StockBean stockBean : data) {
            sum += stockBean.getAmount();
        }

        txtSum.setText(sum + "");
    }


    /**
     * 初始化下拉框
     */
    private void initSpinner() {

        StockDao stockDao = new StockDao(this);


        //特征相关
        List<SpinnerOption> types;

        //准备特征下拉框内容
        types = stockDao.queryForSpinner("type");
        //开始添加请选择
        types.add(0, new SpinnerOption("0", "请选择"));

        //设置Adapter
        ArrayAdapter<SpinnerOption> typeAdapter = new ArrayAdapter<SpinnerOption>
                (this, R.layout.spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);
        //监听
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = types.get(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //仓库相关
        List<SpinnerOption> names;

        //准备特征下拉框内容
        names = stockDao.queryForSpinner("name");
        //开始添加请选择
        names.add(0, new SpinnerOption("0", "请选择"));

        //设置Adapter
        ArrayAdapter<SpinnerOption> nameAdapter = new ArrayAdapter<SpinnerOption>
                (this, R.layout.spinner_item, names);
        nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spName.setAdapter(nameAdapter);
        //监听
        spName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                name = names.get(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //名称相关
        List<SpinnerOption> productNames;

        //准备特征下拉框内容
        productNames = stockDao.queryForSpinner("product_name");
        //开始添加请选择
        productNames.add(0, new SpinnerOption("0", "请选择"));

        //设置Adapter
        ArrayAdapter<SpinnerOption> productNamesAdapter = new ArrayAdapter<SpinnerOption>
                (this, R.layout.spinner_item, productNames);
        productNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProductName.setAdapter(productNamesAdapter);
        //监听
        spProductName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                product_name = productNames.get(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    /**
     * 初始化数据
     *
     * @return
     */
    private List<StockBean> getData() {
        List<StockBean> list = new ArrayList<StockBean>();
        StockDao stockDao = new StockDao(this);
        list = stockDao.selectAll();
        return list;
    }


    /**
     * 点击事件
     *
     * @param v
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                startActivity(new Intent(this, WelcomeActivity.class));
                this.finish();
                break;
            case R.id.iv_filter:
                if (!canSeeFilter) {
                    layoutFilter.setVisibility(View.VISIBLE);
                    canSeeFilter = true;
                } else {
                    layoutFilter.setVisibility(View.GONE);
                    canSeeFilter = false;
                }
                break;
            case R.id.layout_search:
                StockDao stockDao = new StockDao(this);

                data.clear();
                data.addAll(stockDao.queryByCondition(name, product_name, type));
                stockListAdapter.notifyDataSetChanged();

                //更新总计
                initSumText();
                break;
            case R.id.layout_excel:
                //excel名称
                final String[] fileNameArr = {""};


                final EditText et;
                //自定义视图
                View customView = View.inflate(this, R.layout.dialog_edit, null);
                et = customView.findViewById(R.id.et_filename);

                AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("文件名称")
                        .setView(customView)
                        .setPositiveButton("确定", null)
                        .setNegativeButton("取消", null)
                        .show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //按下确定键后的事件
                        fileNameArr[0] = et.getText().toString().trim();
                        if (TextUtils.isEmpty(fileNameArr[0])) {
                            //文件名称为空，不允许
                            Toast.makeText(StockListActivity.this,
                                    "文件名称不能为空！", Toast.LENGTH_LONG).show();
                        } else {
                            List<StockBean> keeSpecimenList = data;
                            // 生成Excel
                            //excel标题
                            String[] title = {"仓库", "姓名", "编码", "名称", "数量", "特征", "标有图标"};
                            //excel名称
                            String fileName = fileNameArr[0] + ".xls";
                            //sheet名

//                //本来以仓库号命名，但是现在可能存在多个仓库的情况
//                SUtils.initialize(getApplication());
//                String sheetName = (String) SPUtil.getInstance()
//                        .get("stock_name", "stock");

                            Time time = new Time();
                            time.setToNow();
                            String sheetName = time.format("%Y-%m-%d");

                            //添加汇总数据信息
                            List<Map<String, String>> sumData = addSumData();


                            //导出Excel
                            HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook
                                    (StockListActivity.this, sheetName, title,
                                            keeSpecimenList, sumData, null);
                            //响应到客户端
                            try {
                                String filePath =
                                        Environment.getExternalStoragePublicDirectory("")
                                                + "/stock_statistic/";

                                makeRootDirectory(filePath);

                                FileOutputStream fosFileOutputStream =
                                        new FileOutputStream(filePath + fileName);
                                wb.write(fosFileOutputStream);
                                fosFileOutputStream.flush();
                                fosFileOutputStream.close();

                                Toast toast = Toast.makeText
                                        (StockListActivity.this, "数据导出成功！", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                                alertDialog.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.err.println(e.getMessage());
                            }
                        }
                    }


                });

                break;
            default:
                break;
        }
    }


    /**
     * 生成汇总数据
     *
     * @return
     */
    public List<Map<String, String>> addSumData() {
        int sum = 0;
        for (StockBean stockBean : data) {
            sum += stockBean.getAmount();
        }

        List<Map<String, String>> sums = new ArrayList<>();

        //以仓库为维度
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("name", "总计");
        nameMap.put("sum", sum + "");
        //添加到数据总集合中
        sums.add(nameMap);

        return sums;
    }


    /**
     * 分维度生成汇总数据
     *
     * @return
     */
    public List<Map<String, String>> addSumDataByDimension() {

        List<Map<String, String>> sums = new ArrayList<>();

        StockDao stockDao = new StockDao(this);
        //以仓库为维度
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("name", "按仓库统计");
        nameMap.put("sum", "");
        //添加到数据总集合中
        sums.add(nameMap);

        //获取仓库的总累计
        List<SpinnerOption> names = stockDao.queryForSpinner("name");
        //分别计算仓库的统计数量
        for (int i = 0; i < names.size(); i++) {
            //仓库名称
            String name = names.get(i).getText();
            //计算该仓库下的数量总计
            List<StockBean> beans = stockDao.queryByCondition(name, "请选择", "请选择");
            int nameSum = 0;
            for (int i1 = 0; i1 < beans.size(); i1++) {
                nameSum += beans.get(i1).getAmount();
            }

            Map<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("sum", nameSum + "");

            //添加到数据总集合中
            sums.add(map);
        }

        //以特征为维度
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("name", "按特征统计");
        typeMap.put("sum", "");
        //添加到数据总集合中
        sums.add(typeMap);

        //获取特征的总累计
        List<SpinnerOption> types = stockDao.queryForSpinner("type");
        //分别计算仓库的统计数量
        for (int i = 0; i < types.size(); i++) {
            //特征名称
            String type = types.get(i).getText();
            //计算该特征下的数量总计
            List<StockBean> beans = stockDao.queryByCondition("请选择", "请选择", type);
            int typeSum = 0;
            for (int i1 = 0; i1 < beans.size(); i1++) {
                typeSum += beans.get(i1).getAmount();
            }

            Map<String, String> map = new HashMap<>();
            map.put("name", type);
            map.put("sum", typeSum + "");

            //添加到数据总集合中
            sums.add(map);
        }

        //以名称为维度
        Map<String, String> productNameMap = new HashMap<>();
        productNameMap.put("name", "按名称统计");
        productNameMap.put("sum", "");
        //添加到数据总集合中
        sums.add(typeMap);
        //获取名称的总累计
        List<SpinnerOption> productNames = stockDao.queryForSpinner("product_name");
        //分别计算名称的统计数量
        for (int i = 0; i < productNames.size(); i++) {
            //特征名称
            String productName = productNames.get(i).getText();
            //计算该特征下的数量总计
            List<StockBean> beans = stockDao.queryByCondition("请选择", productName, "请选择");
            int productNameSum = 0;
            for (int i1 = 0; i1 < beans.size(); i1++) {
                productNameSum += beans.get(i1).getAmount();
            }

            Map<String, String> map = new HashMap<>();
            map.put("name", productName);
            map.put("sum", productNameSum + "");

            //添加到数据总集合中
            sums.add(map);
        }


        return sums;
    }

    /**
     * 生成文件夹
     *
     * @param filePath
     */
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

}
