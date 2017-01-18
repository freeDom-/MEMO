package com.example.ffrae_000.memo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<Memo> memos = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        load();
        memos.add(new TextMemo(memos.size(), "TextTest"));
        memos.add(new TextMemo(memos.size(), "Text2Test"));
        memos.add(new AudioMemo(memos.size(), "AudioTest"));
        memos.add(new TextMemo(memos.size(), "Text3Test"));

        for(Memo m : memos) {
            addButtons(m);
        }

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Create PopUp for adding a Memo here
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addButtons(final Memo m) {
        LinearLayout llContent = (LinearLayout) findViewById(R.id.linearLayoutContent);
        final RelativeLayout rl = new RelativeLayout(this);
        rl.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        // Create buttons
        final Button memoBtn = new Button(this);
        memoBtn.setId(10000 + m.getId());
        ImageButton deleteBtn = new ImageButton(this);
        deleteBtn.setId(20000 + m.getId());

        // Set layout for memoBtn
        memoBtn.setText(m.getName());
        memoBtn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) memoBtn.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.LEFT_OF, deleteBtn.getId());
        memoBtn.setLayoutParams(params);

        if(m instanceof TextMemo) {
            memoBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_dialog_email, 0, 0, 0);
            // Set OnClickListener
            memoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), TextActivity.class);
                    intent.putExtra("TextMemo", m);
                    startActivityForResult(intent, 1337);
                }
            });
        }
        else {
            memoBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_play, 0, 0, 0);
            // Set OnClickListener
            memoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: show popup for audio playback
                }
            });
        }

        // Set layout for deleteBtn
        deleteBtn.setImageResource(android.R.drawable.ic_menu_delete);
        deleteBtn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        params = (RelativeLayout.LayoutParams) deleteBtn.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        deleteBtn.setLayoutParams(params);

        // Set OnClickListener for deleteBtn
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memos.remove(m.getId());
                // TODO: program crashing when removing first memo before second memo
                ((ViewGroup) memoBtn.getParent()).removeAllViews();
                ((ViewGroup) rl.getParent()).removeView(rl);
            }
        });

        // Add buttons to RelativeLayout
        rl.addView(memoBtn);
        rl.addView(deleteBtn);

        // Add RelativeLayout to LinearLayout
        llContent.addView(rl);
    }

    private void save() {
        File file = new File(getFilesDir().getPath() + "/memo_data.xml");
        XStream xstream = new XStream();


        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = xstream.createObjectOutputStream(fos);

            for (Memo m : memos) {
                oos.writeObject(m);
            }
            oos.flush();
            oos.close();
            System.out.println("File saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        XStream xstream = new XStream();
        File file = new File(getFilesDir().getPath() + "/memo_data.xml");
        if(file.exists()) {
            try {
                FileInputStream fis = openFileInput("memo_data.xml");
                Memo memoTemp;
                ObjectInputStream ois = xstream.createObjectInputStream(fis);
                try {
                    while (true) {
                        memoTemp = (Memo) ois.readObject();
                        memos.add(memoTemp);
                        System.out.println(memoTemp.toString());
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (java.io.EOFException e) {
                    // finished loading all objects
                    System.out.println("all objects read");
                    ois.close();
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}