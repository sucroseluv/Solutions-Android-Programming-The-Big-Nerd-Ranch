package android.morlag.com;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity
        implements CrimeFragment.Callbacks {
    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private static final String EXTRA_CRIME_ID = "android.morlag.com.criminalintent.crime_id";
    private static final String KEY_UPDATE = "crime_update";
    private static final String KEY_DELETE = "crime_delete";
    private Button mToStartButton;
    private Button mToEndButton;
    private HashSet<Integer> toUpdateList;
    private HashSet<Integer> toDeleteList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        toUpdateList = new HashSet<Integer>();
        toDeleteList = new HashSet<Integer>();

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);

        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }

        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position==0)
                    mToStartButton.setEnabled(false);
                else
                    mToStartButton.setEnabled(true);
                if(position==mCrimes.size()-1)
                    mToEndButton.setEnabled(false);
                else
                    mToEndButton.setEnabled(true);

                addToUpdateList(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        mToStartButton = (Button) findViewById(R.id.to_start_button);
        mToStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
                mToStartButton.setEnabled(false);
            }
        });

        mToEndButton = (Button) findViewById(R.id.to_end_button);
        mToEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mCrimes.size()-1);
                mToEndButton.setEnabled(false);
            }
        });

    }

    public static Intent newIntent(Context packageContext, UUID crimeID){
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeID);
        return intent;
    }

    private void addToUpdateList(int position){
        toUpdateList.add(position);
    }
    private int[] getArrayFromHashset(HashSet hashSet){
        ArrayList<Integer> array = new ArrayList<>(hashSet);
        int[] currentArray = new int[array.size()];

        for(int i = 0; i < currentArray.length; i++){
            currentArray[i] = array.get(i);
        }

        return currentArray;
    }
    private void setArrayResult(){
        Intent intent = new Intent();

        int[] deleteList = new int[]{};
        if(toDeleteList != null){
            deleteList = getArrayFromHashset(toDeleteList);
            for(int i : deleteList)
                toUpdateList.remove(i);
        }

        int[] updateList = new int[]{};
        if(toUpdateList != null)
            updateList = getArrayFromHashset(toUpdateList);

        intent.putExtra(KEY_UPDATE, updateList);
        intent.putExtra(KEY_DELETE, deleteList);
        setResult(Activity.RESULT_OK, intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static int[] getUpdateArray(Intent intent){
        return intent.getIntArrayExtra(KEY_UPDATE);
    }
    public static int[] getDeleteArray(Intent intent){
        return intent.getIntArrayExtra(KEY_DELETE);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = new MenuInflater(CrimePagerActivity.this);
        inflater.inflate(R.menu.activity_crime_pager,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.delete_crime) :
                toDeleteList.add(mViewPager.getCurrentItem());
                CrimeLab.get(CrimePagerActivity.this).removeCrime(mCrimes.get(mViewPager.getCurrentItem()));
                finish();
        }

        return true;
    }

    @Override
    public void finish() {
        setArrayResult();

        super.finish();
    }

    @Override
    public void onCrimeUpdated(Crime crime) {   }
}
