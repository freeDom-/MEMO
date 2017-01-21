package com.example.ffrae_000.memo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileInputStream;
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

    private List<Memo> memos = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        buildLayout();
        // TODO: remove example data
        if(memos.size() == 0) {
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

    // Get result from TextActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == 1337){
            // Save pressed
            TextMemo memo = (TextMemo) intent.getSerializableExtra("TextMemo");
            // replace old memo by edited one returned from intent
            memos.set(memo.getId(), memo);
            saveAll();
            rebuildLayout();
        }
    }

    private void addMemoPopup(View caller) {
        // TODO: Optimize PopUp for adding a Memo here - betterlooking and better positioning
        // use Alert aswell???
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.add_memo_popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                                                  LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(caller, Gravity.CENTER, 0, 0);

        Button createTextMemo = (Button) popupView.findViewById(R.id.buttonCreateTextMemo);
        Button createAudioMemo = (Button) popupView.findViewById(R.id.buttonCreateAudioMemo);

        createTextMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(getApplicationContext());
                // TODO: keine Zeilenumbrüche (Enter) im EditText zulassen! Nur gültige Dateinamen Zulassen!

                Helpers.showAlert(MainActivity.this, "Please insert a name", "OK", "Cancel", input, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        // TODO: Create new TextMemo and open it
                        TextMemo m = new TextMemo(memos.size(), input.getText().toString());
                        memos.add(memos.size(), m);
                        saveAll();
                        rebuildLayout();
                        // Start TextActivity
                        Intent intent = new Intent(getApplicationContext(), TextActivity.class);
                        intent.putExtra("TextMemo", m);
                        startActivityForResult(intent, 1337);
                        popupWindow.dismiss();
                        return null;
                    }
                });
            }
        });
        createAudioMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Create AudioMemo
            }
        });
    }

    private void rebuildLayout() {
        memos.clear();
        ((ViewGroup) findViewById(R.id.linearLayoutContent)).removeAllViews();
        buildLayout();
    }

    private void buildLayout() {
        loadAll();
        List<Memo> temp = new LinkedList<>(memos);
        Collections.sort(temp, new Comparator<Memo>() {
            @Override
            public int compare(Memo memo, Memo t1) {
                // sort by date inverse -> newest entry will be first
                return t1.compareTo(memo);
            }
        });

        for(Memo m : temp) {
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
                Helpers.showAlert(MainActivity.this, "Do you really want to delete " + m.getName() + "?",
                                  "Yes", "No", null, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        memos.remove(m);
                        saveAll();
                        rebuildLayout();
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

            for(Memo m : memos) {
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
