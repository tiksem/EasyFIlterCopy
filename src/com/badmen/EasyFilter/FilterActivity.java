package com.badmen.EasyFilter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SeekBar;
import com.utilsframework.android.bitmap.BitmapUtilities;
import com.utilsframework.android.bitmap.Size;
import com.utilsframework.android.file.FileUtils;
import com.utilsframework.android.menu.MenuManager;
import com.utilsframework.android.subscaleview.ScaleImagePreviewActivity;
import com.utilsframework.android.threading.AsyncOperationCallback;
import com.utilsframework.android.view.Alerts;
import com.utilsframework.android.view.UiMessages;
import jp.co.cyberagent.android.gpuimage.*;

import java.io.File;
import java.util.List;

/**
 * Created by semyon.tikhonenko on 22.05.2015.
 */
public class FilterActivity extends Activity {
    private static final String IMAGE_PATH = "imagePath";

    private String imagePath;
    private GPUImageView image;
    private List<Filter> filters;
    private GPUImageFilterTools.FilterAdjuster filterAdjuster;
    private SeekBar seekBar;
    private FilterGroupManager filterGroup = new FilterGroupManager();

    public static void start(Context context, String imagePath) {
        Intent intent = new Intent(context, FilterActivity.class);
        intent.putExtra(IMAGE_PATH, imagePath);
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        menu.findItem(R.id.saveAs).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showSaveAsAlert();
                return true;
            }
        });
        menu.findItem(R.id.save).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                saveImage();
                return true;
            }
        });
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

        image = (GPUImageView) findViewById(R.id.image);
        image.setImage(bitmap);

        seekBar = (SeekBar) findViewById(R.id.adjuster);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (filterAdjuster != null) {
                    filterAdjuster.adjust(progress);
                    image.requestRender();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final GridView filtersView = (GridView) findViewById(R.id.grid);
        FilterAdapter filterAdapter = new FilterAdapter(this);
        filters = GPUImageFilterTools.getFilters(this);
        filterAdapter.setElements(filters);
        filtersView.setAdapter(filterAdapter);

        filtersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Filter filter = filters.get(position);
                GPUImageFilter gpuImageFilter = filterGroup.addFilter(filter.filter);
                filterAdjuster = new GPUImageFilterTools.FilterAdjuster(filter.filter);
                seekBar.setVisibility(filterAdjuster.canAdjust() ? View.VISIBLE : View.INVISIBLE);
                seekBar.setProgress(50);
                image.setFilter(gpuImageFilter);
                image.requestRender();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageForPreview();
            }
        });
    }

    private void showSaveAsAlert() {
        Alerts.InputAlertSettings settings = new Alerts.InputAlertSettings();
        settings.message = getString(R.string.enter_file_name);
        settings.okButtonText = getString(R.string.save);
        settings.onInputOk = new Alerts.OnInputOk() {
            @Override
            public void onOk(String value) {
                saveImageAs(value + ".jpg");
            }
        };
        Alerts.showAlertWithInput(this, settings);
    }

    private void saveImageAs(String fileName) {
        saveImageAs(fileName, true);
    }

    private void saveImage() {
        saveImageAs(imagePath, false);
    }

    private void saveImageForPreview() {
        File tempFile = FileUtils.createTempFile(this, "temp.jpg");
        saveImageAs(tempFile.getAbsolutePath(), false, true);
    }

    private void saveImageAs(final String fileName, boolean toPictures) {
        saveImageAs(fileName, toPictures, false);
    }

    private void saveImageAs(final String fileName, boolean toPictures, final boolean forPreview) {
        String message = getString(forPreview ? R.string.generating_preview : R.string.saving_image);
        final ProgressDialog progressDialog = Alerts.showCircleProgressDialog(this, message);
        Size size = BitmapUtilities.getBitmapDimensions(imagePath);
        GPUImageView.OnPictureSavedListener listener = new GPUImageView.OnPictureSavedListener() {
            @Override
            public void onPictureSaved(Uri uri) {
                progressDialog.dismiss();
                if (!forPreview) {
                    ScaleImagePreviewActivity.start(FilterActivity.this, uri);
                } else {
                    ScaleImagePreviewActivity.start(FilterActivity.this, fileName, true);
                }
            }
        };
        if (toPictures) {
            image.saveToPictures("Easy Filter", fileName,
                    size.width, size.height, listener);
        } else {
            image.saveImage(fileName, size.width, size.height, listener);
        }
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
