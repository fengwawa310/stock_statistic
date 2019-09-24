package sky.it.com.stock_statistics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.aip.asrwakeup3.core.mini.AutoCheck;
import com.baidu.aip.asrwakeup3.core.recog.IStatus;
import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer;
import com.baidu.aip.asrwakeup3.core.recog.listener.IRecogListener;
import com.baidu.aip.asrwakeup3.core.recog.listener.MessageStatusRecogListener;
import com.baidu.aip.asrwakeup3.core.util.MyLogger;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import sky.it.com.stock_statistics.R;

public class MainActivity extends BaseActivity implements View.OnClickListener, IStatus {

    @BindView(R.id.btn_stock_list)
    Button btnStockList;
    @BindView(R.id.txtLog)
    TextView txtLog;
    @BindView(R.id.btn)
    Button btn;

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

    private static final String TAG = "MainActivity";
    @BindView(R.id.btn_refresh_list)
    Button btnRefreshList;
    @BindView(R.id.btn_excel)
    Button btnExcel;
    @BindView(R.id.btn_chart)
    Button btnChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            offlineParams.putAll(fetchSlotDataParam());
            myRecognizer.loadOfflineEngine(offlineParams);
        }
    }

    protected void handleMsg(Message msg) {
        if (txtLog != null && msg.obj != null) {
            txtLog.append(msg.obj.toString() + "\n");
        }

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
                Log.e("sky", msg.obj.toString());
                break;
            default:
                break;
        }

    }

    private void initView() {
        btn.setOnClickListener(this);
        btnStockList.setOnClickListener(this);
        btnRefreshList.setOnClickListener(this);
        btnExcel.setOnClickListener(this);
        btnChart.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        status = STATUS_NONE;
        switch (v.getId()) {
            case R.id.btn:
                switch (status) {
                    // 初始状态
                    case STATUS_NONE:
                        start();
                        status = STATUS_WAITING_READY;
                        updateBtnTextByStatus();
                        txtLog.setText("");
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
                break;
            case R.id.btn_stock_list:
                startActivity(new Intent(MainActivity.this, StockListActivity.class));
                break;
            case R.id.btn_refresh_list:
                startActivity(new Intent(MainActivity.this, RefreshListActivity.class));
                break;
            case R.id.btn_excel:
                startActivity(new Intent(MainActivity.this, ExcelActivity.class));
                break;
            case R.id.btn_chart:
                startActivity(new Intent(MainActivity.this, ChartActivity.class));
                break;
            default:
                break;
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
        params.putAll(fetchSlotDataParam());

        // params 也可以根据文档此处手动修改，参数会以json的格式在界面和logcat日志中打印
        Log.i(TAG, "设置的start输入参数：" + params);
        // 复制此段可以自动检测常规错误
        (new AutoCheck(getApplicationContext(), new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainErrorMessage();
                        txtLog.append(message + "\n");
                        // 可以用下面一行替代，在logcat中查看代码
                        // Log.w("AutoCheckMessage", message);
                    }
                }
            }
        }, enableOffline)).checkAsr(params);

        // 这里打印出params， 填写至您自己的app中，直接调用下面这行代码即可。
        // DEMO集成步骤2.2 开始识别
        myRecognizer.start(params);
    }

    public static Map<String, Object> fetchSlotDataParam() {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            JSONObject json = new JSONObject();
            json.put("name", new JSONArray().put("妈妈").put("老伍"))
                    .put("appname", new JSONArray().put("手百").put("度秘"));
            map.put(SpeechConstant.SLOT_DATA, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
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


    private void updateBtnTextByStatus() {
        switch (status) {
            case STATUS_NONE:
                btn.setText("开始录音");
                btn.setEnabled(true);
                break;
            case STATUS_WAITING_READY:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                btn.setText("停止录音");
                btn.setEnabled(true);
                break;
            case STATUS_LONG_SPEECH_FINISHED:
            case STATUS_STOPPED:
                btn.setText("取消整个识别过程");
                btn.setEnabled(true);
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
}
