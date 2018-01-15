package com.jookovjook.base.Main;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jookovjook.base.DataProvider.AbstractDataProvider;
import com.jookovjook.base.DataProvider.DataProviderFragment;
import com.jookovjook.base.Database.DatabaseHelper;
import com.jookovjook.base.Dialog2.Dialog2Activity;
import com.jookovjook.base.DraggableSwipeable.DraggableSwipeableFragment;
import com.jookovjook.base.Preferences.AboutActivity;
import com.jookovjook.base.Preferences.PrefActivity;
import com.jookovjook.base.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        VocalizerFragment.onEventListener2, ItemPinnedMessageDialogFragment.EventListener, DraggableSwipeableFragment.fabListener {
    ///first run stuff first run stuff first run stuff
    SharedPreferences prefs = null;
    ///first run stuff first run stuff first run stuff
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    /////new new new/////
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    // variables for "create new card alert dialog"
    AlertDialog.Builder ad;
    Context context;
    /////////////////
    //preferences
    SharedPreferences sp;
    /////////////////
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ///////////////
        // prepare data
        mDatabaseHelper = new DatabaseHelper(MainActivity.this, "database.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        /*
        Cursor cursor = mSqLiteDatabase.query("database", new String[]{DatabaseHelper._ID, DatabaseHelper.TEXT_COLUMN,
                DatabaseHelper.TYPE_COLUMN, DatabaseHelper.SETT1_COLUMN,
                DatabaseHelper.SETT2_COLUMN, DatabaseHelper.SETT3_COLUMN}, null, null, null, null, null);
                */
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TYPE_COLUMN, "0");
        values.put(DatabaseHelper.SETT1_COLUMN, "0");
        mSqLiteDatabase.update("database", values, DatabaseHelper.SETT1_COLUMN + "= ?", new String[]{"1"});
        mSqLiteDatabase.delete("database", DatabaseHelper.TYPE_COLUMN + "= ?", new String[]{"-1"});
        mSqLiteDatabase.delete("database", DatabaseHelper.TYPE_COLUMN + "= ?", new String[]{"-2"});
        ///close drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        /////preferences
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ////////////////
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ///first run stuff first run stuff first run stuff
        prefs = getSharedPreferences("com.jookovjook.base", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            CreateDatabase();
            prefs.edit().putBoolean("firstrun", false).commit();
        }
        ///first run stuff first run stuff first run stuff
        /////////////
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(new DataProviderFragment(), FRAGMENT_TAG_DATA_PROVIDER)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DraggableSwipeableFragment(), FRAGMENT_LIST_VIEW)
                    .commit();
        }
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = sp.getString("PREF_LANG", "0");
        int lang_int = Integer.parseInt(lang);
        String voice = sp.getString("PREF_VOICE","0");
        int voice_int = Integer.parseInt(voice);
        VocalizerFragment voc_frag = (VocalizerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        voc_frag.setVOICE(voice_int);
        voc_frag.setLANG(lang_int);
        flag = false;
    }

    protected void onResume() {
        //обновление настроек
        String lang = sp.getString("PREF_LANG", "0");
        int lang_int = Integer.parseInt(lang);
        String voice = sp.getString("PREF_VOICE","0");
        int voice_int = Integer.parseInt(voice);
        VocalizerFragment voc_frag = (VocalizerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        voc_frag.setVOICE(voice_int);
        voc_frag.setLANG(lang_int);
        //
        //flag = false;
        if(flag) {
            mDatabaseHelper = new DatabaseHelper(MainActivity.this, "database.db", null, 1);
            mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
            Cursor cursor = mSqLiteDatabase.query("database", new String[]{DatabaseHelper._ID, DatabaseHelper.TEXT_COLUMN,
                    DatabaseHelper.TYPE_COLUMN, DatabaseHelper.SETT1_COLUMN,
                    DatabaseHelper.SETT2_COLUMN, DatabaseHelper.SETT3_COLUMN}, null, null, null, null, null);
            cursor.moveToFirst();
            int count = 0;
            while (cursor.moveToNext()) {
                String txt = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TEXT_COLUMN));
                int sett1 = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SETT1_COLUMN));
                if (sett1 == 1) {
                    getDataProvider().addItem(txt);
                    count = count + 1;
                    //*
                    //ContentValues values = new ContentValues();
                    //values.put(DatabaseHelper.TEXT_COLUMN, txt);
                    //values.put(DatabaseHelper.TYPE_COLUMN, "0");
                    //values.put(DatabaseHelper.SETT1_COLUMN, "0");
                    //values.put(DatabaseHelper.SETT1_COLUMN, "0");
                    //values.put(DatabaseHelper.SETT2_COLUMN, "0");
                    //values.put(DatabaseHelper.SETT3_COLUMN, "0");
                    //*/
                    //mSqLiteDatabase.insert("database", null, values);
                    //cursor.moveToPrevious();
                    //ContentValues values = new ContentValues();
                    //mSqLiteDatabase.update("database", values, mDatabaseHelper._ID + "= ?", new String[]{Integer.toString(_id)});
                }

            }
            cursor.close();
            if(count > 0) {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.SETT1_COLUMN, "-1");
                mSqLiteDatabase.update("database", values, DatabaseHelper.SETT1_COLUMN + "= ?", new String[]{"1"});
                //mSqLiteDatabase.execSQL("UPDATE databse SET SETT1_COLUMN='0' WHERE[]");
                //mSqLiteDatabase.update("database", DatabaseHelper.TYPE_COLUMN + "=?", new String[] { "-1" });
                int pos_new = getDataProvider().getCount();
                final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
                ((DraggableSwipeableFragment) fragment).notifyItemInserted(pos_new - 1);
            }
            flag = false;
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*
        if (id == R.id.DB_settings) {
            Intent iinent= new Intent(MainActivity.this,DatabaseManager.class);
            startActivity(iinent);
            return true;
        }
        */
        if (id == R.id.show_dialog){
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), Dialog2Activity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            flag = true;
            return true;
        }
        if (id == R.id.main_yandex){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(getResources().getString(R.string.yandex_hyperlink)));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dialog) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), Dialog2Activity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            flag = true;
        } else if (id == R.id.nav_cards) {

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), PrefActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        } else if (id == R.id.nav_about){
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), AboutActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        } else if (id == R.id.nav_add) {
            ShowNewCardAlert();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This method will be called when a list item is removed
     *
     * @param position The position of the item within data set
     */
    public void onItemRemoved(int position) {
        Snackbar snackbar = Snackbar.make(
                findViewById(R.id.container),
                R.string.snack_bar_text_item_removed,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.snack_bar_action_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemUndoActionClicked();
            }
        });
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.snackbar_action_color_done));
        snackbar.show();
    }

    /**
     * This method will be called when a list item is pinned
     *
     * @param position The position of the item within data set
     */
    public void onItemPinned(int position) {
        //final DialogFragment dialog = ItemPinnedMessageDialogFragment.newInstance(position);
        //getSupportFragmentManager().beginTransaction().add(dialog, FRAGMENT_TAG_ITEM_PINNED_DIALOG).commit();
        final int pos = position;
        context = MainActivity.this;
        String title = getResources().getString(R.string.main_alert_edit_title);
        String buttonOK = getResources().getString(R.string.main_alert_edit_OK);
        String buttonCancel = getResources().getString(R.string.main_alert_edit_cancel);
        //final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        AbstractDataProvider.Data data = getDataProvider().getItem(pos);
        String text = data.getText();
        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        final EditText input = new EditText(context);
        input.setText(text);
        //input.setSelection(input.getText().length());
        //input.setSelectAllOnFocus(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        ad.setView(input);
        ad.setPositiveButton(buttonOK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                getDataProvider().editItem(pos, input.getText().toString());
                Toast.makeText(context, getResources().getString(R.string.main_toast_edited), Toast.LENGTH_LONG).show();
                //unPin
                final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
                getDataProvider().getItem(pos).setPinned(false);
                ((DraggableSwipeableFragment) fragment).notifyItemChanged(pos);
            }
        });
        ad.setNegativeButton(buttonCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                //upPin
                final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
                getDataProvider().getItem(pos).setPinned(false);
                ((DraggableSwipeableFragment) fragment).notifyItemChanged(pos);
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                //upPin
                final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
                getDataProvider().getItem(pos).setPinned(false);
                ((DraggableSwipeableFragment) fragment).notifyItemChanged(pos);
            }
        });
        ad.show();
        input.requestFocus(input.getText().length());
    }

    /**
     * This method will be called when a list item is clicked
     *
     * @param position The position of the item within data set
     */
    public void onItemClicked(int position) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        AbstractDataProvider.Data data = getDataProvider().getItem(position);
        String txt = data.getText();
        VocalizerFragment voc_frag = (VocalizerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        voc_frag.Vocalize(txt);

        if (data.isPinned()) {
            // unpin if tapped the pinned item
            data.setPinned(false);
            ((DraggableSwipeableFragment) fragment).notifyItemChanged(position);
        }
    }

    private void onItemUndoActionClicked() {
        int position = getDataProvider().undoLastRemoval();
        if (position >= 0) {
            final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
            ((DraggableSwipeableFragment) fragment).notifyItemInserted(position);
        }
    }

    // implements ItemPinnedMessageDialogFragment.EventListener
    @Override
    public void onNotifyItemPinnedDialogDismissed(int itemPosition, boolean ok) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        getDataProvider().getItem(itemPosition).setPinned(ok);
        ((DraggableSwipeableFragment) fragment).notifyItemChanged(itemPosition);
    }

    public AbstractDataProvider getDataProvider() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        return ((DataProviderFragment) fragment).getDataProvider();
    }

    //метод для создания базы данных при первом запуске
    public void CreateDatabase(){
        //Создание базы данных//Создание базы данных//Создание базы данных
        mDatabaseHelper = new DatabaseHelper(this, "database.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TEXT_COLUMN, "");
        values.put(DatabaseHelper.TYPE_COLUMN, "0");
        values.put(DatabaseHelper.SETT1_COLUMN, "0");
        values.put(DatabaseHelper.SETT1_COLUMN, "0");
        values.put(DatabaseHelper.SETT2_COLUMN, "0");
        values.put(DatabaseHelper.SETT3_COLUMN, "0");
        mSqLiteDatabase.insert("database", null, values);
        mDatabaseHelper = new DatabaseHelper(this, "database.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_1));
        values.put(DatabaseHelper.SETT2_COLUMN, "1");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_2));
        values.put(DatabaseHelper.SETT2_COLUMN, "2");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_3));
        values.put(DatabaseHelper.SETT2_COLUMN, "3");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_4));
        values.put(DatabaseHelper.SETT2_COLUMN, "4");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_5));
        values.put(DatabaseHelper.SETT2_COLUMN, "5");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_6));
        values.put(DatabaseHelper.SETT2_COLUMN, "6");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_7));
        values.put(DatabaseHelper.SETT2_COLUMN, "7");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_8));
        values.put(DatabaseHelper.SETT2_COLUMN, "8");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_9));
        values.put(DatabaseHelper.SETT2_COLUMN, "9");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_10));
        values.put(DatabaseHelper.SETT2_COLUMN, "10");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_11));
        values.put(DatabaseHelper.TYPE_COLUMN, "2");
        values.put(DatabaseHelper.SETT2_COLUMN, "11");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_12));
        values.put(DatabaseHelper.TYPE_COLUMN, "1");
        values.put(DatabaseHelper.SETT2_COLUMN, "12");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_13));
        values.put(DatabaseHelper.TYPE_COLUMN, "2");
        values.put(DatabaseHelper.SETT2_COLUMN, "13");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_14));
        values.put(DatabaseHelper.TYPE_COLUMN, "1");
        values.put(DatabaseHelper.SETT2_COLUMN, "14");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_15));
        values.put(DatabaseHelper.TYPE_COLUMN, "1");
        values.put(DatabaseHelper.SETT2_COLUMN, "15");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_16));
        values.put(DatabaseHelper.TYPE_COLUMN, "2");
        values.put(DatabaseHelper.SETT2_COLUMN, "16");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_17));
        values.put(DatabaseHelper.TYPE_COLUMN, "2");
        values.put(DatabaseHelper.SETT2_COLUMN, "17");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_18));
        values.put(DatabaseHelper.TYPE_COLUMN, "2");
        values.put(DatabaseHelper.SETT2_COLUMN, "18");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_19));
        values.put(DatabaseHelper.TYPE_COLUMN, "2");
        values.put(DatabaseHelper.SETT2_COLUMN, "19");
        mSqLiteDatabase.insert("database", null, values);
        values.put(DatabaseHelper.TEXT_COLUMN, getResources().getString(R.string.main_db_20));
        values.put(DatabaseHelper.TYPE_COLUMN, "1");
        values.put(DatabaseHelper.SETT2_COLUMN, "20");
        mSqLiteDatabase.insert("database", null, values);
    }

    //метод создания alertDialog для создания новой карточки
    public void ShowNewCardAlert(){
        context = MainActivity.this;
        String title = getResources().getString(R.string.main_alert_create_title);
        String buttonOK = getResources().getString(R.string.main_alert_create_OK);
        String buttonCancel = getResources().getString(R.string.main_alert_create_cancel);

        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        ad.setView(input);
        ad.setPositiveButton(buttonOK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                String new_text = input.getText().toString();
                CreateNewCard(new_text);
            }
        });
        ad.setNegativeButton(buttonCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        ad.setCancelable(true);
        ad.show();
    }

    @Override
    public void fabPressed() {
        ShowNewCardAlert();
    }

    @Override
    public void SendError2(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
    }

    public void CreateNewCard(String s){
        getDataProvider().addItem(s);
        int pos_new = getDataProvider().getCount();
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        getDataProvider().getItem(pos_new-1).setPinned(false);
        ((DraggableSwipeableFragment) fragment).notifyItemInserted(pos_new-1);
        Toast.makeText(context, getResources().getString(R.string.main_toast_created), Toast.LENGTH_LONG).show();
    }
}
