package com.samahmakki.seacrhforbooksandsave;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.samahmakki.seacrhforbooksandsave.classes.SharedPref;
import com.samahmakki.seacrhforbooksandsave.data.AuthorNameDbHelper;
import com.samahmakki.seacrhforbooksandsave.data.BookContract.Topic;
import com.samahmakki.seacrhforbooksandsave.data.TopicDbHelper;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class FavoriteTopicsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ListView topics_list;
    ArrayAdapter<String> adapter;

    EditText topicsEditText;
    String topic;
    TopicDbHelper topicDbHelper;
    int selectedItem;
    SharedPref sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        setTitle(R.string.favorite_topics);

        sharedpref = new SharedPref(this);//load night mode setting
        if (sharedpref.loadNightModeState() == true) {

            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer_fav_top);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ArrayList<String> NameItem = new ArrayList<String>();
        topics_list = findViewById(R.id.topic_list);
        adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_3, NameItem);
        topics_list.setAdapter(adapter);

        ///////////////////////////////////////////////////////////

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ////////////////////////////////////////////////////////////

        Button add_topic_btn = findViewById(R.id.add_topic_btn);
        add_topic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // custom dialog
                final Dialog dialog = new Dialog(FavoriteTopicsActivity.this);
                dialog.setContentView(R.layout.add_author);
                //dialog.setTitle("Add Author");

                // set the custom dialog components - text, image and button
                topicsEditText = dialog.findViewById(R.id.add_auth_name);

                Button okButton = dialog.findViewById(R.id.ok_btn);
                Button cancelButton = dialog.findViewById(R.id.cancel_btn);

                // if button is clicked, close the custom dialog
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        topic = topicsEditText.getText().toString();
                        if (!topic.isEmpty()) {
                            try {
                                topicDbHelper = new TopicDbHelper(FavoriteTopicsActivity.this);
                                long newRowId = topicDbHelper.insertName(topic);
                                adapter.add(topic);
                                if (newRowId == -1) {
                                    Toast.makeText(FavoriteTopicsActivity.this, getResources().getString(R.string.type_topic_name_again)
                                            , Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(FavoriteTopicsActivity.this, getResources().getString(R.string.successfully_saved)
                                            , Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.e("error", e.getMessage());
                            }
                        }

                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        topics_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final PopupMenu popupMenu = new PopupMenu(FavoriteTopicsActivity.this, view);
                popupMenu.inflate(R.menu.pop_up_edit_author);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        selectedItem = item.getItemId();

                        if (selectedItem == R.id.delete) {
                            showDialogDelete(position);
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
    }

    private void showDialogDelete(final int i) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Objects.requireNonNull(FavoriteTopicsActivity.this));
        dialogDelete.setTitle(getString(R.string.delete_topic));
        dialogDelete.setMessage(getResources().getString(R.string.delete_topic_msg));
        dialogDelete.setPositiveButton(getResources().getString(R.string.ok)
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    topic = adapter.getItem(i);
                    topicDbHelper = new TopicDbHelper(FavoriteTopicsActivity.this);
                    topicDbHelper.deleteName(topic);
                    adapter.remove(topic);
                    Toast.makeText(FavoriteTopicsActivity.this, getResources().getString(R.string.deleted)
                            , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }

            }
        });
        dialogDelete.setNegativeButton(getResources().getString(R.string.cancel)
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Read();
    }

    private void Read() {
        topicDbHelper = new TopicDbHelper(FavoriteTopicsActivity.this);

        final SQLiteDatabase db = topicDbHelper.getReadableDatabase();
        String[] projection = {
                Topic.COLUMN_TOPIC,
        };

        Cursor cursor = db.query(
                Topic.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        final ArrayList<String> NameItem = new ArrayList<String>();
        topics_list = findViewById(R.id.topic_list);
        adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_3, NameItem);
        topics_list.setAdapter(adapter);

        try {
            // Figure out the index of each column
            int topicColumnIndex = cursor.getColumnIndex(Topic.COLUMN_TOPIC);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                String currentTopic = cursor.getString(topicColumnIndex);
                NameItem.add(currentTopic);
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        final String appUrl = "https://play.google.com/store/apps/details?id=com.samahmakki.seacrhforbooksandsave";
        if (id == R.id.home) {
            Intent it = new Intent(FavoriteTopicsActivity.this, MainActivity.class);
            startActivity(it);
        }
        else if (id == R.id.saved_books) {
            Intent it = new Intent(FavoriteTopicsActivity.this, SavedBooksActivity.class);
            startActivity(it);
        } /*else if (id == R.id.favorite_authors) {
            Intent it = new Intent(FavoriteTopicsActivity.this, FavoriteAuthorsActivity.class);
            startActivity(it);
        } else if (id == R.id.favorite_topics) {
            Intent it = new Intent(FavoriteTopicsActivity.this, FavoriteTopicsActivity.class);
            startActivity(it);
        }*/ else if (id == R.id.language) {
            showChangeLanguageDialog();
        } else if (id == R.id.night_mode) {
            showNightModeDialog();
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, appUrl);
            startActivity(Intent.createChooser(shareIntent, "Share using"));

        } else if (id == R.id.nav_rate) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FavoriteTopicsActivity.this);
            dialogBuilder.setTitle(R.string.rate_s);
            dialogBuilder.setMessage(R.string.if_you);
            dialogBuilder.setPositiveButton(R.string.rate_s, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(appUrl));
                    startActivity(i);
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();

        }
        else if (id == R.id.exit) {
            // Toast.makeText(appContext, "BAck", Toast.LENGTH_LONG).show();
            AlertDialog.Builder alert = new AlertDialog.Builder(FavoriteTopicsActivity.this);
            alert.setTitle(getString(R.string.app_name));
            // alert.setIcon(R.drawable.ic_logout);
            alert.setMessage(getString(R.string.Quit));
            alert.setPositiveButton(getString(R.string.Yes),
                    new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {

                           /* finish();
                            System.exit(0);*/
                            finishAffinity();
                        }
                    });
            alert.setNegativeButton(getString(R.string.No),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            dialog.dismiss();
                        }

                    });

            alert.show();
            return true;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showChangeLanguageDialog() {
        //Array of language to display in alert dialog
        final String[] listItems = {"English","عربي"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(FavoriteTopicsActivity.this);
        mBuilder.setTitle(R.string.choose_language);
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 1) {
                    //Arabic
                    Locale locale = new Locale("ar");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources()
                            .getDisplayMetrics());
                    SharedPreferences.Editor editor = getSharedPreferences("CommonPrefs",
                            MODE_PRIVATE).edit();
                    editor.putString("Language", "ar");
                    editor.apply();
                    recreate();
                    Toast.makeText(FavoriteTopicsActivity.this, "تم إختيار اللغة العربية", Toast.LENGTH_LONG).show();

                } else if (which == 0) {

                    //English
                    Locale locale = new Locale("en");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources()
                            .getDisplayMetrics());
                    SharedPreferences.Editor editor = getSharedPreferences("CommonPrefs",
                            MODE_PRIVATE).edit();
                    editor.putString("Language", "en");
                    editor.apply();
                    recreate();
                    Toast.makeText(FavoriteTopicsActivity.this, "English Language Selected", Toast.LENGTH_LONG).show();
                }
                //dismiss BillAlert dialog when language selected
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        //show BillAlert Dialog
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("CommonPrefs", MODE_PRIVATE).edit();
        editor.putString("Language", lang);
        editor.apply();
    }

    public void loadLocale() {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        setLocale(language);
    }

    public void restartApp() {
        Intent i = new Intent(getApplicationContext(), FavoriteTopicsActivity.class);
        startActivity(i);
        // finish();
    }

    private void showNightModeDialog() {
        //Array of language to display in alert dialog
        final String[] listItems = {getResources().getString(R.string.night_mode_on),
                getResources().getString(R.string.night_mode_off)};

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(FavoriteTopicsActivity.this);
        mBuilder.setTitle(R.string.choose_nightMode_state);
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    sharedpref.setNightModeState(true);
                    restartApp();
                    Toast.makeText(FavoriteTopicsActivity.this, getResources().getString(R.string.night_modeOn),
                            Toast.LENGTH_LONG).show();

                } else if (which == 1) {
                    sharedpref.setNightModeState(false);
                    restartApp();
                    Toast.makeText(FavoriteTopicsActivity.this, getResources().getString(R.string.night_modeOff),
                            Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
}
