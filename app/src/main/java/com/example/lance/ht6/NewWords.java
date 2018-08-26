package com.example.lance.ht6;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class NewWords extends AppCompatActivity {

    private static final String TAG = "NewWords";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting the new words screen");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_words);

        Button btnNavToMain = (Button) findViewById(R.id.newWordsToMainButton);

        btnNavToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked btnNavToMain");
                Intent intent = new Intent(NewWords.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
