package com.jookovjook.base.Dialog2;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.jookovjook.base.Database.DatabaseHelper;
import com.jookovjook.base.Dialog2.Dialog2RecognizerFragment.onEventListener;
import com.jookovjook.base.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dialog2Activity extends AppCompatActivity implements onEventListener,
        Dialog2VocalizerFragment.onEventListener2 {

    private List<Dialog2Provider> dialog2ProviderList = new ArrayList<>();
    private Dialog2Adapter mAdapter;
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    //private int fab_flag;
    private ImageButton btnMic;
    private ImageView back_circle, front_circle, front_circle_shaddow, back_circle_shaddow;
    //for animations
    private Animation mRotateRight, mRotateLeft1, mEnlarge, mRotateLeft2;
    private Animation r_mRotateRight, r_mRotateLeft1, r_mEnlarge, r_mRotateLeft2;
    private ImageView mLine;
    private TextView up_text, down_text, up_text_2, down_text_2, up_text_r, down_text_r;
    private boolean recording, full_screen;
    private LinearLayout full_screen_layout;
    SharedPreferences sp;
    //full_screen_button_animations
    private AnimationSet anim_fp1_b, anim_fp4_b;
    private AnimationSet anim_fp1_f, anim_fp4_f;
    private Animation anim_fp2_b, anim_fp3_b, anim_fp5_b;
    private Animation anim_fp2_f, anim_fp3_f, anim_fp5_f;
    private ImageView fp1, fp2, fp3, fp4, fp5, fsb_back, view_agenda, forum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog2_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //подключение RecyclerView и Adapter
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new Dialog2Adapter(dialog2ProviderList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        assert recyclerView != null;
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        //Вставка данных
        prepareData();
        //создание ивентов ввода текста
        full_screen = false;
        final EditText edit_text = (EditText) findViewById(R.id.edit_text);
        assert edit_text != null;
        edit_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!full_screen) {
                    StopRecording();
                }
            }
        });
        //disable fullscreen on start
        full_screen_layout = (LinearLayout) findViewById(R.id.full_screen_layout);
        assert full_screen_layout != null;
        full_screen_layout.setVisibility(View.GONE);
        down_text = (TextView) findViewById(R.id.down_text);
        down_text_2 = (TextView) findViewById(R.id.down_text_2);
        down_text_r = (TextView) findViewById(R.id.down_text_r);
        TextWatcher watcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                assert down_text != null;
                if(full_screen) {
                    down_text.setText(edit_text.getText().toString());
                    down_text_r.setText(edit_text.getText().toString());
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        };
        edit_text.addTextChangedListener(watcher);
        up_text = (TextView) findViewById(R.id.up_text);
        up_text_2 = (TextView) findViewById(R.id.up_text_2);
        up_text_r = (TextView) findViewById(R.id.up_text_r);
        //Вставка текста по вводу
        ImageButton button_send = (ImageButton) findViewById(R.id.button_send);
        assert button_send != null;
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = edit_text.getText().toString();
                Dialog2Provider aDA;
                //get _id of the last element
                mDatabaseHelper = new DatabaseHelper(Dialog2Activity.this, "database.db", null, 1);
                mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                Cursor cursor = mSqLiteDatabase.query("database", new String[]{DatabaseHelper._ID, DatabaseHelper.TEXT_COLUMN,
                        DatabaseHelper.TYPE_COLUMN, DatabaseHelper.SETT1_COLUMN,
                        DatabaseHelper.SETT2_COLUMN, DatabaseHelper.SETT3_COLUMN}, null, null, null, null, null);
                cursor.moveToLast();
                //cursor.moveToNext();
                int _id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
                cursor.close();
                //
                aDA = new Dialog2Provider(text, 1, _id + 1);
                dialog2ProviderList.add(0, aDA);
                mAdapter.notifyDataSetChanged();
                if(full_screen) down_text_2.setText(edit_text.getText().toString());
                edit_text.setText("");
                if(!full_screen) {
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    edit_text.clearFocus();
                }
                mDatabaseHelper = new DatabaseHelper(Dialog2Activity.this, "database.db", null, 1);
                mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.TEXT_COLUMN, text);
                values.put(DatabaseHelper.TYPE_COLUMN, "1");
                values.put(DatabaseHelper.SETT1_COLUMN, "0");
                values.put(DatabaseHelper.SETT1_COLUMN, "0");
                values.put(DatabaseHelper.SETT2_COLUMN, "0");
                values.put(DatabaseHelper.SETT3_COLUMN, "0");
                mSqLiteDatabase.insert("database", null, values);
            }
        });
        //preferences
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = sp.getString("PREF_LANG", "0");
        int lang_int = Integer.parseInt(lang);
        Dialog2RecognizerFragment rec_frag = (Dialog2RecognizerFragment) getSupportFragmentManager().findFragmentById(R.id.dialog2_fragment_recognizer);
        rec_frag.setLANG(lang_int);
        String voice = sp.getString("PREF_VOICE","0");
        int voice_int = Integer.parseInt(voice);
        Dialog2VocalizerFragment voc_frag = (Dialog2VocalizerFragment) getSupportFragmentManager().findFragmentById(R.id.dialog2_fragment_vocalizer);
        voc_frag.setVOICE(voice_int);
        voc_frag.setLANG(lang_int);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dialog2_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem actionViewItem = menu.findItem(R.id.mic_item);
        View v = MenuItemCompat.getActionView(actionViewItem);
        btnMic = (ImageButton) v.findViewById(R.id.btnMic);
        mLine = (ImageView) v.findViewById(R.id.line);
        mLine.setImageResource(R.drawable.dialog2_cross_mic_line_transparent);
        PrepareAnimations();
        recording = false;
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!recording) { StartRecording(); } else { StopRecording(); }
            }
        });
        back_circle = (ImageView) v.findViewById(R.id.back_circle);
        front_circle = (ImageView) v.findViewById(R.id.front_circle);
        front_circle_shaddow = (ImageView) v.findViewById(R.id.front_circle_shaddow);
        back_circle_shaddow = (ImageView) v.findViewById(R.id.back_circle_shaddow);
        //full_screen_item
        MenuItem fsViewItem = menu.findItem(R.id.full_screen);
        View v2 = MenuItemCompat.getActionView(fsViewItem);
        fp1 = (ImageView) v2.findViewById(R.id.forum_patch_1);
        fp2 = (ImageView) v2.findViewById(R.id.forum_patch_2);
        fp3 = (ImageView) v2.findViewById(R.id.forum_patch_3);
        fp4 = (ImageView) v2.findViewById(R.id.forum_patch_4);
        fp5 = (ImageView) v2.findViewById(R.id.forum_patch_5);
        ImageButton fs_btn = (ImageButton) v2.findViewById(R.id.fs_button);
        fsb_back = (ImageView) v2.findViewById(R.id.fsb_back);
        view_agenda = (ImageView) v2.findViewById(R.id.view_agenda);
        forum = (ImageView) v2.findViewById(R.id.forum);
        //prepare full_screen button animations
        PrepareFSBAnimations();
        fs_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(full_screen){
                    //menuItem.setIcon(R.drawable.dialog2_vector_drawable_ic_view_agenda_white___px);
                    HideFullScreen();
                    fsb_back.setAlpha((float) 0.0);
                    forum.setAlpha((float) 0.0);
                    fp1.startAnimation(anim_fp1_b);
                    fp2.startAnimation(anim_fp2_b);
                    fp3.startAnimation(anim_fp3_b);
                    fp4.startAnimation(anim_fp4_b);
                    fp5.startAnimation(anim_fp5_b);
                }else{
                    //menuItem.setIcon(R.drawable.dialog2_vector_drawable_ic_forum_white___px);
                    ShowFullScreen();
                    fsb_back.setAlpha((float) 0.0);
                    view_agenda.setAlpha((float) 0.0);
                    fp1.startAnimation(anim_fp1_f);
                    fp2.startAnimation(anim_fp2_f);
                    fp3.startAnimation(anim_fp3_f);
                    fp4.startAnimation(anim_fp4_f);
                    fp5.startAnimation(anim_fp5_f);
                }
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mic_item) {
            return true;
        }
        if (id == R.id.full_screen){
            return true;
        }
        if (id == R.id.dialog2_yandex){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(getResources().getString(R.string.yandex_hyperlink)));
            startActivity(intent);
        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //вставка данных
    private void prepareData() {
        Dialog2Provider aDA;
        mDatabaseHelper = new DatabaseHelper(this, "database.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        Cursor cursor = mSqLiteDatabase.query("database", new String[]{DatabaseHelper._ID, DatabaseHelper.TEXT_COLUMN,
                DatabaseHelper.TYPE_COLUMN, DatabaseHelper.SETT1_COLUMN,
                DatabaseHelper.SETT2_COLUMN, DatabaseHelper.SETT3_COLUMN}, null, null, null, null, null);
        cursor.moveToLast();
        cursor.moveToNext();
        while (cursor.moveToPrevious()){
            String text = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TEXT_COLUMN));
            int _id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            int type = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TYPE_COLUMN));
            int sett_2 = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SETT2_COLUMN));
            if(type == 1 || type == 2){
                switch (sett_2){
                    case 11:
                        text = getResources().getString(R.string.main_db_11);
                        break;
                    case 12:
                        text = getResources().getString(R.string.main_db_12);
                        break;
                    case 13:
                        text = getResources().getString(R.string.main_db_13);
                        break;
                    case 14:
                        text = getResources().getString(R.string.main_db_14);
                        break;
                    case 15:
                        text = getResources().getString(R.string.main_db_15);
                        break;
                    case 16:
                        text = getResources().getString(R.string.main_db_16);
                        break;
                    case 17:
                        text = getResources().getString(R.string.main_db_17);
                        break;
                    case 18:
                        text = getResources().getString(R.string.main_db_18);
                        break;
                    case 19:
                        text = getResources().getString(R.string.main_db_19);
                        break;
                    case 20:
                        text = getResources().getString(R.string.main_db_20);
                        break;
                }
                aDA = new Dialog2Provider(text, type, _id);
                dialog2ProviderList.add(aDA);
            }
        }
        cursor.close();
        mAdapter.notifyDataSetChanged();
    }

    //методы, вызываемые при нажатии на кнопки карточек
    public void addCardMethod(String s){
        DatabaseHelper mDatabaseHelper2 = new DatabaseHelper(Dialog2Activity.this,
                "database.db", null, 1);
        SQLiteDatabase mSqLiteDatabase2 = mDatabaseHelper2.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TEXT_COLUMN, s);
        values.put(DatabaseHelper.TYPE_COLUMN, "-2");
        values.put(DatabaseHelper.SETT1_COLUMN, "1");
        values.put(DatabaseHelper.SETT2_COLUMN, "0");
        values.put(DatabaseHelper.SETT3_COLUMN, "0");
        mSqLiteDatabase2.insert("database", null, values);
        Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.dialog2_cpu_add_alert), Toast.LENGTH_LONG).show();
    }

    public void copyMethod(String s){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", s);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.dialog2_cpu_copy_alert),
                Toast.LENGTH_SHORT).show();
    }

    public void deleteMethod(int _id){
        Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.dialog2_cpu_del_alert),
                Toast.LENGTH_SHORT).show();
        mDatabaseHelper = new DatabaseHelper(Dialog2Activity.this, "database.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TYPE_COLUMN, "-2");
        mSqLiteDatabase.update("database", values, "_id = ?", new String[] {Integer.toString(_id)});
    }

    public void playMethod(String s){
        Dialog2VocalizerFragment voc_frag = (Dialog2VocalizerFragment)
                getSupportFragmentManager().findFragmentById(R.id.dialog2_fragment_vocalizer);
        voc_frag.Vocalize(s);
    }

    @Override
    public void SendResult(String s) {
        final EditText edit_text = (EditText) findViewById(R.id.edit_text);
        assert edit_text != null;
        if(full_screen) up_text.setText("");
        if(full_screen) up_text_2.setText(s);
        if(!full_screen) edit_text.setText(s);
        //get _id of the last element
        mDatabaseHelper = new DatabaseHelper(Dialog2Activity.this, "database.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        Cursor cursor = mSqLiteDatabase.query("database", new String[]{DatabaseHelper._ID,
                DatabaseHelper.TEXT_COLUMN,
                DatabaseHelper.TYPE_COLUMN, DatabaseHelper.SETT1_COLUMN,
                DatabaseHelper.SETT2_COLUMN, DatabaseHelper.SETT3_COLUMN}, null, null, null, null,
                null);
        cursor.moveToLast();
        //cursor.moveToNext();
        int _id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
        cursor.close();
        //вывод данных на экран через провайдер
        Dialog2Provider aDA;
        aDA = new Dialog2Provider(s, 2, _id + 1);
        dialog2ProviderList.add(0,aDA);
        mAdapter.notifyDataSetChanged();
        //вставка записанных данных
        mDatabaseHelper = new DatabaseHelper(Dialog2Activity.this, "database.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TEXT_COLUMN, s);
        values.put(DatabaseHelper.TYPE_COLUMN, "2");
        values.put(DatabaseHelper.SETT1_COLUMN, "0");
        values.put(DatabaseHelper.SETT1_COLUMN, "0");
        values.put(DatabaseHelper.SETT2_COLUMN, "0");
        values.put(DatabaseHelper.SETT3_COLUMN, "0");
        mSqLiteDatabase.insert("database", null, values);
        //возвращение элементов интерфейса к начальным
        if(!full_screen) {
            edit_text.setText("");
            StopRecording();
        }
        back_circle.setScaleX((float) 0.749);
        back_circle.setScaleY((float) 0.749);
        back_circle_shaddow.setScaleX((float) 0.749);
        back_circle_shaddow.setScaleY((float) 0.749);
        front_circle.setScaleX((float) 1.0);
        front_circle.setScaleY((float) 1.0);
        front_circle_shaddow.setScaleX((float) 1.0);
        front_circle_shaddow.setScaleY((float) 1.0);
    }

    @Override
    public void SendPartitialResults(String s) {
        final EditText edit_text = (EditText) findViewById(R.id.edit_text);
        assert edit_text != null;
        //View view = findViewById(R.id.dialog2_activity);
        //
        //if(!full_screen) {
        //    if (view != null) {
        //        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        //    }
        //    edit_text.clearFocus();
        //}
        //
        if (!full_screen) {
            edit_text.setText(s);
        } else {
            up_text.setText(s);
            up_text_r.setText(s);
        }
    }

    @Override
    public void SendPower(float v) {
        back_circle.setScaleX((float) (0.8+1.75*0.251*Math.sqrt(v)));
        back_circle.setScaleY((float) (0.8+1.75*0.251*Math.sqrt(v)));
        back_circle_shaddow.setScaleX((float) (0.8+1.75*0.251*Math.sqrt(v)));
        back_circle_shaddow.setScaleY((float) (0.8+1.75*0.251*Math.sqrt(v)));
        front_circle.setScaleX((float) (0.9 - 0.5*v));
        front_circle.setScaleY((float) (0.9 - 0.5*v));
        front_circle_shaddow.setScaleX((float) (0.9 - 0.5*v));
        front_circle_shaddow.setScaleY((float) (0.9 - 0.5*v));
    }

    @Override
    public void SendError(String s) {
        Toast.makeText(Dialog2Activity.this, s, Toast.LENGTH_LONG).show();
        if(Objects.equals(s, getResources().getString(R.string.error_network))){
            StopRecording();
        }
    }

    //анимации
    private void PrepareAnimations(){
        //начальный скейл линии
        Animation mShrink = new ScaleAnimation(1, 0, 1, 1);
        mShrink.setDuration(0);
        mShrink.setFillAfter(true);
        mLine.startAnimation(mShrink);
        //анимации
        mRotateRight = AnimationUtils.loadAnimation(this, R.anim.rotate_right);
        mRotateLeft1 = AnimationUtils.loadAnimation(this, R.anim.rotate_left1);
        mRotateLeft2 = AnimationUtils.loadAnimation(this, R.anim.rotate_left2);
        mEnlarge = AnimationUtils.loadAnimation(this, R.anim.enlarge);
        //r анимации
        r_mRotateRight = AnimationUtils.loadAnimation(this, R.anim.r_rotate_right);
        r_mRotateLeft1 = AnimationUtils.loadAnimation(this, R.anim.r_rotate_left1);
        r_mRotateLeft2 = AnimationUtils.loadAnimation(this, R.anim.r_rotate_left2);
        r_mEnlarge = AnimationUtils.loadAnimation(this, R.anim.r_enlarge);
        //листенеры
        mRotateRight.setAnimationListener(RotateRightListener);
        mRotateLeft1.setAnimationListener(RotateLeft1Listener);
        mRotateLeft2.setAnimationListener(RotateLeft2Listener);
        mEnlarge.setAnimationListener(EnlargeListener);
        //r листненеры
        r_mRotateRight.setAnimationListener(r_RotateRightListener);
        r_mRotateLeft1.setAnimationListener(r_RotateLeft1Listener);
        r_mRotateLeft2.setAnimationListener(r_RotateLeft2Listener);
        r_mEnlarge.setAnimationListener(r_EnlargeListener);
        //fix state
        mRotateRight.setFillAfter(true);
        mRotateLeft1.setFillAfter(true);
        mRotateLeft2.setFillAfter(true);
        mEnlarge.setFillAfter(true);
        //fix state reverse
        r_mRotateRight.setFillAfter(true);
        r_mRotateLeft1.setFillAfter(true);
        r_mRotateLeft2.setFillAfter(true);
        r_mEnlarge.setFillAfter(true);
        //

    }

    private void PrepareFSBAnimations(){
        //prepare animations
        AnimationSet anim_fp1_p = new AnimationSet(true);
        anim_fp1_p.addAnimation(new TranslateAnimation(0, dpToPxFloat(-4.5), 0, dpToPxFloat(0)) );
        anim_fp1_p.addAnimation(new ScaleAnimation(1, (float) 1.2, 1, (float) 0.62) );
        anim_fp1_p.setDuration(0);
        anim_fp1_p.setFillAfter(true);

        Animation anim_fp2_p = new AlphaAnimation(1, 0);
        anim_fp2_p.setDuration(0);
        anim_fp2_p.setFillAfter(true);

        Animation anim_fp3_p = new ScaleAnimation(1, 1, 1, 0);
        anim_fp3_p.setDuration(0);
        anim_fp3_p.setFillAfter(true);

        AnimationSet anim_fp4_p = new AnimationSet(true);
        anim_fp4_p.addAnimation(new TranslateAnimation(0, dpToPxFloat(-4.5), 0, dpToPxFloat(14.3)) );
        anim_fp4_p.addAnimation(new ScaleAnimation(1, (float) 1.2, 1, (float) 0.62) );
        anim_fp4_p.setDuration(0);
        anim_fp4_p.setFillAfter(true);

        Animation anim_fp5_p = new AlphaAnimation(1, 0);
        anim_fp5_p.setDuration(0);
        anim_fp5_p.setFillAfter(true);

        fp1.startAnimation(anim_fp1_p);
        fp2.startAnimation(anim_fp2_p);
        fp3.startAnimation(anim_fp3_p);
        fp4.startAnimation(anim_fp4_p);
        fp5.startAnimation(anim_fp5_p);

        //forward animations
        anim_fp1_f = new AnimationSet(true);
        anim_fp1_f.addAnimation(new TranslateAnimation(dpToPxFloat(-4.5), dpToPxFloat(-4.1), dpToPxFloat(0), dpToPxFloat(-4.1)) );
        anim_fp1_f.addAnimation(new ScaleAnimation((float) 1.2, (float)0.95, (float) 0.62, (float)0.95) );
        anim_fp1_f.setDuration(100);
        anim_fp1_f.setStartTime(50);
        anim_fp1_f.setFillAfter(true);

        anim_fp2_f = new AlphaAnimation(0, 1);
        anim_fp2_f.setDuration(50);
        anim_fp1_f.setStartTime(100);
        anim_fp2_f.setFillAfter(true);

        anim_fp3_f = new ScaleAnimation(1, (float)0.95, 0, (float)0.95);
        anim_fp3_f.setDuration(100);
        anim_fp3_f.setFillAfter(true);
        anim_fp3_f.setAnimationListener(enter_fs_Listener);

        anim_fp4_f = new AnimationSet(true);
        anim_fp4_f.addAnimation(new TranslateAnimation(dpToPxFloat(-4.5), 0, dpToPxFloat(14.3), 0) );
        anim_fp4_f.addAnimation(new ScaleAnimation((float) 1.2, 1, (float) 0.62, 1) );
        anim_fp4_f.setDuration(100);
        anim_fp1_f.setStartTime(50);
        anim_fp4_f.setFillAfter(true);

        anim_fp5_f = new AlphaAnimation(0, 1);
        anim_fp5_f.setDuration(50);
        anim_fp1_f.setStartTime(100);
        anim_fp5_f.setFillAfter(true);

        //backward animations
        anim_fp1_b = new AnimationSet(true);
        anim_fp1_b.addAnimation(new TranslateAnimation(dpToPxFloat(-4.1), dpToPxFloat(-4.5), dpToPxFloat(-4.1), dpToPxFloat(0)) );
        anim_fp1_b.addAnimation(new ScaleAnimation((float) 0.95, (float) 1.2, (float) 0.95, (float) 0.62) );
        anim_fp1_b.setDuration(100);
        anim_fp1_b.setFillAfter(true);

        anim_fp2_b = new AlphaAnimation(1, 0);
        anim_fp2_b.setDuration(50);
        anim_fp2_b.setFillAfter(true);

        anim_fp3_b = new ScaleAnimation((float)0.95, 1, (float)0.95, 0);
        anim_fp3_b.setDuration(150);
        anim_fp3_b.setFillAfter(true);
        anim_fp1_b.setAnimationListener(exit_fs_Listener);

        anim_fp4_b = new AnimationSet(true);
        anim_fp4_b.addAnimation(new TranslateAnimation(0, dpToPxFloat(-4.5), 0, dpToPxFloat(14.3)) );
        anim_fp4_b.addAnimation(new ScaleAnimation(1, (float) 1.2, 1, (float) 0.62) );
        anim_fp4_b.setDuration(100);
        anim_fp4_b.setFillAfter(true);

        anim_fp5_b = new AlphaAnimation(1, 0);
        anim_fp5_b.setDuration(50);
        anim_fp5_b.setFillAfter(true);

        //fsb_back.setAlpha((float) 0.0);
        //view_agenda.setAlpha((float) 0.0);

        full_screen = false;
    }

    private void StartRecording(){
        btnMic.startAnimation(mRotateRight);
        Dialog2RecognizerFragment rec_frag = (Dialog2RecognizerFragment)
                getSupportFragmentManager().findFragmentById(R.id.dialog2_fragment_recognizer);
        //action after start recording
        //text_state.setText("Recording...");
        rec_frag.createAndStartRecognizer();
        front_circle.setScaleX((float) 0.9);
        front_circle.setScaleY((float) 0.9);
        front_circle_shaddow.setScaleX((float) 0.9);
        front_circle_shaddow.setScaleY((float) 0.9);
        back_circle.setScaleX((float) (0.8));
        back_circle.setScaleY((float) (0.8));
        back_circle_shaddow.setScaleX((float) (0.8));
        back_circle_shaddow.setScaleY((float) (0.8));
        recording = true;
    }

    private void StopRecording(){
        mLine.startAnimation(r_mRotateLeft2);
        r_mRotateLeft2.setFillAfter(true);
        btnMic.startAnimation(r_mRotateLeft1);
        r_mRotateLeft1.setFillAfter(true);
        //action on stop recording
        //text_state.setText("Not Recording.");
        Dialog2RecognizerFragment rec_frag = (Dialog2RecognizerFragment)
                getSupportFragmentManager().findFragmentById(R.id.dialog2_fragment_recognizer);
        rec_frag.stopRecognizer();
        back_circle.setScaleX((float) 0.749);
        back_circle.setScaleY((float) 0.749);
        back_circle_shaddow.setScaleX((float) 0.749);
        back_circle_shaddow.setScaleY((float) 0.749);
        front_circle.setScaleX((float) 1.0);
        front_circle.setScaleY((float) 1.0);
        front_circle_shaddow.setScaleX((float) 1.0);
        front_circle_shaddow.setScaleY((float) 1.0);
        recording = false;
    }

    private void ShowFullScreen(){
        full_screen_layout.setVisibility(View.VISIBLE);
        if (recording) {
            //StopRecording();
            EditText edit_text = (EditText) findViewById(R.id.edit_text);
            assert edit_text != null;
            SendResult(edit_text.getText().toString());
        }
        Dialog2RecognizerFragment rec_frag = (Dialog2RecognizerFragment)
                getSupportFragmentManager().findFragmentById(R.id.dialog2_fragment_recognizer);
        rec_frag.fullScreen(true);
        //apearence animation
        EditText edit_text = (EditText) findViewById(R.id.edit_text);
        assert edit_text != null;
        edit_text.setMaxLines(1);
        ///
        full_screen = true;
    }

    private void HideFullScreen(){
        if(recording) StopRecording();
        Dialog2RecognizerFragment rec_frag = (Dialog2RecognizerFragment)
                getSupportFragmentManager().findFragmentById(R.id.dialog2_fragment_recognizer);
        rec_frag.fullScreen(false);
        EditText edit_text = (EditText) findViewById(R.id.edit_text);
        assert edit_text != null;
        edit_text.setMaxLines(4);
        full_screen_layout.setVisibility(View.GONE);
        full_screen = false;
    }

    //Animation Listeners

    Animation.AnimationListener RotateRightListener = new Animation.AnimationListener(){
        @Override
        public void onAnimationEnd(Animation animation) {
            mLine.setImageResource(R.drawable.dialog2_cross_mic_line);
            mLine.startAnimation(mEnlarge);
            mEnlarge.setFillAfter(true);
        }
        @Override
        public void onAnimationRepeat(Animation animation) {
        }
        @Override
        public void onAnimationStart(Animation animation) {
        }
    };

    Animation.AnimationListener RotateLeft1Listener = new Animation.AnimationListener(){
        @Override
        public void onAnimationEnd(Animation animation) {
        }
        @Override
        public void onAnimationRepeat(Animation animation) {
        }
        @Override
        public void onAnimationStart(Animation animation) {
        }
    };

    Animation.AnimationListener RotateLeft2Listener = new Animation.AnimationListener(){
        @Override
        public void onAnimationEnd(Animation animation) {
        }
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        @Override
        public void onAnimationStart(Animation animation) {
        }
    };

    Animation.AnimationListener EnlargeListener = new Animation.AnimationListener(){
        @Override
        public void onAnimationEnd(Animation animation) {
            btnMic.startAnimation(mRotateLeft1);
            mRotateLeft1.setFillAfter(true);
            mLine.startAnimation(mRotateLeft2);
            mRotateLeft2.setFillAfter(true);
        }
        @Override
        public void onAnimationRepeat(Animation animation) {
        }
        @Override
        public void onAnimationStart(Animation animation) {
        }
    };

    //reverse Animation Listeners

    Animation.AnimationListener r_RotateRightListener = new Animation.AnimationListener(){
        @Override
        public void onAnimationEnd(Animation animation) {
        }
        @Override
        public void onAnimationRepeat(Animation animation) {
        }
        @Override
        public void onAnimationStart(Animation animation) {
        }
    };

    Animation.AnimationListener r_RotateLeft1Listener = new Animation.AnimationListener(){
        @Override
        public void onAnimationEnd(Animation animation) {
            mLine.startAnimation(r_mEnlarge);
            r_mEnlarge.setFillAfter(true);
        }
        @Override
        public void onAnimationRepeat(Animation animation) {
        }
        @Override
        public void onAnimationStart(Animation animation) {
        }
    };

    Animation.AnimationListener r_RotateLeft2Listener = new Animation.AnimationListener(){
        @Override
        public void onAnimationEnd(Animation animation) {
        }
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        @Override
        public void onAnimationStart(Animation animation) {
        }
    };

    Animation.AnimationListener r_EnlargeListener = new Animation.AnimationListener(){
        @Override
        public void onAnimationEnd(Animation animation) {
            mLine.setImageResource(R.drawable.dialog2_cross_mic_line_transparent);
            btnMic.startAnimation(r_mRotateRight);
            r_mRotateRight.setFillAfter(true);
        }
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        @Override
        public void onAnimationStart(Animation animation) {
        }
    };

    //full_screen_listeners

    Animation.AnimationListener exit_fs_Listener = new Animation.AnimationListener(){
        @Override
        public void onAnimationEnd(Animation animation) {
            fsb_back.setAlpha((float) 1.0);
            view_agenda.setAlpha((float) 1.0);
        }
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        @Override
        public void onAnimationStart(Animation animation) {
        }
    };

    Animation.AnimationListener enter_fs_Listener = new Animation.AnimationListener(){
        @Override
        public void onAnimationEnd(Animation animation) {
            fsb_back.setAlpha((float) 1.0);
            forum.setAlpha((float) 1.0);
        }
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        @Override
        public void onAnimationStart(Animation animation) {
        }
    };

    @Override
    public void SendError2(String s) {
        Toast.makeText(Dialog2Activity.this, s, Toast.LENGTH_LONG).show();
    }

    public static float dpToPxFloat(double dp) {
        return (float) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}