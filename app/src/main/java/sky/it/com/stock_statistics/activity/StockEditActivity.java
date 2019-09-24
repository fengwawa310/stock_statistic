package sky.it.com.stock_statistics.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.asrwakeup3.core.mini.AutoCheck;
import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer;
import com.baidu.aip.asrwakeup3.core.recog.listener.IRecogListener;
import com.baidu.aip.asrwakeup3.core.recog.listener.MessageStatusRecogListener;
import com.baidu.aip.asrwakeup3.core.util.MyLogger;
import com.baidu.speech.asr.SpeechConstant;
import com.bumptech.glide.Glide;
import com.hmy.popwindow.PopWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import sky.it.com.stock_statistics.R;
import sky.it.com.stock_statistics.adapter.IconListAdapter;
import sky.it.com.stock_statistics.customview.ClearEditText;
import sky.it.com.stock_statistics.database.bean.IconBean;
import sky.it.com.stock_statistics.database.bean.StockBean;
import sky.it.com.stock_statistics.database.dao.IconDao;
import sky.it.com.stock_statistics.database.dao.StockDao;
import sky.it.com.stock_statistics.entity.IconSelectBean;
import sky.it.com.stock_statistics.entity.ImageTypeEnum;
import sky.it.com.stock_statistics.sortlist.CharacterParser;
import sky.it.com.stock_statistics.sortlist.PinyinComparator;
import sky.it.com.stock_statistics.sortlist.SideBar;
import sky.it.com.stock_statistics.utils.NumberUtils;

import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_FINISHED;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_LONG_SPEECH_FINISHED;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_NONE;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_READY;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_RECOGNITION;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_SPEAKING;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_STOPPED;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_WAITING_READY;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/7/31 10:51 AM
 * @className: StockEditActivity
 * @description: 库存编辑
 * @modified By:
 * @modifyDate:
 */
public class StockEditActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.wel_top_layout)
    RelativeLayout welTopLayout;
    @BindView(R.id.et_stock_name)
    ClearEditText etStockName;
    @BindView(R.id.iv_record_name)
    ImageView ivRecordName;
    @BindView(R.id.et_stock_manager)
    ClearEditText etStockManager;
    @BindView(R.id.iv_record_manager)
    ImageView ivRecordManager;
    @BindView(R.id.txt_stock_number)
    TextView txtStockNumber;
    @BindView(R.id.iv_record_number)
    ImageView ivRecordNumber;
    @BindView(R.id.et_stock_product_name)
    ClearEditText etStockProductName;
    @BindView(R.id.iv_record_product_name)
    ImageView ivRecordProductName;
    @BindView(R.id.et_stock_unit)
    ClearEditText etStockUnit;
    @BindView(R.id.iv_record_unit)
    ImageView ivRecordUnit;
    @BindView(R.id.et_stock_amount)
    ClearEditText etStockAmount;
    @BindView(R.id.iv_record_amount)
    ImageView ivRecordAmount;
    @BindView(R.id.et_stock_type)
    ClearEditText etStockType;
    @BindView(R.id.iv_record_type)
    ImageView ivRecordType;
    @BindView(R.id.layout_icons)
    LinearLayout layoutIcons;
    @BindView(R.id.iv_stock_select)
    ImageView ivStockSelect;
    @BindView(R.id.btn_save_info)
    TextView btnSaveInfo;

    /**
     * 该条数据ID
     */
    private int itemId;
    private String stock_manager;
    private String stock_name;
    private int stock_number;
    private String stock_product_name;
    private String stock_unit;
    private int stock_amount;
    private String stock_type;
    private String stock_mark;

    TextView btnConfirm;
    TextView btnCancel;
    ListView lvTest;
    SideBar sideBar;
    TextView dialog;
    TextView title;
    LinearLayout titleLayout;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
    private int lastFirstVisibleItem = -1;


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
    private static final String TAG = "StockEditActivity";

    private List<IconSelectBean> list = new ArrayList<>();
    private IconListAdapter adapter;
    private List<Integer> iconPaths = new ArrayList<>();

    private PopWindow popWindow;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_edit);
        ButterKnife.bind(this);

        //获取该页面的ID信息，便于数据库查询
        Intent intent = getIntent();
        itemId = intent.getIntExtra("itemId", 0);

        //字符解析器
        characterParser = CharacterParser.getInstance();
        //汉语拼音
        pinyinComparator = new PinyinComparator();

        initData();
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
                    //名称
                    etStockProductName.setText(msg.obj.toString());
                } else if (ImageTypeEnum.STOCK_UNIT.getImageType() == imageType) {
                    //单位
                    etStockUnit.setText(msg.obj.toString());
                } else if (ImageTypeEnum.STOCK_AMOUNT.getImageType() == imageType) {
                    //数量要转化成数字
                    String amountStr = msg.obj.toString();
                    try {
                        stock_amount = Integer.parseInt(amountStr);
                    } catch (Exception e) {
                        stock_amount = NumberUtils.numberCN2Arab(amountStr);
                    }
                    Log.e("sky", stock_amount + "");
                    etStockAmount.setText(stock_amount + "");
                } else if (ImageTypeEnum.STOCK_TYPE.getImageType() == imageType) {
                    //特征
                    etStockType.setText(msg.obj.toString().toUpperCase());
                } else if (ImageTypeEnum.STOCK_NAME.getImageType() == imageType) {
                    //仓库
                    etStockName.setText(msg.obj.toString().toUpperCase());
                } else if (ImageTypeEnum.STOCK_MANAGER.getImageType() == imageType) {
                    //嫌疑人姓名
                    etStockManager.setText(msg.obj.toString().toUpperCase());
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
     * 更新录音图片
     */
    private void updateBtnTextByStatus() {
        switch (status) {
            case STATUS_NONE:
                if (ImageTypeEnum.STOCK_PRODUCT_NAME.getImageType() == imageType) {
                    ivRecordProductName.setImageResource(R.mipmap.iv_record_unuse);
                    ivRecordProductName.setClickable(true);
                } else if (ImageTypeEnum.STOCK_UNIT.getImageType() == imageType) {
                    ivRecordUnit.setImageResource(R.mipmap.iv_record_unuse);
                    ivRecordUnit.setClickable(true);
                } else if (ImageTypeEnum.STOCK_AMOUNT.getImageType() == imageType) {
                    ivRecordAmount.setImageResource(R.mipmap.iv_record_unuse);
                    ivRecordAmount.setClickable(true);
                } else if (ImageTypeEnum.STOCK_TYPE.getImageType() == imageType) {
                    ivRecordType.setImageResource(R.mipmap.iv_record_unuse);
                    ivRecordType.setClickable(true);
                } else if (ImageTypeEnum.STOCK_NAME.getImageType() == imageType) {
                    //仓库
                    ivRecordName.setImageResource(R.mipmap.iv_record_unuse);
                    ivRecordName.setClickable(true);
                } else if (ImageTypeEnum.STOCK_MANAGER.getImageType() == imageType) {
                    //嫌疑人姓名
                    ivRecordManager.setImageResource(R.mipmap.iv_record_unuse);
                    ivRecordManager.setClickable(true);
                }
                break;
            case STATUS_WAITING_READY:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                if (ImageTypeEnum.STOCK_PRODUCT_NAME.getImageType() == imageType) {
                    ivRecordProductName.setImageResource(R.mipmap.iv_record);
                    ivRecordProductName.setClickable(true);
                } else if (ImageTypeEnum.STOCK_UNIT.getImageType() == imageType) {
                    ivRecordUnit.setImageResource(R.mipmap.iv_record);
                    ivRecordUnit.setClickable(true);
                } else if (ImageTypeEnum.STOCK_AMOUNT.getImageType() == imageType) {
                    ivRecordAmount.setImageResource(R.mipmap.iv_record);
                    ivRecordAmount.setClickable(true);
                } else if (ImageTypeEnum.STOCK_TYPE.getImageType() == imageType) {
                    ivRecordType.setImageResource(R.mipmap.iv_record);
                    ivRecordType.setClickable(true);
                } else if (ImageTypeEnum.STOCK_NAME.getImageType() == imageType) {
                    //仓库
                    ivRecordName.setImageResource(R.mipmap.iv_record);
                    ivRecordName.setClickable(true);
                } else if (ImageTypeEnum.STOCK_MANAGER.getImageType() == imageType) {
                    //嫌疑人姓名
                    ivRecordManager.setImageResource(R.mipmap.iv_record);
                    ivRecordManager.setClickable(true);
                }
                break;
            case STATUS_LONG_SPEECH_FINISHED:
            case STATUS_STOPPED:
                if (ImageTypeEnum.STOCK_PRODUCT_NAME.getImageType() == imageType) {
                    ivRecordProductName.setImageResource(R.mipmap.iv_record);
                    ivRecordProductName.setClickable(true);
                } else if (ImageTypeEnum.STOCK_UNIT.getImageType() == imageType) {
                    ivRecordUnit.setImageResource(R.mipmap.iv_record);
                    ivRecordUnit.setClickable(true);
                } else if (ImageTypeEnum.STOCK_AMOUNT.getImageType() == imageType) {
                    ivRecordAmount.setImageResource(R.mipmap.iv_record);
                    ivRecordAmount.setClickable(true);
                } else if (ImageTypeEnum.STOCK_TYPE.getImageType() == imageType) {
                    ivRecordType.setImageResource(R.mipmap.iv_record);
                    ivRecordType.setClickable(true);
                } else if (ImageTypeEnum.STOCK_NAME.getImageType() == imageType) {
                    //仓库
                    ivRecordName.setImageResource(R.mipmap.iv_record);
                    ivRecordName.setClickable(true);
                } else if (ImageTypeEnum.STOCK_MANAGER.getImageType() == imageType) {
                    //嫌疑人姓名
                    ivRecordManager.setImageResource(R.mipmap.iv_record);
                    ivRecordManager.setClickable(true);
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
     * 初始化图标列表数据和编辑框数据
     */
    private void initData() {

        //列表图标数据
        IconDao iconDao = new IconDao(this);
        List<IconBean> iconBeans = iconDao.selectAll();

        for (int i = 0; i < iconBeans.size(); i++) {

            String iconName = iconBeans.get(i).getBanner();

            IconSelectBean iconBean = new IconSelectBean();
            iconBean.setIconName(iconName);
            iconBean.setIconPath(iconBeans.get(i).getPath());
            iconBean.setSelected(false);

            String pinyin = characterParser.getSelling(iconName);
            String sortStr = pinyin.substring(0, 1).toUpperCase();
            if (sortStr.matches("[A-Z]")) {
                iconBean.setSortLetters(sortStr.toUpperCase());
            } else {
                iconBean.setSortLetters("#");
            }
            list.add(iconBean);
        }

        //初始化编辑框数据
        StockDao stockDao = new StockDao(this);
        StockBean stockBean = stockDao.queryById(itemId);
        stock_name = stockBean.getName();
        stock_manager = stockBean.getManager();
        stock_number = stockBean.getNumber();
        stock_product_name = stockBean.getProduct_name();
        stock_amount = stockBean.getAmount();
        stock_unit = stockBean.getUnit();
        stock_type = stockBean.getType();
        stock_mark = stockBean.getMark();

    }

    /**
     * 初始化组件
     */
    private void initView() {

        //初始化编辑框
        etStockName.setText(stock_name);
        etStockManager.setText(stock_manager);
        txtStockNumber.setText(stock_number + "");
        etStockProductName.setText(stock_product_name);
        etStockAmount.setText(stock_amount + "");
        etStockUnit.setText(stock_unit);
        etStockType.setText(stock_type);
        String[] split = stock_mark.split(",");
        for (int i = 0; i < split.length; i++) {
            addImg(split[i]);
        }


        ivBack.setOnClickListener(this::onClick);

        ivRecordManager.setOnClickListener(this::onClick);
        ivRecordName.setOnClickListener(this::onClick);
        ivRecordProductName.setOnClickListener(this::onClick);
        ivRecordUnit.setOnClickListener(this::onClick);
        ivRecordAmount.setOnClickListener(this::onClick);
        ivRecordType.setOnClickListener(this::onClick);

        ivStockSelect.setOnClickListener(this::onClick);
        btnSaveInfo.setOnClickListener(this::onClick);
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


    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        status = STATUS_NONE;
        switch (v.getId()) {
            case R.id.iv_record_name:
                imageType = ImageTypeEnum.STOCK_NAME.getImageType();
                switchImage();
                break;
            case R.id.iv_record_manager:
                imageType = ImageTypeEnum.STOCK_MANAGER.getImageType();
                switchImage();
                break;
            case R.id.iv_record_product_name:
                imageType = ImageTypeEnum.STOCK_PRODUCT_NAME.getImageType();
                switchImage();
                break;
            case R.id.iv_record_unit:
                imageType = ImageTypeEnum.STOCK_UNIT.getImageType();
                switchImage();
                break;
            case R.id.iv_record_amount:
                imageType = ImageTypeEnum.STOCK_AMOUNT.getImageType();
                switchImage();
                break;
            case R.id.iv_record_type:
                imageType = ImageTypeEnum.STOCK_TYPE.getImageType();
                switchImage();
                break;
            case R.id.iv_back:
                myRecognizer.release();
                this.finish();
                break;
            case R.id.btn_save_info:
                stock_product_name = etStockProductName.getText().toString().trim();
                stock_unit = etStockUnit.getText().toString().trim();
                stock_type = etStockType.getText().toString().trim();
                stock_manager = etStockManager.getText().toString().trim();
                stock_name = etStockName.getText().toString().trim();

                // TODO: 2019/7/30

                if (TextUtils.isEmpty(stock_manager)) {
                    Toast.makeText(this, "姓名不能为空！", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(stock_name)) {
                    Toast.makeText(this, "仓库不能为空！", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(txtStockNumber.getText().toString())) {
                    Toast.makeText(this, "编号不能为空！", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(stock_product_name)) {
                    Toast.makeText(this, "名称不能为空！", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(stock_unit)) {
                    Toast.makeText(this, "单位不能为空！", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(etStockAmount.getText().toString().trim())) {
                    Toast.makeText(this, "数量不能为空！", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(stock_type)) {
                    Toast.makeText(this, "特征不能为空！", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(stock_mark)) {
                    Toast.makeText(this, "标有图标不能为空！", Toast.LENGTH_LONG).show();
                } else {
                    //存入数据库
                    StockBean stockBean = new StockBean();
                    stockBean.setId(itemId);
                    stockBean.setManager(stock_manager);
                    stockBean.setName(stock_name);
                    //从输入框获取此次的序号
                    stock_number = Integer.parseInt(txtStockNumber.getText().toString());
                    stockBean.setNumber(stock_number);
                    stockBean.setProduct_name(stock_product_name);
                    stockBean.setUnit(stock_unit);
                    stockBean.setAmount(Integer.parseInt(etStockAmount.getText().toString().trim()));
                    stockBean.setType(stock_type);
                    stockBean.setMark(stock_mark);

                    StockDao stockDao = new StockDao(this);
                    stockDao.update(stockBean);

                    Toast toast = Toast.makeText
                            (this, "数据更新成功！", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    this.finish();
                }
                break;
            case R.id.iv_stock_select:
                //清空所有选项
                for (IconSelectBean bean : list) {
                    bean.setSelected(false);
                }
                //清空布局视图
                layoutIcons.removeAllViews();

                // 绑定布局
                View customView = View.inflate(this, R.layout.activity_test, null);
                btnCancel = customView.findViewById(R.id.btn_cancel);
                lvTest = customView.findViewById(R.id.lv_test);
                btnConfirm = customView.findViewById(R.id.btn_confirm);

                btnConfirm.setOnClickListener(this::onClick);
                btnCancel.setOnClickListener(this::onClick);
                adapter = new IconListAdapter(list, StockEditActivity.this);
                //checkbox的点击事件在adapter中设置
                lvTest.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                sideBar = customView.findViewById(R.id.sidebar);
                dialog = customView.findViewById(R.id.floating_header);
                //标题
                titleLayout = customView.findViewById(R.id.title_layout);
                //标题
                title = customView.findViewById(R.id.title_layout_catalog);

                //排列
                Collections.sort(list, pinyinComparator);
                sideBar.setTextView(dialog);
                //右边的sidebar
                sideBar.setOnTouchingLetterChangedListener(s -> {
                    // 该字母首次出现的位置
                    int position = adapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        lvTest.setSelection(position);
                    }
                });

                lvTest.setOnScrollListener(new AbsListView.OnScrollListener() {
                    //上推标题。可用可不用
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        if (list.size() > 1) {
                            int section = getSectionForPosition(firstVisibleItem);
                            int nextSection = getSectionForPosition(firstVisibleItem + 1);
                            int nextSecPosition = getPositionForSection(+nextSection);
                            if (firstVisibleItem != lastFirstVisibleItem) {
                                title.setText(list.get(
                                        getPositionForSection(section)).getSortLetters());
                            }
                            if (nextSecPosition == firstVisibleItem + 1) {
                                View childView = view.getChildAt(0);
                                if (childView != null) {
                                    int titleHeight = titleLayout.getHeight();
                                    int bottom = childView.getBottom();
                                }
                            }
                            lastFirstVisibleItem = firstVisibleItem;
                        }
                    }
                });

                popWindow = new PopWindow.Builder(this)
                        .setStyle(PopWindow.PopWindowStyle.PopUp)
                        .setIsShowCircleBackground(true)
                        .setView(customView)
                        .show();
                break;
            case R.id.btn_confirm:
                StringBuffer sb = new StringBuffer();
                for (IconSelectBean bean : list) {
                    if (bean.isSelected()) {
                        sb.append(bean.getIconPath() + ",");
                    }
                }
                stock_mark = sb.toString().trim();
                if (!TextUtils.isEmpty(stock_mark)) {
                    //去除最后的逗号
                    stock_mark = stock_mark.substring(0, stock_mark.length() - 1);
                }

                popWindow.dismiss();
                for (IconSelectBean bean : list) {
                    //动态添加linearlayout的布局
                    if (bean.isSelected()) {
                        addImg(bean.getIconPath());
                    }
                }
                break;
            case R.id.btn_cancel:
                //清空所有选项
                for (IconSelectBean bean : list) {
                    bean.setSelected(false);
                }

                popWindow.dismiss();
                break;
            default:
                break;
        }
    }


    /**
     * 动态添加imageView视图
     *
     * @param iconPath
     */
    public void addImg(String iconPath) {
        ImageView newImg = new ImageView(this);
        //设置想要的图片，相当于android:src="@drawable/image"
        Glide.with(this)
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
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float density = displayMetrics.density;
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                (int) (28 * density),
                (int) (28 * density));

        //相当于android:layout_marginLeft="8dp"
        params1.leftMargin = 10;

        //addView(View child, LayoutParams params)，往已有的view后面添加，后插入,并设置布局
        layoutIcons.addView(newImg, params1);
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < list.size(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

}
