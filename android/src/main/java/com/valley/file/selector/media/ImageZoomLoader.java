package com.valley.file.selector.media;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.valley.file.selector.capacitorpluginfileselector.R;
import com.valley.file.selector.media.preview.loader.IZoomMediaLoader;
import com.valley.file.selector.media.preview.loader.MySimpleTarget;

public class ImageZoomLoader implements IZoomMediaLoader {
    @Override
    public void displayImage(@NonNull Fragment context, @NonNull String path, ImageView imageView, @NonNull final MySimpleTarget simpleTarget) {
        Glide.with(context)
                .load(path)
                .apply(new RequestOptions().error(R.drawable.ic_default_image))
                .into(imageView);
    }

    @Override
    public void displayGifImage(@NonNull Fragment context, @NonNull String path, ImageView imageView, @NonNull final MySimpleTarget simpleTarget) {
        Glide.with(context)
                .load(path)
                .apply(new RequestOptions().error(R.drawable.ic_default_image))
                .into(imageView);
    }

    @Override
    public void onStop(@NonNull Fragment context) {
        Glide.with(context).onStop();

    }

    @Override
    public void clearMemory(@NonNull Context c) {
        Glide.get(c).clearMemory();
    }
}
