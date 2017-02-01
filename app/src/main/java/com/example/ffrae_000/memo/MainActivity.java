package com.example.ffrae_000.memo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    private final List<Memo> memos = new LinkedList<>();
    private final List<AudioMemo> notFoundAudioMemo = new LinkedList<>();
    private File appFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String outputDirNoSD, outputDir;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: remove if we don't use settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        Utilities.verifyStoragePermissions(this);
        outputDirNoSD = getCacheDir() + File.separator + "MEMO";
        outputDir = getExternalCacheDir() + File.separator + "MEMO";

        if (Utilities.externalStoragecheck()) {
            appFolder = new File(outputDir);
        } else {
            appFolder = new File(outputDirNoSD);
        }
        if (!appFolder.exists()) {
            Utilities.createDirectory(appFolder);
        }
        buildLayout();
        // TODO: if no memos exist display label: "You can add memos by touching the + icon in the bottom-left corner"

        final FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMemoPopup();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: remove if we don't use settings
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: remove if we don't use settings
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

    /**
     * Shows a popup window for selecting the Memo to create
     */
    private void createMemoPopup() {
        // Create View for Utilities.showAlert() method
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        Button createTextMemoBtn = new Button(this);
        createTextMemoBtn.setText("Create Textmemo");
        Button createAudioMemoBtn = new Button(this);
        createAudioMemoBtn.setText("Create Audiomemo");
        // Add buttons to linearLayout and show alert
        linearLayout.addView(createTextMemoBtn);
        linearLayout.addView(createAudioMemoBtn);
        final AlertDialog alert = Utilities.showAlert(MainActivity.this, "Choose the memo type:", null, "Cancel", linearLayout, null);

        // Set OnClickListeners for createTextMemoBtn
        createTextMemoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                createTextMemo();
            }
        });
        // Set OnClickListeners for createTextMemoBtn
        createAudioMemoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                createAudioMemo();
            }
        });
    }

    /**
     * Build the layout by removing all existing layout components and rebuilding them
     */
    private void buildLayout() {
        // Clear layout
        ((ViewGroup) findViewById(R.id.linearLayoutContent)).removeAllViews();
        // Load data
        loadAll();
        // Create a copy of the data and sort it
        List<Memo> temp = new LinkedList<>(memos);
        Collections.sort(temp);
        // Iterate through the sorted copy and create the GUI buttons
        for (Memo m : temp) {
            addButtons(m);
        }
    }

    /**
     * Adds a button to start, share and delete the Memo
     *
     * @param m Memo for which the buttons should be added
     */
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
        ImageButton shareBtn = new ImageButton(this);
        shareBtn.setId(30000 + m.getId());

        // Set layout for memoBtn
        // TODO: display "as much from name as fits..." if name is not fitting inside one line
        memoBtn.setText(m.getName());
        memoBtn.setSingleLine();
        memoBtn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) memoBtn.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.LEFT_OF, shareBtn.getId());
        memoBtn.setLayoutParams(params);
        if (m instanceof TextMemo) {
            // TODO: replace icon by something better fitting
            memoBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_dialog_email, 0, 0, 0);
        } else {
            memoBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_play, 0, 0, 0);
        }

        // Set layout for shareBtn
        shareBtn.setImageResource(android.R.drawable.ic_menu_share);
        shareBtn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        params = (RelativeLayout.LayoutParams) shareBtn.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.LEFT_OF, deleteBtn.getId());
        shareBtn.setLayoutParams(params);

        // Set layout for deleteBtn
        deleteBtn.setImageResource(android.R.drawable.ic_menu_delete);
        deleteBtn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        params = (RelativeLayout.LayoutParams) deleteBtn.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        deleteBtn.setLayoutParams(params);

        // Set OnClickListener for memoBtn
        if (m instanceof TextMemo) {
            memoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), TextActivity.class);
                    intent.putExtra("TextMemo", m);
                    startActivityForResult(intent, 1337);
                }
            });
        } else {
            memoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playMemo((AudioMemo) m);
                }
            });
        }

        // Set OnClickListener for shareBtn
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(m);
            }
        });

        // Set OnClickListener for deleteBtn
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(m);
            }
        });

        // Add buttons to RelativeLayout
        rl.addView(memoBtn);
        rl.addView(shareBtn);
        rl.addView(deleteBtn);

        // Add RelativeLayout to LinearLayout
        llContent.addView(rl);
    }

    /**
     * Shares a Memo with another App
     *
     * @param m Memo which should be shared
     */
    private void share(Memo m) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        if (m instanceof TextMemo) {
            sendIntent.putExtra(Intent.EXTRA_TEXT, m.getName() + ":\n" + ((TextMemo) m).getData());
            sendIntent.setType("text/plain");
        } else {
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(((AudioMemo) m).getData()));
            sendIntent.setType("audio/*");
        }

        startActivity(Intent.createChooser(sendIntent, "Share Memo..."));
    }

    /**
     * Deletes a Memo and rebuilds the layout
     *
     * @param m Memo which should be deleted
     */
    private void delete(final Memo m) {
        Utilities.showAlert(MainActivity.this, "Do you really want to delete " + m.getName() + "?",
                "Yes", "No", null, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        memos.remove(m);
                        if (m instanceof AudioMemo) {
                            Utilities.delete(((AudioMemo) m).getData());
                        }
                        saveAll();
                        buildLayout();
                        return null;
                    }
                });
    }

    /**
     * Saves all Memos from the memos List into a xml-file
     */
    private void saveAll() {
        File file = new File(getFilesDir().getPath() + "/memo_data.xml");
        XStream xstream = new XStream();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = xstream.createObjectOutputStream(fos);

            if (Utilities.externalStoragecheck()) {
                for (AudioMemo nfm : notFoundAudioMemo) {
                    if (!nfm.getData().exists()) {
                        notFoundAudioMemo.remove(nfm);
                    }
                }
            }
            memos.addAll(notFoundAudioMemo);
            notFoundAudioMemo.clear();

            for (Memo m : memos) {
                oos.writeObject(m);
            }
            oos.flush();
            oos.close();
            Log.i(TAG, "Data saved successfully to " + file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all memos from a xml-file into the memos List.
     */
    private void loadAll() {
        // Clear old data
        memos.clear();
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
                        if (memoTemp instanceof AudioMemo && !((AudioMemo) memoTemp).getData().exists()) {
                            notFoundAudioMemo.add((AudioMemo) memoTemp);
                            continue;
                        }
                        memos.add(memoTemp);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (java.io.EOFException e) {
                    Log.i(TAG, "All objects load successfully!");
                    ois.close();
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Shows an Alert asking for the Memos name and creates a TextMemo
     */
    private void createTextMemo() {
        final EditText input = new EditText(getApplicationContext());
        input.setSingleLine();
        input.setTextColor(Color.BLACK);

        Utilities.showAlert(MainActivity.this, "Please insert a name", "OK", "Cancel", input, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                //Add new TextMemo
                TextMemo m = new TextMemo(Utilities.getNextId(memos), input.getText().toString());
                memos.add(Utilities.getNextId(memos), m);
                saveAll();
                buildLayout();
                // Start TextActivity
                Intent intent = new Intent(getApplicationContext(), TextActivity.class);
                intent.putExtra("TextMemo", m);
                startActivityForResult(intent, 1337);

                return null;
            }
        });
    }

    /**
     * Shows an Alert for recording an audio file and creates an AudioMemo
     */
    private void createAudioMemo() {
        final AudioRecorder aR = new AudioRecorder();
        final ImageButton recordButton = new ImageButton(getApplicationContext());
        recordButton.setImageResource(android.R.drawable.btn_star_big_off);

        final AlertDialog recorder = Utilities.showAlert(MainActivity.this, "Record Memo", null, "Cancel", recordButton, null);
        recorder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                cleanUp(aR);
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordButton.setImageResource(android.R.drawable.btn_star_big_on);
                aR.setOutputFileDir(appFolder.getPath());
                aR.setRecorder();
                aR.startRecord();
                recordButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        aR.stopRecord();
                        recorder.dismiss();
                        final EditText input = new EditText(getApplicationContext());
                        input.setSingleLine();
                        input.setTextColor(Color.BLACK);

                        AlertDialog alert = Utilities.showAlert(MainActivity.this, "Please insert a name", "OK", "Cancel", input, new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                // Add new Memo
                                AudioMemo m = new AudioMemo(Utilities.getNextId(memos), input.getText().toString(), appFolder.getPath());
                                memos.add(memos.size(), m);

                                // Rename File
                                File temp = aR.getOutFile();
                                Utilities.moveFile(temp, m.getData());

                                saveAll();
                                buildLayout();
                                // Start PlayDialog
                                playMemo(m);
                                return null;
                            }
                        });
                        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                cleanUp(aR);
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Removes the temporary audio file after recording was cancelled
     */
    private void cleanUp(AudioRecorder aR) {
        // TODO BUG: not deleting the temp file correctly yet.. Debug?! (also bei mir schon Frido)
        aR.stopRecord();
        File temp = new File(appFolder.getPath());
        Utilities.delete(temp);
    }

    /**
     * Opens an AlertDialog to play the Memo
     *
     * @param m Memo which should be played
     */
    private void playMemo(AudioMemo m) {
        final AudioPlayer aP;

        // Show an error and return if the audio file was not found
        try {
            aP = new AudioPlayer(m.getData().getPath());
        } catch (IOException e) {
            // TODO: not working?? App still crashes if the file was deleted (manually) and thus not found....
            Utilities.showAlert(MainActivity.this, "File " + m.getData().getPath() + " not found!", "OK", null, null, null);
            return;
        }

        LinearLayout playerLayout = aP.createLayout(this);
        AlertDialog player = Utilities.showAlert(this, m.getName(), "Close", null, playerLayout, null);
        // Set OnDismissListener to release the MediaPlayer and free its memory once the AlertDialog is closed
        player.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                aP.releasePlayer();
            }
        });
    }
}
