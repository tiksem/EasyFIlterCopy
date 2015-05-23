package com.badmen.EasyFilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.utilsframework.android.adapters.ViewArrayAdapter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by semyon.tikhonenko on 23.05.2015.
 */
public class FilterAdapter extends ViewArrayAdapter<Filter, FilterHolder> {
    public FilterAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getRootLayoutId(int viewType) {
        return R.layout.filter;
    }

    @Override
    protected FilterHolder createViewHolder(View view) {
        FilterHolder holder = new FilterHolder();
        holder.filterName = (TextView) view.findViewById(R.id.name);
        holder.image = (ImageView) view.findViewById(R.id.preview);
        return holder;
    }

    @Override
    protected void reuseView(Filter filter, FilterHolder filterHolder, int position,
                             View view) {
        ImageLoader.getInstance().displayImage(
                "assets://samples/sample_" + position + ".jpg",
                filterHolder.image);
        filterHolder.filterName.setText(filter.name);
    }
}
