package com.example.phraseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper DBHelper;
    private SQLiteDatabase Db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHelper=new DatabaseHelper(this);
        try {
            DBHelper.updateDataBase();
        } catch (IOException except){
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            Db = DBHelper.getWritableDatabase();
        } catch (SQLException SQLExcept) {
            throw SQLExcept;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageButton phrases_button = findViewById(R.id.phrase_btn);
        phrases_button.setBackground(getDrawable(R.drawable.message_white));
        Cursor counter = Db.rawQuery("SELECT COUNT(*) FROM Phrases", null);
        counter.moveToFirst();
        int size=counter.getInt(0);
        counter.close();
        int[] phrase_id = new int[size];
        String[] phrase_name = new String[size];
        String[] phrase_author = new String[size];
        String[] phrase_text = new String[size];
        int len=0;
        Cursor query = Db.rawQuery("SELECT ph._phrase_id, ph.name, au.name, ph.txt_phrase FROM Phrases ph\n" +
                "INNER JOIN Authors au ON ph._author_id=au._author_id;", null);
        if(query.moveToFirst()) {
            int i=0;
            while (!query.isAfterLast()) {
                phrase_id[i] = query.getInt(0);
                phrase_name[i] = query.getString(1);
                phrase_author[i] = query.getString(2);
                phrase_text[i] = query.getString(3);
                len++; i++;
                query.moveToNext();
            }
        }
        query.close();
        LinearLayout ContentLayout= (LinearLayout) findViewById(R.id.content_phrases);
        int i=0;
        LinearLayout[] ConstructLay = new LinearLayout[len];
        while (i<len) {
            ConstructLay[i] = buildConstructLay(ConstructLay[i]);
            Button PhraseName = buildButton(phrase_name[i], phrase_id[i]);
            TextView AuthorName = buildTextView(phrase_author[i], 14, 30);
            TextView TextPhrase = buildTextView(phrase_text[i], 12, 96);
            TextPhrase.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ConstructLay[i].addView(PhraseName);
            ConstructLay[i].addView(AuthorName);
            ConstructLay[i].addView(TextPhrase);
            ContentLayout.addView(ConstructLay[i]);
            i++;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        LinearLayout ContentLayout= (LinearLayout) findViewById(R.id.content_phrases);
        ContentLayout.removeAllViews();
    }

    public void goToAuthors(View view) {
        Intent authors=new Intent(MainActivity.this, Authors.class);
        startActivity(authors);

    }
    public void goToBooks(View view) {
        Intent books=new Intent(MainActivity.this, Books.class);
        startActivity(books);
    }

    public void addPhrases(View view) {
        Intent addphrase=new Intent(MainActivity.this, AddToDB.class);
        addphrase.putExtra("callback",  1);
        startActivity(addphrase);
    }

    protected LinearLayout buildConstructLay (LinearLayout l) {
        l = new LinearLayout(this);
        LinearLayout.LayoutParams LayParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LayParams.setMargins(128,64,128,64);
        l.setLayoutParams(LayParams);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackgroundColor(Color.parseColor("#2A2C33"));
        return l;
    }

    protected Button buildButton (String text, int id) {
        Button b = new Button(this);
        b.setText(text);
        b.setId(id);
        b.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        b.setTextColor(Color.WHITE);
        b.setTextSize(16);
        LinearLayout.LayoutParams BtnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        BtnParams.setMargins(24,24,24,16);
        b.setLayoutParams(BtnParams);
        b.setBackgroundColor(Color.parseColor("#EE7B7E"));
        b.setOnClickListener(btn_click);
        return b;
    }

    protected TextView buildTextView (String text, int fontsize, int max_len) {
        TextView t = new TextView(this);
        t.setFilters(new InputFilter[] {new InputFilter.LengthFilter(max_len)});
        LinearLayout.LayoutParams TxtViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TxtViewParams.setMargins(24,16,24,8);
        t.setLayoutParams(TxtViewParams);
        t.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        t.setTextColor(Color.WHITE);
        t.setTextSize(fontsize);
        if (text.length()>max_len) {
            text= text.substring(0, max_len-3);
            text+="...";
        }
        t.setText(text);
        return t;
    }

    View.OnClickListener btn_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int idv = v.getId();
            if (idv < 9999) {
                Intent check = new Intent(MainActivity.this, CheckView.class);
                check.putExtra("phrase_id", idv);
                check.putExtra("callback", 1);
                startActivity(check);
            }
        }
    };
}