package android.morlag.com;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

public class ImageViewerFragment extends DialogFragment {
    private static String ARG_PATH = "path";

    public static ImageViewerFragment newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
        ImageViewerFragment instance = new ImageViewerFragment();
        instance.setArguments(args);
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String path = getArguments().getString(ARG_PATH);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image,null);
        ImageView imageView = view.findViewById(R.id.dialog_viewer_image);
        imageView.setImageDrawable(Drawable.createFromPath(path));
        String filename=path.substring(path.lastIndexOf("/")+1);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(filename)
                .setPositiveButton("Close",null)
                .create();
    }
}
