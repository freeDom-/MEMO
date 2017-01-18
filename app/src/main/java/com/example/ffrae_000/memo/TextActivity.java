package com.example.ffrae_000.memo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.EditText;

public class TextActivity extends AppCompatActivity {

    private TextMemo memo;
    private EditText etData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        Intent intent = getIntent();
        memo = (TextMemo) intent.getSerializableExtra("TextMemo");

        // Load text from Memo
        etData = (EditText) findViewById(R.id.editTextData);
        etData.setText(memo.getData());
        etData.setKeyListener(null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabSave = (FloatingActionButton) findViewById(R.id.fabSave);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save data into the object and return code 1337 for saving the changes to a file
                memo.setData(etData.getText().toString());

                Intent intent = new Intent();
                intent.putExtra("TextMemo", memo);
                setResult(1337, intent);
                finish();
            }
        });

        FloatingActionButton fabEdit = (FloatingActionButton) findViewById(R.id.fabEdit);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etData.setKeyListener((KeyListener) etData.getTag());
            }
        });
    }

}
