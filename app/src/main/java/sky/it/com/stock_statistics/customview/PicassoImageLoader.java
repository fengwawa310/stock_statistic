package sky.it.com.stock_statistics.customview;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.lzy.imagepicker.loader.ImageLoader;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import sky.it.com.stock_statistics.R;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/7/28 11:24 AM
 * @className: PicassoImageLoader
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class PicassoImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        Picasso.with(activity)
                .load(Uri.fromFile(new File(path)))
                .placeholder(R.mipmap.icon)
                .error(R.mipmap.icon)
                .resize(width, height)
                .centerInside()
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(imageView);
    }

    @Override
    public void displayImagePreview(Activity activity, String path,
                                    ImageView imageView, int width, int height) {
        Picasso.with(activity)
                .load(Uri.fromFile(new File(path)))
                .placeholder(R.mipmap.icon)
                .error(R.mipmap.icon)
                .resize(width, height)
                .centerInside()
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(imageView);

    }

    @Override
    public void clearMemoryCache() {
        //这里是清除缓存的方法,根据需要自己实现
    }
}