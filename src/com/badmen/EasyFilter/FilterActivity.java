package com.badmen.EasyFilter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.utilsframework.android.bitmap.BitmapUtilities;
import com.utilsframework.android.threading.AsyncOperationCallback;
import com.utilsframework.android.view.Alerts;
import com.utilsframework.android.view.UiMessages;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

import java.util.List;

/**
 * Created by semyon.tikhonenko on 22.05.2015.
 */
public class FilterActivity extends Activity {
    private static final String IMAGE_PATH = "imagePath";

    private String imagePath;
    private GPUImageView image;
    private List<Filter> filters;

    public static void start(Context context, String imagePath) {
        Intent intent = new Intent(context, FilterActivity.class);
        intent.putExtra(IMAGE_PATH, imagePath);
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePath = getIntent().getStringExtra(IMAGE_PATH);
        Alerts.runAsyncOperationWithCircleLoading(this, R.string.please_wait,
                new AsyncOperationCallback<Bitmap>() {
                    Bitmap miniBitmap;

                    @Override
                    public Bitmap runOnBackground() {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample);
                        double k = Math.min(bitmap.getWidth() / 100.0, bitmap.getHeight() / 100.0);
                        miniBitmap = BitmapUtilities.resizeExistingBitmap(bitmap, (int) (bitmap.getWidth() / k),
                                (int) (bitmap.getHeight() / k));
                        return bitmap;
                    }

                    @Override
                    public void onFinish(Bitmap result) {
                        initViews(result, miniBitmap);
                    }
                });
    }

    private void initViews(Bitmap bitmap, Bitmap miniBitmap) {
        setContentView(R.layout.filter_activity);

        image = (GPUImageView) findViewById(R.id.image);
        image.setImage(bitmap);

        final GridView filtersView = (GridView) findViewById(R.id.grid);
        FilterAdapter filterAdapter = new FilterAdapter(this, miniBitmap);
        filters = GPUImageFilterTools.getFilters(this);
        filterAdapter.setElements(filters);
        filtersView.setAdapter(filterAdapter);

        filtersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Filter filter = filters.get(position);
                image.setFilter(filter.filter);
            }
        });
    }

    void generateSamples() {
        new Runnable() {
            int i = 0;

            @Override
            public void run() {
                if (i < filters.size()) {
                    Filter filter = filters.get(i);
                    image.setFilter(filter.filter);
                    image.saveToPictures("samples", "sample_" + i + ".jpg", 100, 100,
                            new GPUImageView.OnPictureSavedListener() {
                                @Override
                                public void onPictureSaved(Uri uri) {
                                    i++;
                                    run();
                                }
                            });
                } else {
                    UiMessages.message(FilterActivity.this, "finished");
                }
            }
        };
    }
}
