package com.example.ffrae_000.memo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;


public class MainActivity extends AppCompatActivity {

    //TODO Share-Funktion

    private List<Memo> memos = new LinkedList<>();
    private static String TEMP_FILE = Environment.getExternalStorageDirectory() + "/Media/MEMO/temp.3gpp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        buildLayout();
        // TODO: remove example data
        if (memos.size() == 0) {
            memos.add(new TextMemo(memos.size(), "TextTest"));
            ((TextMemo) memos.get(0)).setData("Lorem Ipsum");
            memos.add(new AudioMemo(memos.size(), "AudioTest"));
            memos.add(new TextMemo(memos.size(), "Text2Test"));
            buildLayout();
        }

        final FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMemoPopup(fabAdd);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Gets result from TextActivity
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == 1337) {
            // Save pressed
            TextMemo memo = (TextMemo) intent.getSerializableExtra("TextMemo");
            // Replaces old memo by edited one returned by intent
            memos.set(memo.getId(), memo);
            saveAll();
            buildLayout();
        }
    }

    private void addMemoPopup(View caller) {
        // Create View for Helpers.showAlert() method
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        Button createTextMemo = new Button(this);
        createTextMemo.setText("Create Textmemo");
        Button createAudioMemo = new Button(this);
        createAudioMemo.setText("Create Audiomemo");
        // Add buttons to linearLayout and show alert
        linearLayout.addView(createTextMemo);
        linearLayout.addView(createAudioMemo);
        final AlertDialog alert = Helpers.showAlert(MainActivity.this, "Choose the memo type:", null, "Cancel", linearLayout, null);

        // Add OnClickListeners to Buttons
        createTextMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(getApplicationContext());
                input.setSingleLine();

                Helpers.showAlert(MainActivity.this, "Please insert a name", "OK", "Cancel", input, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        TextMemo m = new TextMemo(memos.size(), input.getText().toString());
                        memos.add(memos.size(), m);
                        saveAll();
                        buildLayout();
                        // Start TextActivity
                        Intent intent = new Intent(getApplicationContext(), TextActivity.class);
                        intent.putExtra("TextMemo", m);
                        startActivityForResult(intent, 1337);
                        // Close opened alert
                        alert.dismiss();            //TODO alert.dismiss() würde ich schon direkt bei der auswahl aufrufen siehe createAudioMemo
                        return null;
                    }
                });
            }
        });
        createAudioMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Create AudioMemo
                alert.dismiss();
                record();
            }
        });
    }

    private void record(){
        final ImageButton recordButton = new ImageButton(getApplicationContext());
        recordButton.setImageResource(android.R.drawable.ic_btn_speak_now);                         //TODO Layout

        final AlertDialog recorder = Helpers.showAlert(MainActivity.this, "Record Memo", null, "cancle", recordButton, null);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AudioRecorder aR = new AudioRecorder();
                recordButton.setImageResource(android.R.drawable.ic_notification_overlay);
                aR.setRecorder();
                aR.startRecord();                                                                   //TODO fix recordproblem
                recordButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        aR.stopRecord();
                        recorder.dismiss();
                        playRecorded();
                    }
                });
            }
        });
    }

    private void playRecorded(){
        AudioPlayer aP;
        try {
            aP = new AudioPlayer(TEMP_FILE);
        } catch (FileNotFoundException e){
            AlertDialog.Builder fnf = new AlertDialog.Builder(this);
            fnf.setMessage("File not found!");
            fnf.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            fnf.create();
            fnf.show();
            return;
        }
        String length = "5:00";                             //TODO insert length of the audiofile

        LinearLayout playerLayout = aP.setPlayerLayout(this, length, "00:00");

        LinearLayout recordReplayLayout = new LinearLayout(this);
        recordReplayLayout.setOrientation(LinearLayout.VERTICAL);


        Button redoButton = new Button(this);
        redoButton.setText("REDO");

        recordReplayLayout.addView(playerLayout);
        recordReplayLayout.addView(redoButton);

        final AlertDialog player = Helpers.showAlert(this, "Your MEMO", "save", "cancle", recordReplayLayout, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                final EditText input = new EditText(getApplicationContext());
                input.setSingleLine();

                Helpers.showAlert(MainActivity.this, "Please insert a name", "OK", "Cancel", input, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        AudioMemo m = new AudioMemo(memos.size(), input.getText().toString());
                        memos.add(memos.size(), m);

                        try {
                            FileInputStream fis = new FileInputStream(TEMP_FILE);
                            File zwischenSpeicher = new File(m.getPath());
                            if (zwischenSpeicher.exists()){
                                zwischenSpeicher.delete();
                            }
                            
                            FileOutputStream fos = new FileOutputStream(zwischenSpeicher);
                            fos.write(fis.read());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        saveAll();
                        buildLayout();
                        return null;
                    }
                });
                return null;
            }
        });

        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.dismiss();
                record();
            }
        });
    }

    private void buildLayout() {
        // Clear old data
        memos.clear();
        ((ViewGroup) findViewById(R.id.linearLayoutContent)).removeAllViews();
        // Load data
        loadAll();
        // Create a copy of the data and sort it
        List<Memo> temp = new LinkedList<>(memos);
        Collections.sort(temp, new Comparator<Memo>() {
            @Override
            public int compare(Memo memo, Memo t1) {
                // sort by date inverse -> newest entry will be first
                return t1.compareTo(memo);
            }
        });
        // Iterate through the sorted copy and create the GUI buttons
        for (Memo m : temp) {
            addButtons(m);
        }
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

        if (m instanceof TextMemo) {
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
        } else {
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
                Helpers.showAlert(MainActivity.this, "Do you really want to delete " + m.getName() + "?",
                        "Yes", "No", null, new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                memos.remove(m);
                                if (m instanceof AudioMemo){
                                    File delTemp = new File(((AudioMemo) m).getPath());
                                    delTemp.delete();
                                }
                                saveAll();
                                buildLayout();
                                return null;
                            }
                        });
            }
        });

        // Add buttons to RelativeLayout
        rl.addView(memoBtn);
        rl.addView(deleteBtn);

        // Add RelativeLayout to LinearLayout
        llContent.addView(rl);
    }

    private void saveAll() {
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

    private void loadAll() {
        XStream xstream = new XStream();
        File file = new File(getFilesDir().getPath() + "/memo_data.xml");
        if (file.exists()) {
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
