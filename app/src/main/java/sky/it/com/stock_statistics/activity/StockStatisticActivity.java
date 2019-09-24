package sky.it.com.stock_statistics.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.asrwakeup3.core.mini.AutoCheck;
import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer;
import com.baidu.aip.asrwakeup3.core.recog.listener.IRecogListener;
import com.baidu.aip.asrwakeup3.core.recog.listener.MessageStatusRecogListener;
import com.baidu.aip.asrwakeup3.core.util.MyLogger;
import com.baidu.speech.asr.SpeechConstant;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import sky.it.com.stock_statistics.R;
import sky.it.com.stock_statistics.customview.ClearEditText;
import sky.it.com.stock_statistics.database.bean.StockBean;
import sky.it.com.stock_statistics.database.dao.StockDao;
import sky.it.com.stock_statistics.entity.ImageTypeEnum;
import sky.it.com.stock_statistics.entity.SpinnerOption;
import sky.it.com.stock_statistics.utils.ExcelUtil;

import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_FINISHED;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_LONG_SPEECH_FINISHED;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_NONE;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_READY;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_RECOGNITION;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_SPEAKING;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_STOPPED;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_WAITING_READY;
import static sky.it.com.stock_statistics.activity.StockListActivity.makeRootDirectory;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/7/31 5:42 PM
 * @className: StockStatisticActivity
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class StockStatisticActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_excel)
    ImageView ivExcel;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.wel_top_layout)
    RelativeLayout welTopLayout;
    @BindView(R.id.et_stock_name)
    ClearEditText etStockName;
    @BindView(R.id.iv_record_name)
    ImageView ivRecordName;
    @BindView(R.id.et_stock_product_name)
    ClearEditText etStockProductName;
    @BindView(R.id.iv_record_product_name)
    ImageView ivRecordProductName;
    @BindView(R.id.et_stock_type)
    ClearEditText etStockType;
    @BindView(R.id.iv_record_type)
    ImageView ivRecordType;
    @BindView(R.id.layout_filter)
    RelativeLayout layoutFilter;
    @BindView(R.id.txt_sum)
    TextView txtSum;
    @BindView(R.id.layout_sum)
    LinearLayout layoutSum;
    @BindView(R.id.sp_name)
    Spinner spName;
    @BindView(R.id.layout_condition)
    LinearLayout layoutCondition;
    @BindView(R.id.bar_chart)
    BarChart barChart;


    private String stock_name;
    private String stock_product_name;
    private String stock_type;

    /**
     * 柱形图的筛选条件
     */
    private String chart_name_condition;

    private List<StockBean> data = new ArrayList<>();


    /**
     * 当前点击的录音图片类型
     */
    private int imageType;

    /**
     * 识别控制器，使用MyRecognizer控制识别的流程
     */
    private MyRecognizer myRecognizer;
    private Handler handler;
    /**
     * 控制UI按钮的状态
     */
    private int status;
    /**
     * 本Activity中是否需要调用离线命令词功能。根据此参数，判断是否需要调用SDK的ASR_KWS_LOAD_ENGINE事件
     */
    private boolean enableOffline = true;
    private static final String TAG = "StockStatisticActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_statistic);
        ButterKnife.bind(this);

        initView();

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMsg(msg);
            }

        };
        MyLogger.setHandler(handler);
        // 基于DEMO集成第1.1, 1.2, 1.3 步骤 初始化EventManager类并注册自定义输出事件
        // DEMO集成步骤 1.2 新建一个回调类，识别引擎会回调这个类告知重要状态和识别结果
        IRecogListener listener = new MessageStatusRecogListener(handler);
        // DEMO集成步骤 1.1 1.3 初始化：new一个IRecogListener示例 & new 一个 MyRecognizer 示例,并注册输出事件
        myRecognizer = new MyRecognizer(this, listener);
        if (enableOffline) {
            // 基于DEMO集成1.4 加载离线资源步骤(离线时使用)。offlineParams是固定值，复制到您的代码里即可
            Map<String, Object> offlineParams = new HashMap<>();
            offlineParams.put(SpeechConstant.DECODER, 2);
            offlineParams.put(SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH, "assets:///baidu_speech_grammar.bsg");
            myRecognizer.loadOfflineEngine(offlineParams);
        }
    }


    /**
     * 开始录音，点击“开始”按钮后调用。
     * 基于DEMO集成2.1, 2.2 设置识别参数并发送开始事件
     */
    protected void start() {
        // DEMO集成步骤2.1 拼接识别参数： 此处params可以打印出来，直接写到你的代码里去，最终的json一致即可。
        final Map<String, Object> params = new HashMap<>();
        params.put(SpeechConstant.DECODER, 2);
        params.put(SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH, "assets:///baidu_speech_grammar.bsg");
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, true);
        params.put(SpeechConstant.NLU, "enable");
        params.put(SpeechConstant.PID, "1536");
        params.put(SpeechConstant.SOUND_START, R.raw.bdspeech_recognition_start);
        params.put(SpeechConstant.SOUND_END, R.raw.bdspeech_speech_end);
        params.put(SpeechConstant.SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
        params.put(SpeechConstant.SOUND_ERROR, R.raw.bdspeech_recognition_error);
        params.put(SpeechConstant.SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);

        // params 也可以根据文档此处手动修改，参数会以json的格式在界面和logcat日志中打印
        Log.i(TAG, "设置的start输入参数：" + params);
        // 复制此段可以自动检测常规错误
        (new AutoCheck(getApplicationContext(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainErrorMessage();
                        // 可以用下面一行替代，在logcat中查看代码
                        Log.e("sky", "AutoCheckMessage:" + message);
                    }
                }
            }
        }, enableOffline)).checkAsr(params);

        // 这里打印出params， 填写至您自己的app中，直接调用下面这行代码即可。
        // DEMO集成步骤2.2 开始识别
        myRecognizer.start(params);
    }

    protected void handleMsg(Message msg) {
        switch (msg.what) {
            // 处理MessageStatusRecogListener中的状态回调
            case STATUS_FINISHED:
                status = msg.what;
                updateBtnTextByStatus();
                break;
            case STATUS_NONE:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                status = msg.what;
                updateBtnTextByStatus();
                break;
            case 8000:
                if (ImageTypeEnum.STOCK_PRODUCT_NAME.getImageType() == imageType) {
                    etStockProductName.setText(msg.obj.toString());
                } else if (ImageTypeEnum.STOCK_NAME.getImageType() == imageType) {
                    etStockName.setText(msg.obj.toString());
                } else if (ImageTypeEnum.STOCK_TYPE.getImageType() == imageType) {
                    etStockType.setText(msg.obj.toString().toUpperCase());
                }
                break;
            default:
                break;
        }

    }

    /**
     * 开始录音后，手动点击“停止”按钮。
     * SDK会识别不会再识别停止后的录音。
     * 基于DEMO集成4.1 发送停止事件 停止录音
     */
    protected void stop() {
        myRecognizer.stop();
    }

    /**
     * 开始录音后，手动点击“取消”按钮。
     * SDK会取消本次识别，回到原始状态。
     * 基于DEMO集成4.2 发送取消事件 取消本次识别
     */
    protected void cancel() {
        myRecognizer.cancel();
    }


    /**
     * 更改录音图片
     */
    private void updateBtnTextByStatus() {
        switch (status) {
            case STATUS_NONE:
                if (ImageTypeEnum.STOCK_PRODUCT_NAME.getImageType() == imageType) {
                    ivRecordProductName.setImageResource(R.mipmap.iv_record_unuse);
                    ivRecordProductName.setClickable(true);
                } else if (ImageTypeEnum.STOCK_NAME.getImageType() == imageType) {
                    ivRecordName.setImageResource(R.mipmap.iv_record_unuse);
                    ivRecordName.setClickable(true);
                } else if (ImageTypeEnum.STOCK_TYPE.getImageType() == imageType) {
                    ivRecordType.setImageResource(R.mipmap.iv_record_unuse);
                    ivRecordType.setClickable(true);
                }
                break;
            case STATUS_WAITING_READY:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                if (ImageTypeEnum.STOCK_PRODUCT_NAME.getImageType() == imageType) {
                    ivRecordProductName.setImageResource(R.mipmap.iv_record);
                    ivRecordProductName.setClickable(true);
                } else if (ImageTypeEnum.STOCK_NAME.getImageType() == imageType) {
                    ivRecordName.setImageResource(R.mipmap.iv_record);
                    ivRecordName.setClickable(true);
                } else if (ImageTypeEnum.STOCK_TYPE.getImageType() == imageType) {
                    ivRecordType.setImageResource(R.mipmap.iv_record);
                    ivRecordType.setClickable(true);
                }
                break;
            case STATUS_LONG_SPEECH_FINISHED:
            case STATUS_STOPPED:
                if (ImageTypeEnum.STOCK_PRODUCT_NAME.getImageType() == imageType) {
                    ivRecordProductName.setImageResource(R.mipmap.iv_record);
                    ivRecordProductName.setClickable(true);
                } else if (ImageTypeEnum.STOCK_NAME.getImageType() == imageType) {
                    ivRecordName.setImageResource(R.mipmap.iv_record);
                    ivRecordName.setClickable(true);
                } else if (ImageTypeEnum.STOCK_TYPE.getImageType() == imageType) {
                    ivRecordType.setImageResource(R.mipmap.iv_record);
                    ivRecordType.setClickable(true);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 销毁时需要释放识别资源。
     */
    @Override
    protected void onDestroy() {
        // 如果之前调用过myRecognizer.loadOfflineEngine()， release()里会自动调用释放离线资源
        // 基于DEMO5.1 卸载离线资源(离线时使用) release()方法中封装了卸载离线资源的过程
        // 基于DEMO的5.2 退出事件管理器
        myRecognizer.release();
        Log.i(TAG, "onDestory");
        super.onDestroy();
    }


    /**
     * 初始化组件
     */
    private void initView() {

        initSpinner();

        //显示柱状图
        initCharData();

        ivBack.setOnClickListener(this::onClick);
        ivRecordProductName.setOnClickListener(this::onClick);
        ivRecordName.setOnClickListener(this::onClick);
        ivRecordType.setOnClickListener(this::onClick);

        ivSearch.setOnClickListener(this::onClick);
        ivExcel.setOnClickListener(this::onClick);
    }


    /**
     * 显示柱状图
     */
    private void initCharData() {
        //初始化柱状图
        initBarChart();
        //查询数据

        StockDao stockDao = new StockDao(this);
        List<Map<String, String>> maps = stockDao.queryByStockName(chart_name_condition);
        if (maps.size() != 0) {
            showBarChart(maps, chart_name_condition, Color.parseColor("#65C8CA"));
        }
    }


    /**
     * 左侧Y轴
     */
    private YAxis leftAxis;
    /**
     * 右侧Y轴
     */
    private YAxis rightAxis;
    /**
     * X轴
     */
    private XAxis xAxis;
    /**
     * 图例
     */
    private Legend legend;
    /**
     * 限制线
     */
    private LimitLine limitLine;

    /**
     * 初始化柱状图
     */
    public void initBarChart() {
        /***图表设置***/
        //背景颜色
        barChart.setBackgroundColor(Color.WHITE);
        //不显示图表网格
        barChart.setDrawGridBackground(false);
        //背景阴影
        barChart.setDrawBarShadow(false);
        barChart.setHighlightFullBarEnabled(false);
        //不显示边框
        barChart.setDrawBorders(false);

        //不显示右下角描述内容
        Description description = new Description();
        description.setEnabled(false);
        barChart.setDescription(description);

        //设置动画效果
        barChart.animateY(1000, Easing.EasingOption.Linear);
        barChart.animateX(1000, Easing.EasingOption.Linear);

        /***XY轴的设置***/
        //X轴设置显示位置在底部
        xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setGranularity(1f);

        leftAxis = barChart.getAxisLeft();
        rightAxis = barChart.getAxisRight();
        //保证Y轴从0开始，不然会上移一点
        leftAxis.setAxisMinimum(0f);
        rightAxis.setAxisMinimum(0f);

        /***折线图例 标签 设置***/
        legend = barChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(11f);
        //显示位置
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //是否绘制在图表里面
        legend.setDrawInside(false);


        //不显示X轴 Y轴线条
        xAxis.setDrawAxisLine(false);
        //横坐标倾斜
        xAxis.setLabelRotationAngle((float) -15.0);
        leftAxis.setDrawAxisLine(false);
        rightAxis.setDrawAxisLine(false);
        //不显示左侧Y轴
        leftAxis.setEnabled(false);

        barChart.setDrawGridBackground(false);

        //不显示X轴网格线
        xAxis.setDrawGridLines(false);
        //右侧Y轴网格线设置为虚线
        rightAxis.enableGridDashedLine(10f, 10f, 0f);

        //左边横坐标显示不全时使用
//        barChart.setExtraLeftOffset(30f);

    }


    /**
     * 柱状图始化设置 一个BarDataSet 代表一列柱状图
     *
     * @param barDataSet 柱状图
     * @param color      柱状图颜色
     */
    private void initBarDataSet(BarDataSet barDataSet, int color) {
        barDataSet.setColor(color);
        barDataSet.setFormLineWidth(1f);
        barDataSet.setFormSize(15.f);
        //显示柱状图顶部值
        barDataSet.setDrawValues(true);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(1f);
        barDataSet.setValueTextSize(10f);
//        barDataSet.setValueTextColor(color);
    }


    public void showBarChart(List<Map<String, String>> dateValueList, String name, int color) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < dateValueList.size(); i++) {
            /**
             * 此处还可传入Drawable对象 BarEntry(float x, float y, Drawable icon)
             * 即可设置柱状图顶部的 icon展示
             */
            BarEntry barEntry = new BarEntry(i, Float.parseFloat(dateValueList.get(i).get("sum")));
            entries.add(barEntry);
        }
        // 每一个BarDataSet代表一类柱状图
        BarDataSet barDataSet = new BarDataSet(entries, name);
        initBarDataSet(barDataSet, color);


        //X轴自定义值
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dateValueList.get((int) value % dateValueList.size()).get("product_name");
            }
        });

        BarData data = new BarData(barDataSet);
        barChart.setData(data);
    }


    /**
     * 初始化下拉框
     */
    private void initSpinner() {

        StockDao stockDao = new StockDao(this);

        //仓库相关
        List<SpinnerOption> names;

        //准备特征下拉框内容
        names = stockDao.queryForSpinner("name");

        //默认初始化第一个选项
        if (names.size() != 0) {
            chart_name_condition = names.get(0).getText();
        }

        //设置Adapter
        ArrayAdapter<SpinnerOption> nameAdapter = new ArrayAdapter<SpinnerOption>
                (this, R.layout.spinner_item, names);
        nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spName.setAdapter(nameAdapter);
        //监听
        spName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chart_name_condition = names.get(position).toString();
                //更新柱状图
                initCharData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * 切换录音图片状态
     */
    public void switchImage() {
        switch (status) {
            // 初始状态
            case STATUS_NONE:
                start();
                status = STATUS_WAITING_READY;
                updateBtnTextByStatus();
                break;
            // 调用本类的start方法后，即输入START事件后，等待引擎准备完毕。
            case STATUS_WAITING_READY:
                // 引擎准备完毕。
            case STATUS_READY:
                // 用户开始讲话
            case STATUS_SPEAKING:
                // 一句话识别语音结束
            case STATUS_FINISHED:
                // 识别中
            case STATUS_RECOGNITION:
                stop();
                // 引擎识别中
                status = STATUS_STOPPED;
                updateBtnTextByStatus();
                break;
            // 长语音识别结束
            case STATUS_LONG_SPEECH_FINISHED:
                // 引擎识别中
            case STATUS_STOPPED:
                cancel();
                // 识别结束，回到初始状态
                status = STATUS_NONE;
                updateBtnTextByStatus();
                break;
            default:
                break;
        }
    }


    @Override
    public void onClick(View v) {
        status = STATUS_NONE;
        switch (v.getId()) {
            case R.id.iv_record_product_name:
                imageType = ImageTypeEnum.STOCK_PRODUCT_NAME.getImageType();
                switchImage();
                break;
            case R.id.iv_record_name:
                imageType = ImageTypeEnum.STOCK_NAME.getImageType();
                switchImage();
                break;
            case R.id.iv_record_type:
                imageType = ImageTypeEnum.STOCK_TYPE.getImageType();
                switchImage();
                break;
            case R.id.iv_back:
                myRecognizer.release();
                startActivity(new Intent(this, WelcomeActivity.class));
                this.finish();
                break;
            case R.id.iv_search:
                // 模糊查询
                stock_product_name = etStockProductName.getText().toString().trim();
                stock_name = etStockName.getText().toString().trim();
                stock_type = etStockType.getText().toString().trim();

                StockDao stockDao = new StockDao(this);
                data = stockDao.queryDimByCondition(stock_name, stock_product_name, stock_type);

                //计算总数
                int sum = 0;
                for (int i = 0; i < data.size(); i++) {
                    sum += data.get(i).getAmount();
                }
                txtSum.setText(sum + "");

                break;
            case R.id.iv_excel:
                //导出Excel
                exportExcel();
                break;
            default:
                break;
        }
    }


    /**
     * 导出Excel
     */
    public void exportExcel() {
        List<StockBean> keeSpecimenList = data;
        // 生成Excel
        //excel标题
        String[] title = {"仓库", "姓名", "编码", "名称", "数量", "特征", "标有图标"};
        //excel名称
        String fileName = "存库统计数据.xls";
        //sheet名 日期
        Time time = new Time();
        time.setToNow();
        String sheetName = time.format("%Y-%m-%d");

        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook
                (this, sheetName, title, keeSpecimenList, new ArrayList<Map<String, String>>(),null);
        //响应到客户端
        try {
            String filePath =
                    Environment.getExternalStoragePublicDirectory("")
                            + "/stock_statistic/dim/";

            makeRootDirectory(filePath);

            FileOutputStream fosFileOutputStream =
                    new FileOutputStream(filePath + fileName);
            wb.write(fosFileOutputStream);
            fosFileOutputStream.flush();
            fosFileOutputStream.close();

            Toast toast = Toast.makeText
                    (this, "数据导出成功！", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

}
