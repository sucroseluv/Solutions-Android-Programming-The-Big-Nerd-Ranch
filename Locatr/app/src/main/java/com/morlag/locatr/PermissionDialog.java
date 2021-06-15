package com.morlag.locatr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.w3c.dom.Text;

public class PermissionDialog extends DialogFragment {
    interface Kekw {
        void onInvoke();
    }

    public void setInvoke(Kekw invoke) {
        mInvoke = invoke;
    }

    private Kekw mInvoke;


    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        TextView view = new TextView(getActivity());
        view.setText("Locatr использует\n" +
                "позиционные данные для поиска близлежащих изображений на Flickr");
        view.setTextSize(24);
        view.setPadding(5,5,5,5);
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setView(view);

        return adb.create();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        mInvoke.onInvoke();
        // requestPermissions(LocatrFragment.LOCATION_PERMISSIONS,0);
    }
}
