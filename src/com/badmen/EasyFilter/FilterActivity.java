package com.badmen.EasyFilter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.GridView;
import com.utilsframework.android.threading.AsyncOperationCallback;
import com.utilsframework.android.view.Alerts;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by semyon.tikhonenko on 22.05.2015.
 */
public class FilterActivity extends Activity {
    private static final String IMAGE_PATH = "imagePath";

    private String imagePath;

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
                    @Override
                    public Bitmap runOnBackground() {
                        return BitmapFactory.decodeFile(imagePath);
                    }

                    @Override
                    public void onFinish(Bitmap result) {
                        initViews(result);
                    }
                });
    }

    private void initViews(Bitmap bitmap) {
        setContentView(R.layout.filter_activity);

        GPUImageView image = (GPUImageView) findViewById(R.id.image);
        image.setImage(bitmap);
        image.setFilter(new GPUImageGrayscaleFilter());

        GridView filtersView = (GridView) findViewById(R.id.grid);
        FilterAdapter filterAdapter = new FilterAdapter(this, bitmap);
        filterAdapter.setElements(Filters.filters);
        filtersView.setAdapter(filterAdapter);
    }
}
