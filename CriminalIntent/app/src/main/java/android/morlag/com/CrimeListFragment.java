package android.morlag.com;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.morlag.com.database.CrimeDbSchema;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private List<Crime> mCrimes;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private ConstraintLayout mNullCrimesHint;
    private Button mHintButton;
    private Callbacks mCallbacks;

    interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list,container,false);
        mCrimeRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(savedInstanceState!=null)
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        mNullCrimesHint = (ConstraintLayout) v.findViewById(R.id.null_crimes_hint);
        mHintButton = (Button)mNullCrimesHint.findViewById(R.id.create_crime);
        mHintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getId());
                startActivityForResult(intent,1);
            }
        });

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT){

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mCrimeRecyclerView);

        updateUI();
        return v;
    }
    private class CrimeHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ConstraintLayout layout;
        private ImageView mSolvedImageView;
        private Crime mCrime;
        DateFormat mDateFormat;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent, int type) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            itemView.setOnClickListener(this);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
            mDateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
        }
        public void bind(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mDateFormat.format(mCrime.getDate()));
            mSolvedImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.GONE);
            View v = this.itemView.findViewById(R.id.crime_item_layout);
            v.setContentDescription("Преступление " + crime.getTitle() + " дата " + crime.getDate()
                    + (crime.isSolved() ? "раскрыто" : "не раскрыто"));
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onCrimeSelected(mCrime);
        }
    }
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;
        public void setNewCrime(Crime crime, int position){
            mCrimes.set(position,crime);
        }
        public Crime getCrime(int position){
            return mCrimes.get(position);
        }

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(inflater, parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            holder.bind(mCrimes.get(position));
        }
        public void onItemDismiss(int position) {
            CrimeLab.get(getActivity()).removeCrime(mCrimes.get(position));
            mCrimes.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void refreshAll() {
            mCrimes = CrimeLab.get(getActivity()).getCrimes();
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }
    }
    public void updateUI(){
        if(mAdapter == null){
            CrimeLab crimeLab = CrimeLab.get(getActivity());
            mCrimes = crimeLab.getCrimes();
            mAdapter = new CrimeAdapter(mCrimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.refreshAll();
        }
    }

    private void updateItemUI(int position) {
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSubtitle();
        updateHint();
    }

    private void updateHint(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        if(crimeCount <= 0)
            mNullCrimesHint.setVisibility(View.VISIBLE);
        else
            mNullCrimesHint.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode != Activity.RESULT_OK)
            return;
        if(requestCode == 1){
            CrimeLab lab = CrimeLab.get(getActivity());
            int[] updateArray = CrimePagerActivity.getUpdateArray(data);
            int[] deleteArray = CrimePagerActivity.getDeleteArray(data);

            for(int i : updateArray){
                if(i >= 0 && i < mCrimes.size()) {
                    mAdapter.setNewCrime(lab.getCrime(mAdapter.getCrime(i).getId()),i);
                    mAdapter.notifyItemChanged(i);
                }
            }
            for(int i : deleteArray){
                if(i >= 0 && i < mCrimes.size()) {
                    mAdapter.notifyItemRemoved(i);
                    mCrimes.remove(i);
                }
            }

        }
    }

    private boolean mSubtitleVisible;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem item = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible)
            item.setTitle(R.string.hide_subtitle);
        else
            item.setTitle(R.string.show_subtitle);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                //Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getId());
                //startActivityForResult(intent,1);
                mCallbacks.onCrimeSelected(crime);

                mCrimes.add(crime);
                //mAdapter.notifyItemInserted(mCrimes.size()-1);
                updateUI();
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);
        if(!mSubtitleVisible)
            subtitle = null;
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
