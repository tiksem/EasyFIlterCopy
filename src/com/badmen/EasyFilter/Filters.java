package com.badmen.EasyFilter;

import jp.co.cyberagent.android.gpuimage.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by semyon.tikhonenko on 23.05.2015.
 */
public class Filters {
    public static final List<Filter> filters =
            Arrays.asList(new Filter("Greyscale", new GPUImageGrayscaleFilter()),
                    new Filter("Box Blur", new GPUImageBoxBlurFilter(1.0f)),
                    new Filter("Brightness", new GPUImageBrightnessFilter(0.7f)),
                    new Filter("Chromium", new GPUImageChromaKeyBlendFilter()),
                    new Filter("Distortion", new GPUImageBulgeDistortionFilter())
                    );
}
