package com.badmen.EasyFilter;

import com.utils.framework.CollectionUtils;
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
    private int undoCount = 0;
    private GPUImageFilter lastFilter;
    private boolean applyFilterRequested = false;

    public FilterGroupManager() {
    }

    public GPUImageFilter addFilter(GPUImageFilter filter) {
        if (undoCount != 0) {
            filters = new ArrayList<>(filters.subList(0, filters.size() - undoCount));
            undoCount = 0;
        }

        filters.add(filter);
        return getImageFilter();
    }

    public GPUImageFilter replaceTopFilter(GPUImageFilter filter) {
        if (undoCount != 0 || filters.isEmpty()) {
            return addFilter(filter);
        }

        CollectionUtils.setLast(filters, filter);
        return getImageFilter();
    }

    public GPUImageFilter addOrReplaceFilter(GPUImageFilter filter) {
        if (applyFilterRequested) {
            applyFilterRequested = false;
            return addFilter(filter);
        } else {
            return replaceTopFilter(filter);
        }
    }

    public GPUImageFilter undo() {
        if (undoCount == filters.size()) {
            return new GPUImageFilter();
        }

        undoCount++;
        return getImageFilter();
    }

    public GPUImageFilter redo() {
        if (undoCount == 0) {
            return getLastFilter();
        }

        undoCount--;
        return getImageFilter();
    }

    private GPUImageFilter getLastFilter() {
        if (lastFilter == null) {
            lastFilter = new GPUImageFilter();
        }

        return lastFilter;
    }

    public GPUImageFilter getTopFilter() {
        if (filters.isEmpty()) {
            return new GPUImageFilter();
        }

        return CollectionUtils.getLast(filters);
    }

    private GPUImageFilter getImageFilter() {
        int index = 0;
        int last = filters.size() - undoCount - 1;
        if (last < 0) {
            return new GPUImageFilter();
        }

        GPUImageFilterGroup result = new GPUImageFilterGroup();
        for (GPUImageFilter gpuImageFilter : filters) {
            if (index > last) {
                break;
            }

            result.addFilter(gpuImageFilter);
            index++;
        }

        lastFilter = result;
        return result;
    }

    public void applyFilter() {
        applyFilterRequested = true;
    }
}
