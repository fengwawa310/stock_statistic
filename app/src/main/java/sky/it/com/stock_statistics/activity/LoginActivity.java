package sky.it.com.stock_statistics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import sky.it.com.stock_statistics.R;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/5/12 4:04 PM
 * @className: LoginActivity
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.iv_stock_login)
    ImageView ivStockLogin;
    @BindView(R.id.layout_login)
    RelativeLayout layoutLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        layoutLogin.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_login:
                startActivity(new Intent(this,WelcomeActivity.class));
        }
    }
}
