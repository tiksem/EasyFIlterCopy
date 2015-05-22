package com.badmen.EasyFilter;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by semyon.tikhonenko on 23.05.2015.
 */
public class Filter {
    public String name;
    public GPUImageFilter filter;

    public Filter(String name, GPUImageFilter filter) {
        this.name = name;
        this.filter = filter;
    }
}
