/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.badmen.EasyFilter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import com.utils.framework.strings.Strings;
import com.utilsframework.android.file.FileUtils;
import jp.co.cyberagent.android.gpuimage.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GPUImageFilterTools {
    public static List<Filter> getFilters(final Context context) {
        final FilterList filters = new FilterList(context);
        filters.addFilter("Contrast", new GPUImageContrastFilter(2.0f));
        filters.addFilter("Invert", new GPUImageColorInvertFilter());
        filters.addFilter("Pixelation", new GPUImagePixelationFilter());
        filters.addFilter("Hue", new GPUImageHueFilter(90.0f));
        filters.addFilter("Gamma", new GPUImageGammaFilter(2.0f));
        filters.addFilter("Brightness", new GPUImageBrightnessFilter(0.4f));
        filters.addFilter("Sepia", new GPUImageSepiaFilter());
        filters.addFilter("Grayscale", new GPUImageGrayscaleFilter());
        GPUImageSharpenFilter sharpness = new GPUImageSharpenFilter();
        sharpness.setSharpness(2.0f);
        filters.addFilter("Sharpness", sharpness);
        filters.addFilter("Sobel Edge Detection", new GPUImageSobelEdgeDetection());
        GPUImage3x3ConvolutionFilter convolution = new GPUImage3x3ConvolutionFilter();
        convolution.setConvolutionKernel(new float[]{
                -1.0f, 0.0f, 1.0f,
                -2.0f, 0.0f, 2.0f,
                -1.0f, 0.0f, 1.0f
        });

        try {
            AssetManager assets = context.getAssets();
            for (String filterName : assets.list("instagram")) {
                GPUImageLookupFilter lookupFilter = new GPUImageLookupFilter();
                Bitmap bitmap = BitmapFactory.decodeStream(assets.open("instagram/" + filterName));
                lookupFilter.setBitmap(bitmap);
                filterName = getFilterNameFromFileName(filterName);
                filters.addFilter(filterName, lookupFilter);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        filters.addFilter("3x3 Convolution", convolution);
        filters.addFilter("Emboss", new GPUImageEmbossFilter());
        filters.addFilter("Posterize", new GPUImagePosterizeFilter());
        List<GPUImageFilter> groupFilters = new LinkedList<GPUImageFilter>();
        groupFilters.add(new GPUImageContrastFilter());
        groupFilters.add(new GPUImageDirectionalSobelEdgeDetectionFilter());
        groupFilters.add(new GPUImageGrayscaleFilter());
        filters.addFilter("Grouped filters", new GPUImageFilterGroup(groupFilters));
        filters.addFilter("Saturation", new GPUImageSaturationFilter(1.0f));
        filters.addFilter("Exposure", new GPUImageExposureFilter(0.0f));
        filters.addFilter("Highlight Shadow", new GPUImageHighlightShadowFilter(0.0f, 1.0f));
        filters.addFilter("Monochrome", new GPUImageMonochromeFilter(1.0f, new float[]{0.6f, 0.45f, 0.3f, 1.0f}));
        filters.addFilter("Opacity", new GPUImageOpacityFilter(1.0f));
        filters.addFilter("RGB", new GPUImageRGBFilter(1.0f, 1.0f, 1.0f));
        filters.addFilter("White Balance", new GPUImageWhiteBalanceFilter(5000.0f, 0.0f));
        PointF centerPoint = new PointF();
        centerPoint.x = 0.5f;
        centerPoint.y = 0.5f;
        filters.addFilter("Vignette", new GPUImageVignetteFilter(centerPoint,
                new float[]{0.0f, 0.0f, 0.0f}, 0.3f, 0.75f));
        GPUImageToneCurveFilter toneCurveFilter = new GPUImageToneCurveFilter();
        toneCurveFilter.setFromCurveFileInputStream(
                context.getResources().openRawResource(R.raw.tone_cuver_sample));
        filters.addFilter("ToneCurve", toneCurveFilter);

        filters.addFilter("Blend (Difference)", createBlendFilter(context, GPUImageDifferenceBlendFilter.class));
        filters.addFilter("Blend (Source Over)", createBlendFilter(context, GPUImageSourceOverBlendFilter.class));

        GPUImageLookupFilter amatorka = new GPUImageLookupFilter();
        amatorka.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.amatorka));
        filters.addFilter("Lookup (Amatorka)", amatorka);
        filters.addFilter("Gaussian Blur", new GPUImageGaussianBlurFilter());
        filters.addFilter("Crosshatch", new GPUImageCrosshatchFilter());

        filters.addFilter("Box Blur", new GPUImageBoxBlurFilter());
        filters.addFilter("CGA Color Space", new GPUImageCGAColorspaceFilter());
        filters.addFilter("Dilation", new GPUImageDilationFilter());
        filters.addFilter("Kuwahara", new GPUImageKuwaharaFilter());
        filters.addFilter("RGB Dilation", new GPUImageRGBDilationFilter());
        filters.addFilter("Sketch", new GPUImageSketchFilter());
        filters.addFilter("Toon", new GPUImageToonFilter());
        filters.addFilter("Smooth Toon", new GPUImageSmoothToonFilter());

        filters.addFilter("Bulge Distortion", new GPUImageBulgeDistortionFilter());
        filters.addFilter("Glass Sphere", new GPUImageGlassSphereFilter());
        filters.addFilter("Haze", new GPUImageHazeFilter());
        filters.addFilter("Laplacian", new GPUImageLaplacianFilter());
        filters.addFilter("Non Maximum Suppression",
                new GPUImageNonMaximumSuppressionFilter());
        filters.addFilter("Sphere Refraction", new GPUImageSphereRefractionFilter());
        filters.addFilter("Swirl", new GPUImageSwirlFilter());
        filters.addFilter("Weak Pixel Inclusion", new GPUImageWeakPixelInclusionFilter());
        filters.addFilter("False Color", new GPUImageFalseColorFilter());

        filters.addFilter("Color Balance", new GPUImageColorBalanceFilter());

        GPUImageLevelsFilter levelsFilter = new GPUImageLevelsFilter();
        levelsFilter.setMin(0.0f, 3.0f, 1.0f);
        filters.addFilter("Levels Min (Mid Adjust)", levelsFilter);

        return filters.filters;
    }

    private static String getFilterNameFromFileName(String filterName) {
        return Strings.splitReplaceAndJoin("_", " ", FileUtils.removeExtension(filterName),
                new Strings.Replacer() {
                    @Override
                    public String getReplacement(String source) {
                        return Strings.capitalize(source);
                    }
                });
    }

    private static GPUImageFilter createBlendFilter(Context context, Class<? extends GPUImageTwoInputFilter> filterClass) {
        try {
            GPUImageTwoInputFilter filter = filterClass.newInstance();
            filter.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.clouds));
            return filter;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface OnGpuImageFilterChosenListener {
        void onGpuImageFilterChosenListener(GPUImageFilter filter);
    }

    private static class FilterList {
        public List<Filter> filters = new ArrayList<>();
        private Context context;

        public FilterList(Context context) {
            this.context = context;
        }

        public void addFilter(final String name, GPUImageFilter filter) {
            filters.add(new Filter(name, filter));
        }
    }

    public static class FilterAdjuster {
        private final Adjuster<? extends GPUImageFilter> adjuster;

        public FilterAdjuster(final GPUImageFilter filter) {
            if (filter instanceof GPUImageSharpenFilter) {
                adjuster = new SharpnessAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSepiaFilter) {
                adjuster = new SepiaAdjuster().filter(filter);
            } else if (filter instanceof GPUImageContrastFilter) {
                adjuster = new ContrastAdjuster().filter(filter);
            } else if (filter instanceof GPUImageGammaFilter) {
                adjuster = new GammaAdjuster().filter(filter);
            } else if (filter instanceof GPUImageBrightnessFilter) {
                adjuster = new BrightnessAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSobelEdgeDetection) {
                adjuster = new SobelAdjuster().filter(filter);
            } else if (filter instanceof GPUImageEmbossFilter) {
                adjuster = new EmbossAdjuster().filter(filter);
            } else if (filter instanceof GPUImage3x3TextureSamplingFilter) {
                adjuster = new GPU3x3TextureAdjuster().filter(filter);
            } else if (filter instanceof GPUImageHueFilter) {
                adjuster = new HueAdjuster().filter(filter);
            } else if (filter instanceof GPUImagePosterizeFilter) {
                adjuster = new PosterizeAdjuster().filter(filter);
            } else if (filter instanceof GPUImagePixelationFilter) {
                adjuster = new PixelationAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSaturationFilter) {
                adjuster = new SaturationAdjuster().filter(filter);
            } else if (filter instanceof GPUImageExposureFilter) {
                adjuster = new ExposureAdjuster().filter(filter);
            } else if (filter instanceof GPUImageHighlightShadowFilter) {
                adjuster = new HighlightShadowAdjuster().filter(filter);
            } else if (filter instanceof GPUImageMonochromeFilter) {
                adjuster = new MonochromeAdjuster().filter(filter);
            } else if (filter instanceof GPUImageOpacityFilter) {
                adjuster = new OpacityAdjuster().filter(filter);
            } else if (filter instanceof GPUImageRGBFilter) {
                adjuster = new RGBAdjuster().filter(filter);
            } else if (filter instanceof GPUImageWhiteBalanceFilter) {
                adjuster = new WhiteBalanceAdjuster().filter(filter);
            } else if (filter instanceof GPUImageVignetteFilter) {
                adjuster = new VignetteAdjuster().filter(filter);
            } else if (filter instanceof GPUImageDissolveBlendFilter) {
                adjuster = new DissolveBlendAdjuster().filter(filter);
            } else if (filter instanceof GPUImageGaussianBlurFilter) {
                adjuster = new GaussianBlurAdjuster().filter(filter);
            } else if (filter instanceof GPUImageCrosshatchFilter) {
                adjuster = new CrosshatchBlurAdjuster().filter(filter);
            } else if (filter instanceof GPUImageBulgeDistortionFilter) {
                adjuster = new BulgeDistortionAdjuster().filter(filter);
            } else if (filter instanceof GPUImageGlassSphereFilter) {
                adjuster = new GlassSphereAdjuster().filter(filter);
            } else if (filter instanceof GPUImageHazeFilter) {
                adjuster = new HazeAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSphereRefractionFilter) {
                adjuster = new SphereRefractionAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSwirlFilter) {
                adjuster = new SwirlAdjuster().filter(filter);
            } else if (filter instanceof GPUImageColorBalanceFilter) {
                adjuster = new ColorBalanceAdjuster().filter(filter);
            } else if (filter instanceof GPUImageLevelsFilter) {
                adjuster = new LevelsMinMidAdjuster().filter(filter);
            } else if (filter instanceof GPUImageLookupFilter) {
                adjuster = new LookupFilterAdjuster().filter(filter);
            } else {
                adjuster = null;
            }
        }

        public boolean canAdjust() {
            return adjuster != null;
        }

        public void adjust(final int percentage) {
            if (adjuster != null) {
                adjuster.adjust(percentage);
            }
        }

        public int getProgress() {
            return adjuster.getProgress();
        }

        private abstract class Adjuster<T extends GPUImageFilter> {
            private T filter;

            @SuppressWarnings("unchecked")
            public Adjuster<T> filter(final GPUImageFilter filter) {
                this.filter = (T) filter;
                return this;
            }

            public T getFilter() {
                return filter;
            }

            public abstract void adjust(int percentage);

            protected float range(final int percentage, final float start, final float end) {
                return (end - start) * percentage / 100.0f + start;
            }

            protected int range(final int percentage, final int start, final int end) {
                return (end - start) * percentage / 100 + start;
            }

            protected int progress(int current, final int start, final int end) {
                int diff = end - start;
                double k = 100.0 / diff;
                return (int) Math.round((current - start) * k);
            }

            protected int progress(float current, final float start, final float end) {
                float diff = end - start;
                float k = 100.0f / diff;
                return Math.round((current - start) * k);
            }

            public abstract int getProgress();
        }

        private class SharpnessAdjuster extends Adjuster<GPUImageSharpenFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setSharpness(range(percentage, -4.0f, 4.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getSharpness(), -4.0f, 4.0f);
            }
        }

        private class PixelationAdjuster extends Adjuster<GPUImagePixelationFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setPixel(range(percentage, 1.0f, 100.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getPixel(), 1.0f, 100.0f);
            }
        }

        private class HueAdjuster extends Adjuster<GPUImageHueFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setHue(range(percentage, 0.0f, 360.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getmHue(), 0.0f, 360.0f);
            }
        }

        private class ContrastAdjuster extends Adjuster<GPUImageContrastFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setContrast(range(percentage, 0.0f, 2.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getContrast(), 0.0f, 2.0f);
            }
        }

        private class GammaAdjuster extends Adjuster<GPUImageGammaFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setGamma(range(percentage, 0.0f, 3.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getGamma(), 0.0f, 3.0f);
            }
        }

        private class BrightnessAdjuster extends Adjuster<GPUImageBrightnessFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setBrightness(range(percentage, -1.0f, 1.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getBrightness(), -1.0f, 1.0f);
            }
        }

        private class SepiaAdjuster extends Adjuster<GPUImageSepiaFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setIntensity(range(percentage, 0.0f, 2.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getIntensity(), 0.0f, 2.0f);
            }
        }

        private class SobelAdjuster extends Adjuster<GPUImageSobelEdgeDetection> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setLineSize(range(percentage, 0.0f, 5.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getLineSize(), 0.0f, 5.0f);
            }
        }

        private class EmbossAdjuster extends Adjuster<GPUImageEmbossFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setIntensity(range(percentage, 0.0f, 4.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getIntensity(), 0.0f, 4.0f);
            }
        }

        private class PosterizeAdjuster extends Adjuster<GPUImagePosterizeFilter> {
            @Override
            public void adjust(final int percentage) {
                // In theorie to 256, but only first 50 are interesting
                getFilter().setColorLevels(range(percentage, 1, 50));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getColorLevels(), 1, 50);
            }
        }

        private class GPU3x3TextureAdjuster extends Adjuster<GPUImage3x3TextureSamplingFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setLineSize(range(percentage, 0.0f, 5.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getLineSize(), 0.0f, 5.0f);
            }
        }

        private class SaturationAdjuster extends Adjuster<GPUImageSaturationFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setSaturation(range(percentage, 0.0f, 2.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getSaturation(), 0.0f, 2.0f);
            }
        }

        private class ExposureAdjuster extends Adjuster<GPUImageExposureFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setExposure(range(percentage, -10.0f, 10.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getExposure(), -10.0f, 10.0f);
            }
        }

        private class HighlightShadowAdjuster extends Adjuster<GPUImageHighlightShadowFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setShadows(range(percentage, 0.0f, 1.0f));
                getFilter().setHighlights(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getHighlights(), 0.0f, 1.0f);
            }
        }

        private class MonochromeAdjuster extends Adjuster<GPUImageMonochromeFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setIntensity(range(percentage, 0.0f, 1.0f));
                //getFilter().setColor(new float[]{0.6f, 0.45f, 0.3f, 1.0f});
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getIntensity(), 0.0f, 1.0f);
            }
        }

        private class OpacityAdjuster extends Adjuster<GPUImageOpacityFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setOpacity(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getOpacity(), 0.0f, 1.0f);
            }
        }

        private class RGBAdjuster extends Adjuster<GPUImageRGBFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setRed(range(percentage, 0.0f, 1.0f));
                //getFilter().setGreen(range(percentage, 0.0f, 1.0f));
                //getFilter().setBlue(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getRed(), 0.0f, 1.0f);
            }
        }

        private class WhiteBalanceAdjuster extends Adjuster<GPUImageWhiteBalanceFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setTemperature(range(percentage, 2000.0f, 8000.0f));
                //getFilter().setTint(range(percentage, -100.0f, 100.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getTemperature(), 2000.0f, 8000.0f);
            }
        }

        private class VignetteAdjuster extends Adjuster<GPUImageVignetteFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setVignetteStart(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getVignetteStart(), 0.0f, 1.0f);
            }
        }

        private class DissolveBlendAdjuster extends Adjuster<GPUImageDissolveBlendFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setMix(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getMix(), 0.0f, 1.0f);
            }
        }

        private class GaussianBlurAdjuster extends Adjuster<GPUImageGaussianBlurFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setBlurSize(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getBlurSize(), 0.0f, 1.0f);
            }
        }

        private class CrosshatchBlurAdjuster extends Adjuster<GPUImageCrosshatchFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setCrossHatchSpacing(range(percentage, 0.0f, 0.06f));
                getFilter().setLineWidth(range(percentage, 0.0f, 0.006f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getCrossHatchSpacing(), 0.0f, 0.06f);
            }
        }

        private class BulgeDistortionAdjuster extends Adjuster<GPUImageBulgeDistortionFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setRadius(range(percentage, 0.0f, 1.0f));
                getFilter().setScale(range(percentage, -1.0f, 1.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getRadius(), 0.0f, 1.0f);
            }
        }

        private class GlassSphereAdjuster extends Adjuster<GPUImageGlassSphereFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setRadius(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getRadius(), 0.0f, 1.0f);
            }
        }

        private class HazeAdjuster extends Adjuster<GPUImageHazeFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setDistance(range(percentage, -0.3f, 0.3f));
                getFilter().setSlope(range(percentage, -0.3f, 0.3f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getSlope(), -0.3f, 0.3f);
            }
        }

        private class SphereRefractionAdjuster extends Adjuster<GPUImageSphereRefractionFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setRadius(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getRadius(), 0.0f, 1.0f);
            }
        }

        private class SwirlAdjuster extends Adjuster<GPUImageSwirlFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setAngle(range(percentage, 0.0f, 2.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getAngle(), 0.0f, 20.0f);
            }
        }

        private class ColorBalanceAdjuster extends Adjuster<GPUImageColorBalanceFilter> {

            @Override
            public void adjust(int percentage) {
                getFilter().setMidtones(new float[]{
                        range(percentage, 0.0f, 1.0f),
                        range(percentage / 2, 0.0f, 1.0f),
                        range(percentage / 3, 0.0f, 1.0f)});
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getProgress(), 0.0f, 1.0f);
            }
        }

        private class LevelsMinMidAdjuster extends Adjuster<GPUImageLevelsFilter> {
            @Override
            public void adjust(int percentage) {
                getFilter().setMin(0.0f, range(percentage, 0.0f, 1.0f), 1.0f);
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getAverageMid(), 0.0f, 1.0f);
            }
        }

        private class LookupFilterAdjuster extends Adjuster<GPUImageLookupFilter> {
            @Override
            public void adjust(int percentage) {
                getFilter().setStrength(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public int getProgress() {
                return progress(getFilter().getStrength(), 0.0f, 1.0f);
            }
        }
    }
}
