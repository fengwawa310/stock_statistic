package sky.it.com.stock_statistics.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/5/12 7:57 PM
 * @className: CustomTextView
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class CustomTextView extends AppCompatTextView {
    private boolean adjustTopForAscent = true;
    private Paint.FontMetricsInt fontMetricsInt;

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //设置是否remove间距，true为remove
        if (adjustTopForAscent) {
            if (fontMetricsInt == null) {
                fontMetricsInt = new Paint.FontMetricsInt();
                getPaint().getFontMetricsInt(fontMetricsInt);
            }
            canvas.translate(0, fontMetricsInt.top - fontMetricsInt.ascent);
        }
        super.onDraw(canvas);
    }
}
