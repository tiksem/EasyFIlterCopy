package com.badmen.EasyFilter;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by semyon.tikhonenko on 23.05.2015.
 */
public class FilterGroupManager {
    private List<GPUImageFilter> filters = new ArrayList<>();

    public FilterGroupManager() {
    }

    public GPUImageFilter addFilter(GPUImageFilter filter) {
        filters.add(filter);
        return getImageFilter();
    }

    private GPUImageFilter getImageFilter() {
        if (filters.isEmpty()) {
            return new GPUImageFilter();
        }

        GPUImageFilterGroup result = new GPUImageFilterGroup();
        for (GPUImageFilter gpuImageFilter : filters) {
            result.addFilter(gpuImageFilter);
        }

        return result;
    }
}
