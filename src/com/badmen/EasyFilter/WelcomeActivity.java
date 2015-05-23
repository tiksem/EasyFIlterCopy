package com.badmen.EasyFilter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import com.utilsframework.android.image.ImagePreviewActivity;
import com.utilsframework.android.image.ImageUtils;

public class WelcomeActivity extends Activity {
    static final int GALLERY = 1;
    static final int CAMERA = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUtils.pickImageFromGallery(WelcomeActivity.this, GALLERY);
            }
        });

        findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUtils.takeImageFromCamera(WelcomeActivity.this, CAMERA);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String selectedImagePath = ImageUtils.getSelectedImagePath(this, data);
            if (selectedImagePath != null) {
                FilterActivity.start(this, selectedImagePath);
            }
        }
    }
}
