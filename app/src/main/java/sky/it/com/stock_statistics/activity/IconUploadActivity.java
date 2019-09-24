package sky.it.com.stock_statistics.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import sky.it.com.stock_statistics.R;
import sky.it.com.stock_statistics.customview.ClearEditText;
import sky.it.com.stock_statistics.customview.PicassoImageLoader;
import sky.it.com.stock_statistics.database.bean.IconBean;
import sky.it.com.stock_statistics.database.dao.IconDao;
import sky.it.com.stock_statistics.entity.ImageTypeEnum;
import sky.it.com.stock_statistics.utils.FileUtil;

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
 * @creatDate: 2019/7/28 5:41 PM
 * @className: IconUploadActivity
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class IconUploadActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_stock_icon)
    ClearEditText etStockIcon;
    @BindView(R.id.iv_record_icon)
    ImageView ivRecordIcon;
    @BindView(R.id.iv_pic)
    ImageView ivPic;
    @BindView(R.id.layout_pic)
    RelativeLayout layoutPic;
    @BindView(R.id.btn_save)
    TextView btnSave;

    private static int IMAGE_PICKER = 999;

    /**
     * 当前点击的录音图片类型
     */
    private int imageType;

    private String icon_name, icon_path;


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
    private static final String TAG = "IconUploadActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_upload);
        ButterKnife.bind(this);


        initImagePicker();
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
            offlineParams.put(SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH,
                    "assets:///baidu_speech_grammar.bsg");
            myRecognizer.loadOfflineEngine(offlineParams);
        }

    }

    /**
     * 初始化图片选择器
     */
    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        //设置图片加载器
        imagePicker.setImageLoader(new PicassoImageLoader());
        //显示拍照按钮
        imagePicker.setShowCamera(true);
        //允许裁剪（单选才有效）
        imagePicker.setCrop(true);
        //是否按矩形区域保存
        imagePicker.setSaveRectangle(true);
        //选中数量限制
        imagePicker.setSelectLimit(1);
        //裁剪框的形状
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);
        //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusWidth(800);
        //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);
        //保存文件的宽度。单位像素
        imagePicker.setOutPutX(100);
        //保存文件的高度。单位像素
        imagePicker.setOutPutY(100);
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
                if (ImageTypeEnum.STOCK_ICON.getImageType() == imageType) {
                    etStockIcon.setText(msg.obj.toString());
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
                if (ImageTypeEnum.STOCK_ICON.getImageType() == imageType) {
                    ivRecordIcon.setImageResource(R.mipmap.iv_record_unuse);
                    ivRecordIcon.setClickable(true);
                }
                break;
            case STATUS_WAITING_READY:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                if (ImageTypeEnum.STOCK_ICON.getImageType() == imageType) {
                    ivRecordIcon.setImageResource(R.mipmap.iv_record);
                    ivRecordIcon.setClickable(true);
                }
                break;
            case STATUS_LONG_SPEECH_FINISHED:
            case STATUS_STOPPED:
                if (ImageTypeEnum.STOCK_ICON.getImageType() == imageType) {
                    ivRecordIcon.setImageResource(R.mipmap.iv_record);
                    ivRecordIcon.setClickable(true);
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
        ivBack.setOnClickListener(this::onClick);
        btnSave.setOnClickListener(this::onClick);
        ivRecordIcon.setOnClickListener(this::onClick);
        layoutPic.setOnClickListener(this::onClick);
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
            case R.id.iv_record_icon:
                imageType = ImageTypeEnum.STOCK_ICON.getImageType();
                switchImage();
                break;
            case R.id.iv_back:
                myRecognizer.release();
                startActivity(new Intent(this, WelcomeActivity.class));
                this.finish();
                break;
            case R.id.btn_save:
                icon_name = etStockIcon.getText().toString().trim();
                if (TextUtils.isEmpty(icon_name)) {
                    Toast.makeText(this, "品牌不能为空！", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(icon_path)) {
                    Toast.makeText(this, "图标不能为空！", Toast.LENGTH_LONG).show();
                } else {
                    myRecognizer.release();

                    System.out.println("文件路径:" + icon_path);

                    String fileName = icon_path.substring(icon_path.lastIndexOf("/") + 1,
                            icon_path.length());
                    System.out.println("文件名称:" + fileName);

                    //将图片进行copy到指定的文件位置
                    String saveIconPath =
                            Environment.getExternalStoragePublicDirectory("")
                                    + "/stock_statistic/icons/";

                    //创建文件
                    FileUtil.newFile(saveIconPath, fileName);

                    FileUtil.copyFile(icon_path, saveIconPath + fileName);

                    //构建icon保存实体e
                    IconBean iconBean = new IconBean();
                    iconBean.setBanner(icon_name);
                    iconBean.setPath(saveIconPath + fileName);

                    //保存icon信息
                    IconDao iconDao = new IconDao(this);
                    iconDao.insert(iconBean);

                    Toast.makeText
                            (this, "图标保存成功！", Toast.LENGTH_LONG).show();
                    Handler handler = new Handler();
                    //2秒后执行Runnable中的run方法
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(IconUploadActivity.this,
                                    WelcomeActivity.class));
                            IconUploadActivity.this.finish();
                        }
                    }, 2000);
                }
                break;
            case R.id.layout_pic:
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, IMAGE_PICKER);
                break;
            default:
                break;
        }
    }


    /**
     * 图片选择回调方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                ArrayList<ImageItem> images =
                        (ArrayList<ImageItem>) data.getSerializableExtra
                                (ImagePicker.EXTRA_RESULT_ITEMS);
                //获取图片路径
                icon_path = images.get(0).path;

                //回显图片
                Glide.with(this)
                        //获取图片的第一张
                        .load(Uri.fromFile(new File(icon_path)))
                        .into(ivPic);
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
