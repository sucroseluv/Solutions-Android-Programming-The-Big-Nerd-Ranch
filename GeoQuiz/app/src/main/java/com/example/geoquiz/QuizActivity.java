package com.example.geoquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_ANSWERS = "answered";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final String KEY_CHEATER = "cheater";
    private static final String KEY_CHEAT_COUNT = "cheat_count";

    private boolean mIsCheater;
    private int mCheatCount = 3;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private TextView mQuestionTextView;
    private TextView mCheatCountTextView;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    private int mCurrentIndex = 0;
    private boolean[] mAnsweredQuestions = new boolean[mQuestionBank.length];
    private int mCurrentAnsewers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"cnCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);
        setTitle("География");

        if(savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mAnsweredQuestions = savedInstanceState.getBooleanArray(KEY_ANSWERS);
            if (mAnsweredQuestions == null)
                mAnsweredQuestions = new boolean[mQuestionBank.length];
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEATER);
            mCheatCount = savedInstanceState.getInt(KEY_CHEAT_COUNT);
        }
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatCountTextView = (TextView) findViewById(R.id.cheat_count_text_view);

        setButtonEnabled(!mAnsweredQuestions[mCurrentIndex]);

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
                setButtonEnabled(false);
                mAnsweredQuestions[mCurrentIndex] = true;
                if(checkEnd())
                    showResult();
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
                setButtonEnabled(false);
                mAnsweredQuestions[mCurrentIndex] = true;
                if(checkEnd())
                    showResult();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                while(!questionNotAnswered() && !checkEnd()) {
                    mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                }
                updateQuestion();
                setButtonEnabled(!mAnsweredQuestions[mCurrentIndex]);
                mIsCheater = false;
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + mQuestionBank.length - 1) % mQuestionBank.length;
                while(!questionNotAnswered() && !checkEnd()) {
                    mCurrentIndex = (mCurrentIndex + mQuestionBank.length - 1) % mQuestionBank.length;
                }
                updateQuestion();
                setButtonEnabled(!mAnsweredQuestions[mCurrentIndex]);
                mIsCheater = false;
            }
        });
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        updateQuestion();
        updateCount();
    }
    private boolean questionNotAnswered(){
        return !mAnsweredQuestions[mCurrentIndex];
    }
    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }
    private void setButtonEnabled(boolean isEnabled){
        mTrueButton.setEnabled(isEnabled);
        mFalseButton.setEnabled(isEnabled);
    }
    private void checkAnswer(boolean userPressedTrue){
        if(mIsCheater){
            Toast message = Toast.makeText(QuizActivity.this, R.string.judgment_toast, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.TOP,0,120);
            message.show();
        }
        else if(mQuestionBank[mCurrentIndex].isAnswerTrue() == userPressedTrue) {
            Toast message = Toast.makeText(QuizActivity.this, R.string.correct_toast, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.TOP,0,120);
            message.show();
            mCurrentAnsewers++;
        }
        else {
            Toast message = Toast.makeText(QuizActivity.this, R.string.incorrect_toast, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.TOP,0,120);
            message.show();
        }
    }
    private boolean checkEnd(){
        for (boolean ans:mAnsweredQuestions) {
            if(!ans)
                return false;
        }
        mNextButton.setEnabled(false);
        mPreviousButton.setEnabled(false);
        return true;
    }
    private void showResult(){
        Toast.makeText(QuizActivity.this, "Вы ответили на все вопросы",Toast.LENGTH_SHORT).show();
        Toast.makeText(QuizActivity.this, String.format("Вы ответили верно на %.1f%% вопросов.",
                ((double)mCurrentAnsewers/mQuestionBank.length*100)),Toast.LENGTH_LONG).show();
    }
    private void updateCount(){
        mCheatCountTextView.setText("Подсказок: " + mCheatCount);
        if(mCheatCount <= 0){
            mCheatButton.setVisibility(View.GONE);
            mCheatCountTextView.setText("Подсказок не осталось.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
            return;
        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null)
                return;
            mIsCheater = CheatActivity.wasAnswerShown(data);
            mCheatCount--;
            updateCount();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause() called");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putBooleanArray(KEY_ANSWERS, mAnsweredQuestions);
        outState.putBoolean(KEY_CHEATER, mIsCheater);
        outState.putInt(KEY_CHEAT_COUNT, mCheatCount);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy() called");
    }
}