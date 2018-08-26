package com.example.lance.ht6;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;

public class NewWords extends AppCompatActivity {

    private static final String TAG = "NewWords";
    private static final String filename = "keywords.txt";
    private FileOutputStream outputStream;
    Button btnNavToMain;
    EditText wordField;
    Button submitNewWord;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting the new words screen");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_words);


        Button btnNavToMain = (Button) findViewById(R.id.newWordsToMainButton);
        Button btnSubmit = (Button) findViewById(R.id.new_word_submit);
        Button btnReset = (Button) findViewById(R.id.resetButton);
        final EditText mEdit = (EditText) findViewById(R.id.new_word_field);

        btnReset.setOnClickListener((v) -> {
            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write("".getBytes());
                outputStream.close();
                Toast.makeText(getApplicationContext(), "Word set reset", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        myDb = new DatabaseHelper(this);

        //        NAVIGATE TO MAIN PAGE
        btnNavToMain = (Button) findViewById(R.id.newWordsToMainButton);

        btnSubmit.setOnClickListener((v) -> {
            String textToWrite = mEdit.getText().toString();
            // Longer words should have lower threshold
            if (textToWrite.length() < 5) {
                textToWrite = textToWrite + " /1e-1/\n";
            } else {
                textToWrite = textToWrite + " /1e-8/\n";
            }
            Log.d(TAG, "onClick: clicked btnSubmit");
            try {
                outputStream = openFileOutput(filename, MODE_APPEND);
                outputStream.write(textToWrite.getBytes());
                outputStream.close();
                Toast.makeText(getApplicationContext(), mEdit.getText().toString() + " added!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        btnNavToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked btnNavToMain");
                Intent intent = new Intent(NewWords.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //        NEW WORD FIELD
        wordField = (EditText) findViewById(R.id.new_word_field);

        //      SUBMIT NEW WORD BUTTON
        submitNewWord = (Button) findViewById(R.id.new_word_submit);

        submitNewWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempted submited new word");
                if(wordField.getText().toString() != ""){
                    boolean isInserted = myDb.insertWordData(wordField.getText().toString(),0);
                    if(isInserted == true){
                        Toast.makeText(NewWords.this, "Word Added", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(NewWords.this, "Failed to Add Word :(", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }
}
