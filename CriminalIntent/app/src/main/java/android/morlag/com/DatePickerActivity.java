package android.morlag.com;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.Date;

public class DatePickerActivity extends SingleFragmentActivity {
    private static final String DATE_EXTRA = "crime_date";

    @Override
    protected Fragment createFragment() {
        Date date = getDate(getIntent());
        return DatePickerFragment.newInstance(date);
    }

    public static Intent getIntent(Context packageContext, Date date){
        Intent intent = new Intent(packageContext, DatePickerActivity.class);
        intent.putExtra(DATE_EXTRA, date);
        return intent;
    }

    private static Date getDate(Intent intent){
        return (Date) intent.getSerializableExtra(DATE_EXTRA);
    }
}
