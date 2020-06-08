package com.samahmakki.seacrhforbooksandsave;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.samahmakki.seacrhforbooksandsave.classes.Adapter;
import com.samahmakki.seacrhforbooksandsave.classes.SharedPref;
import com.samahmakki.seacrhforbooksandsave.fragments.SavedBooksFragment;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer;
    SharedPref sharedpref;
    private static final int RC_DOCUMENT_PICKER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        setTitle(R.string.app_name);

        sharedpref = new SharedPref(this);//load night mode setting
        if (sharedpref.loadNightModeState() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.viewpager);

        Adapter adapter = new Adapter(MainActivity.this, getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);

        ///////////////////////////////////////////////////////////

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ////////////////////////////////////////////////////////////
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
         final String appUrl = "https://play.google.com/store/apps/details?id=com.samahmakki.seacrhforbooksandsave";
        if (id == R.id.home) {
            Intent it = new Intent(MainActivity.this, MainActivity.class);
            startActivity(it);
        }
        else if (id == R.id.saved_books) {
            Intent it = new Intent(MainActivity.this, SavedBooksActivity.class);
            startActivity(it);
        }
        /*else if (id == R.id.favorite_authors) {
            Intent it = new Intent(MainActivity.this, FavoriteAuthorsActivity.class);
            startActivity(it);
        }
        else if (id == R.id.favorite_topics) {
            Intent it = new Intent(MainActivity.this, FavoriteTopicsActivity.class);
            startActivity(it);
        }*/
        /*else if (id == R.id.from_storage){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("document/pdf");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"),
                    RC_DOCUMENT_PICKER);
        }*/
        else if (id == R.id.language) {
            showChangeLanguageDialog();
        }
        else if (id == R.id.night_mode) {
            showNightModeDialog();
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, appUrl);
            startActivity(Intent.createChooser(shareIntent, "Share using"));

        } else if (id == R.id.nav_rate) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
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
            AlertDialog.Builder alert = new AlertDialog.Builder(
                    MainActivity.this);
            alert.setTitle(getString(R.string.app_name));
            // alert.setIcon(R.drawable.ic_logout);
            alert.setMessage(getString(R.string.Quit));
            alert.setPositiveButton(getString(R.string.Yes),
                    new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {

                            /*finish();
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

    private void showChangeLanguageDialog() {
        //Array of language to display in alert dialog
        final String[] listItems = {"English","عربي"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
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
                    Toast.makeText(MainActivity.this, "تم إختيار اللغة العربية", Toast.LENGTH_LONG).show();

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
                    Toast.makeText(MainActivity.this, "English Language Selected", Toast.LENGTH_LONG).show();
                }
                //dismiss BillAlert dialog when language selected
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        //show BillAlert Dialog
        mDialog.show();
    }

    private void showNightModeDialog() {
        //Array of language to display in alert dialog
        final String[] listItems = {getResources().getString(R.string.night_mode_on),
                getResources().getString(R.string.night_mode_off)};

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle(R.string.choose_nightMode_state);
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    sharedpref.setNightModeState(true);
                    restartApp();
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.night_modeOn),
                            Toast.LENGTH_LONG).show();

                } else if (which == 1) {
                    sharedpref.setNightModeState(false);
                    restartApp();
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.night_modeOff),
                            Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void restartApp() {
        loadLocale();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        // finish();
    }

    /*@Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(
                    MainActivity.this);
            alert.setTitle(getString(R.string.app_name));
            // alert.setIcon(R.drawable.ic_logout);
            alert.setMessage(getString(R.string.Quit));
            alert.setPositiveButton(getString(R.string.Yes),
                    new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {

                           *//* finish();
                            System.exit(0);*//*
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


            alert.setNeutralButton(getString(R.string.Rate_app),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            final String appName = getPackageName();
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=" + appName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                            }
                        }
                    });
            alert.show();
        }
    }*/
}
