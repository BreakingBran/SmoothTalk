package com.example.lance.ht6;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lance.ht6.schemas.CountsTableContract;
import com.example.lance.ht6.schemas.CountsTableDbHelper;
import com.example.lance.ht6.schemas.EventsTableContract;
import com.example.lance.ht6.schemas.EventsTableDbHelper;
import com.example.lance.ht6.schemas.ReportPerMinuteDbHelper;
import com.example.lance.ht6.utils.DatabaseUtilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;


public class Recording extends AppCompatActivity implements
        RecognitionListener {

    /* Database inititialization */
    private EventsTableDbHelper dbEventsHelper;
    private SQLiteDatabase dbEvents;
    private CountsTableDbHelper dbCountsHelper;
    private SQLiteDatabase dbCounts;
    private ReportPerMinuteDbHelper dbReportsHelper;
    private SQLiteDatabase dbReports;

    private List<String> wordList;

    public int currentSessionId = -1;

    Button stopButton;
    ImageButton settingsButton;
    ImageButton reportButton;
    ConstraintLayout backgroundView;
    private static final String TAG = "Recording";
    private static final String KEYWORDS_SEARCH = "fillers";
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SpeechRecognizer recognizer;
    private int hypolength;


    public Context getContext() {
        return this.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);


        settingsButton = findViewById(R.id.settings_button);
        reportButton = findViewById(R.id.report_button);

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked the go to reports button");
                Intent writeReport = new Intent(Recording.this, WriteReport.class);
                startActivity(writeReport);
            }
        });

        /* Events database configuration */
        dbEventsHelper = new EventsTableDbHelper(getContext());
        dbCountsHelper = new CountsTableDbHelper(getContext());
        dbReportsHelper = new ReportPerMinuteDbHelper(getContext());
        dbEvents = dbEventsHelper.getWritableDatabase();
        dbCounts = dbCountsHelper.getWritableDatabase();
        dbReports = dbReportsHelper.getWritableDatabase();

        // TO-DO: Temp until we add button to reset tables
        DatabaseUtilities.resetTables(dbEvents,
                dbCounts,
                dbReports);

        currentSessionId = DatabaseUtilities.getLastSessionId(dbEvents) + 1;

        // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        wordList = DatabaseUtilities.getWordList(this.getApplicationContext().getFilesDir());
        Log.i(TAG, "Words detected: " + Arrays.toString(wordList.toArray()));

        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new SetupTask(this).execute();
        stopButton = findViewById(R.id.end_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startRecording = new Intent(Recording.this, MainActivity.class);
                startActivity(startRecording);
                finish();
            }
        });

    }
    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<Recording> activityReference;
        SetupTask(Recording activity) {
            this.activityReference = new WeakReference<>(activity);
        }
        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                activityReference.get().setupRecognizer(assetDir);
            } catch (IOException e) {
                return e;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                Log.i(TAG, result.getMessage());
            } else {
                Log.i(TAG, "STARTING");
                activityReference.get().switchSearch(KEYWORDS_SEARCH);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                new SetupTask(this).execute();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        dbEventsHelper.close();
        super.onDestroy();

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null || hypothesis.getHypstr().split("\\s+").length == hypolength)
            return;

        hypolength = hypothesis.getHypstr().split("\\s+").length;

        String text = hypothesis.getHypstr().split("\\s+")[0];
        makeText(getContext(), "Detected " + text, Toast.LENGTH_SHORT);
        Log.i(Recording.class.getSimpleName(), "DETECTED " + text + "\n");
        // Flash the screen blue
        backgroundView = findViewById(R.id.background);
        updateColor(Color.BLUE);

        // Update database tables
            DatabaseUtilities.updateEvents(dbEvents,
                    text,
                    wordList,
                    currentSessionId);
            DatabaseUtilities.updateCounts(dbCounts,
                    text,
                    wordList,
                    currentSessionId);
    }

    /** Change background screen colour for 1 second. **/
    private void updateColor(int color) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                backgroundView.setBackgroundColor(color);
                new CountDownTimer(500, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        backgroundView.setBackgroundColor(Color.TRANSPARENT);
                    }
                }.start();
            }
        });
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KEYWORDS_SEARCH))
            switchSearch(KEYWORDS_SEARCH);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KEYWORDS_SEARCH)){
            Log.i(TAG, "Started Listening");
            makeText(getContext(), "Started Listening", Toast.LENGTH_SHORT);
            recognizer.startListening(searchName);}
        else
            recognizer.startListening(searchName, 20000);
        }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);

        /* In your application you might not need to add all those searches.
          They are added here for demonstration. You can leave just one.
         */

        // Keyword list search for fillers
        File newKeywords = new File(getContext().getFilesDir(), "keywords.txt");
        recognizer.addKeywordSearch(KEYWORDS_SEARCH, newKeywords);
    }

    @Override
    public void onError(Exception error) {
        makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeout() {
        switchSearch(KEYWORDS_SEARCH);
    }
}
