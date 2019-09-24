package sky.it.com.stock_statistics.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sky.it.com.stock_statistics.R;
import sky.it.com.stock_statistics.entity.PullToRefreshBean;
import sky.it.com.stock_statistics.entity.SpinnerOption;
import sky.it.com.stock_statistics.utils.ExcelUtil;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/3/31 12:55 PM
 * @className: ExcelActivity
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class ExcelActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_export)
    Button btnExport;
    @BindView(R.id.sp_test)
    Spinner spTest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excel);
        ButterKnife.bind(this);

        btnExport.setOnClickListener(this::onClick);


        initSpinner();

    }

    private void initSpinner() {
        ArrayList<SpinnerOption> towns;

        //准备好下拉框内容
        towns = new ArrayList<SpinnerOption>();
        for (int i = 0; i < 5; i++) {
            SpinnerOption c = new SpinnerOption(i + "", i + " 镇");
            towns.add(c);
        }

        //设置Adapter
        ArrayAdapter<SpinnerOption> townAdapter = new ArrayAdapter<SpinnerOption>
                (this, android.R.layout.simple_spinner_item, towns);
        townAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTest.setAdapter(townAdapter);
        //监听
        spTest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast toast = Toast.makeText(getApplicationContext(), towns.get(position).toString(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_export:
                exportExcel();
                break;
            default:
                break;
        }
    }


    public void exportExcel() {

        String filePath = Environment.getExternalStoragePublicDirectory("") + "/111myprint/";

        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        String excelFileName = "/stock.xls";

        String[] title = {"序号", "库房", "总量", "剩余"};
        String sheetName = "20190331";

        List<PullToRefreshBean> demoBeanList = new ArrayList<>();
        PullToRefreshBean demoBean1 =
                new PullToRefreshBean("1", 10, 1, "库房01");
        PullToRefreshBean demoBean2 =
                new PullToRefreshBean("2", 10, 1, "库房02");
        PullToRefreshBean demoBean3 =
                new PullToRefreshBean("3", 10, 1, "库房03");
        PullToRefreshBean demoBean4 =
                new PullToRefreshBean("4", 10, 1, "库房04");
        demoBeanList.add(demoBean1);
        demoBeanList.add(demoBean2);
        demoBeanList.add(demoBean3);
        demoBeanList.add(demoBean4);
        filePath = filePath + excelFileName;

        ExcelUtil.initExcel(filePath, sheetName, title);

        Log.e("file", filePath);
        ExcelUtil.writeObjListToExcel(demoBeanList, filePath, this);
    }

    private void mCreatFile() {
        try {
            //获取手机本身存储根目录Environment.getExternalStoragePublicDirectory("")
            //sd卡根目录Environment.getExternalStorageDirectory()
            String path = Environment.getExternalStoragePublicDirectory("") + "/111myprint/";
            String fileName = "print.txt";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            //第三个参数：真，后续内容被追加到文件末尾处，反之则替换掉文件全部内容
            FileWriter fw = new FileWriter(path + fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append("在已有的基础上添加字符串");
            bw.write("abc\r\n ");// 往已有的文件上添加字符串
            bw.write("def\r\n ");
            bw.write("hijk ");
            bw.write("hijk ");
            bw.write("hijk ");
            bw.close();
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
