package android.morlag.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class ImageViewerActivity extends SingleFragmentActivity {
    private static String EXTRA_PATH = "path";
    @Override
    protected Fragment createFragment() {
        String path = getIntent().getStringExtra(EXTRA_PATH);
        return ImageViewerFragment.newInstance(path);
    }

    public static Intent getIntent(Context packageContext, String path) {
        Intent intent = new Intent(packageContext, ImageViewerActivity.class);
        intent.putExtra(EXTRA_PATH, path);
        return intent;
    }
}
