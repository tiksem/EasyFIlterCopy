package com.badmen.EasyFilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;
import com.utilsframework.android.adapters.ViewArrayAdapter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by semyon.tikhonenko on 23.05.2015.
 */
public class FilterAdapter extends ViewArrayAdapter<Filter, FilterHolder> {
    private Bitmap bitmap;

    public FilterAdapter(Context context, Bitmap bitmap) {
        super(context);
        this.bitmap = bitmap;
    }

    @Override
    protected int getRootLayoutId(int viewType) {
        return R.layout.filter;
    }

    @Override
    protected FilterHolder createViewHolder(View view) {
        FilterHolder holder = new FilterHolder();
        holder.filterName = (TextView) view.findViewById(R.id.name);
        holder.image = (GPUImageView) view.findViewById(R.id.preview);
        return holder;
    }

    @Override
    protected void reuseView(Filter filter, FilterHolder filterHolder, int position,
                             View view) {
        filterHolder.image.setImage(bitmap);
        filterHolder.image.setFilter(filter.filter);
        filterHolder.filterName.setText(filter.name);
    }
}
