package android.morlag.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity {

    PhotoPageFragment mFragment;
    public static Intent newIntent(Context context, Uri uri){
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(uri);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        return mFragment = PhotoPageFragment.newInstance(getIntent().getData());
    }

    @Override
    public void onBackPressed() {
        if(mFragment != null && mFragment.getWebView().canGoBack())
            mFragment.getWebView().goBack();
        else
            super.onBackPressed();
    }
}
