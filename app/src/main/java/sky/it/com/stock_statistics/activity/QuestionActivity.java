package sky.it.com.stock_statistics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import sky.it.com.stock_statistics.R;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/7/28 11:38 PM
 * @className: QuestionActivity
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        ivBack.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                startActivity(new Intent(QuestionActivity.this, WelcomeActivity.class));
                this.finish();
                break;
            default:
                break;
        }
    }
}
