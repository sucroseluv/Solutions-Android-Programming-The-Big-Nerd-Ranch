package android.morlag.com;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {
    private static final String ARG_DATE = "date";
    private static final String EXTRA_DATE = "android.morlag.com.criminalintent.date";
    private DatePicker mDatePicker;
    private Button mSendDateButton;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        if(mDatePicker == null) {
            mDatePicker = (DatePicker) view.findViewById(R.id.dialog_date_picker);
            mDatePicker.init(year,month,day,null);
        }

        mSendDateButton = (Button) view.findViewById(R.id.send_date_button);
        mSendDateButton.setVisibility(View.GONE);

        TextView title = view.findViewById(R.id.date_dialog_title);
        title.setVisibility(View.GONE);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.date_picker_title)
                /*.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, getDateFromDP());
                    }
                })*/
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(getTargetFragment() != null){
                            sendResult(Activity.RESULT_OK, getDateFromDP());
                        }
                        else {
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_DATE, getDateFromDP());
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            closeFragment();
                        }
                    }
                })
                .create();
    }
    private Date getDateFromDP(){
        int year = mDatePicker.getYear();
        int month = mDatePicker.getMonth();
        int day = mDatePicker.getDayOfMonth();
        Date date = new GregorianCalendar(year,month,day).getTime();

        return date;
    }


    private void sendResult(int resultCode, Date date){
        if (getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }

    public static DatePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DatePickerFragment instance = new DatePickerFragment();
        instance.setArguments(args);
        return instance;
    }

    public static Date getDate(Intent intent){
        return (Date) intent.getSerializableExtra(EXTRA_DATE);
    }

    /* Full-screen-activity realization */

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_date, container,false);

        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if(mDatePicker == null) {
            mDatePicker = (DatePicker) view.findViewById(R.id.dialog_date_picker);
            mDatePicker.init(year,month,day,null);
        }

        if(mSendDateButton == null){
            mSendDateButton = (Button) view.findViewById(R.id.send_date_button);
            mSendDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getTargetFragment() != null){
                        sendResult(Activity.RESULT_OK, getDateFromDP());
                    }
                    else {
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_DATE, getDateFromDP());
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        closeFragment();
                    }
                }
            });
        }

        return view;
    }

    private void closeFragment(){
        getActivity().finish();
    }
}
