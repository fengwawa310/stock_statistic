package sky.it.com.stock_statistics.customview;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lzy.imagepicker.loader.ImageLoader;

import java.io.File;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/7/28 9:41 AM
 * @className: GlideImageLoader
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class GlideImageLoader implements ImageLoader {

    private Context context;

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView,
                             int width, int height) {
        context = activity;
        Glide.with(activity)
                .load(Uri.fromFile(new File(path)))
                .into(imageView);

    }

    @Override
    public void displayImagePreview(Activity activity, String path, ImageView imageView,
                                    int width, int height) {

    }

    @Override
    public void clearMemoryCache() {
        Glide.get(context).clearDiskCache();
    }
}
