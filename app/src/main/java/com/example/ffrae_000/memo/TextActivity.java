package com.example.ffrae_000.memo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import java.util.Date;
import java.util.concurrent.Callable;

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

        FloatingActionButton fabSave = (FloatingActionButton) findViewById(R.id.fabSave);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("clicked");
                // Save data into the object and return code 1337 for saving the changes to a file
                memo.setData(etData.getText().toString());
                memo.setDate(new Date());

                Intent intent = new Intent();
                intent.putExtra("TextMemo", memo);
                setResult(1337, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Show popup question if text was edited
            if (memo.getData().compareTo(etData.getText().toString()) != 0) {
                Utilities.showAlert(TextActivity.this, "Are you sure? All changes won't be saved!",
                        "Yes", "Cancel", null, new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                finish();
                                return null;
                            }
                        });
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
