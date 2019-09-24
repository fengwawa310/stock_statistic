package sky.it.com.stock_statistics.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import sky.it.com.stock_statistics.R;
import sky.it.com.stock_statistics.chart.BarChartManager;
import sky.it.com.stock_statistics.chart.PieChartManagger;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/3/31 3:06 PM
 * @className: ChartActivity
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class ChartActivity extends AppCompatActivity {

    @BindView(R.id.bar_chart)
    BarChart barChart;
    @BindView(R.id.pie_chart)
    PieChart pieChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        ButterKnife.bind(this);

        initBarChart();
//        initPieChart();
    }

    private void initPieChart() {
        //实心
        showhodlePieChart();

        //圆环
//        showRingPieChart();
    }


    private void showRingPieChart() {
//设置每份所占数量
        List<PieEntry> yvals = new ArrayList<>();
        yvals.add(new PieEntry(2.0f, "本科"));
        yvals.add(new PieEntry(3.0f, "硕士"));
        yvals.add(new PieEntry(4.0f, "博士"));
        yvals.add(new PieEntry(5.0f, "大专"));
        yvals.add(new PieEntry(1.0f, "其他"));
// 设置每份的颜色
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#6785f2"));
        colors.add(Color.parseColor("#675cf2"));
        colors.add(Color.parseColor("#496cef"));
        colors.add(Color.parseColor("#aa63fa"));
        colors.add(Color.parseColor("#f5a658"));
        PieChartManagger pieChartManagger = new PieChartManagger(pieChart);
        pieChartManagger.showRingPieChart(yvals, colors);
    }

    private void showhodlePieChart() {
        // 设置每份所占数量
        List<PieEntry> yvals = new ArrayList<>();
        yvals.add(new PieEntry(2.0f, "汉族"));
        yvals.add(new PieEntry(3.0f, "回族"));
        yvals.add(new PieEntry(4.0f, "壮族"));
        yvals.add(new PieEntry(5.0f, "维吾尔族"));
        yvals.add(new PieEntry(6.0f, "土家族"));
        //设置每份的颜色
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#6785f2"));
        colors.add(Color.parseColor("#675cf2"));
        colors.add(Color.parseColor("#496cef"));
        colors.add(Color.parseColor("#aa63fa"));
        colors.add(Color.parseColor("#58a9f5"));

        PieChartManagger pieChartManagger = new PieChartManagger(pieChart);
        pieChartManagger.showSolidPieChart(yvals, colors);
    }


    public void initBarChart() {

//        BarChartManager barChartManager1 = new BarChartManager(barChart);
        BarChartManager barChartManager2 = new BarChartManager(barChart);

        //设置x轴的数据
        ArrayList<String> xValues0 = new ArrayList<>();
        xValues0.add("2019/01");
        xValues0.add("2019/02");
        xValues0.add("2019/03");
        xValues0.add("2019/04");
        xValues0.add("2019/05");

        //设置x轴的数据
        ArrayList<Integer> xValues = new ArrayList<>();
        for (int i = 1; i < 80; i++) {
            xValues.add(i);
        }

        //设置y轴的数据()
        List<List<Integer>> yValues = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            List<Integer> yValue = new ArrayList<>();
            Random random = new Random();
            for (int j = 0; j < 5; j++) {
                yValue.add(random.nextInt(100));
            }
            yValues.add(yValue);
        }

        //线的名字集合
        List<String> names = new ArrayList<>();
        names.add("库房01");
        names.add("库房02");
        names.add("库房03");
        names.add("库房04");

        //创建多条柱状的图表
        barChartManager2.showBarChart(xValues0, yValues, names);
    }


}
