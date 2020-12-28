package com.example.phraseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Authors extends AppCompatActivity {
    private DatabaseHelper DBHelper;
    private SQLiteDatabase Db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);
        DBHelper=new DatabaseHelper(this);
        try {
            Db = DBHelper.getWritableDatabase();
        } catch (SQLException SQLExcept) {
            throw SQLExcept;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        ImageButton authors_button = (ImageButton) findViewById(R.id.authors_button);
        authors_button.setBackground(getDrawable(R.drawable.author_white));
        LinearLayout ContentLayout = (LinearLayout) findViewById(R.id.content_authors);
        //Cursor fastdelete =Db.rawQuery("DELETE FROM Authors WHERE _author_id=7", null);
        //fastdelete.moveToFirst();
        //fastdelete.close();
        Cursor counter = Db.rawQuery("SELECT COUNT(*) FROM Authors", null);
        counter.moveToFirst();
        int size=counter.getInt(0);
        counter.close();
        int[] author_id = new int[size];
        String[] author_name = new String[size];
        String[] born = new String[size];
        String[] dead_day = new String[size];
        int len=0;
        Cursor query = Db.rawQuery("SELECT _author_id, name, born, dead_day FROM Authors;", null);
        if(query.moveToFirst()) {
            int i=0;
            while (!query.isAfterLast()) {
                author_id[i] = query.getInt(0);
                author_name[i] = query.getString(1);
                born[i] = query.getString(2);
                dead_day[i] = query.getString(3);
                len++; i++;
                query.moveToNext();
            }
        }
        query.close();
        int i=0;
        LinearLayout[] ConstructLay = new LinearLayout[len];
        while (i<len) {
            ConstructLay[i] = buildConstructLay(ConstructLay[i]);
            Button AuthorName = buildButton(author_name[i], author_id[i]);
            String timelife = "";
            if (born[i]!=null) {
                timelife+=born[i];
            }
            else { timelife+="..."; }
            if (dead_day[i]!=null) {
                timelife=timelife+" - "+dead_day[i];
            }
            else { timelife=timelife+" - ..."; }
            TextView LifeTime = buildTextView(timelife, 16, 30);
            LifeTime.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ConstructLay[i].addView(AuthorName);
            ConstructLay[i].addView(LifeTime);
            ContentLayout.addView(ConstructLay[i]);
            i++;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LinearLayout ContentLayout= (LinearLayout) findViewById(R.id.content_authors);
        ContentLayout.removeAllViews();
    }

    public void goToBooks(View view) {
        Intent books=new Intent(this, Books.class);
        startActivity(books);
        finish();
    }
    public void goToPhrases(View view) {
        Intent phrases=new Intent(this, MainActivity.class);
        startActivity(phrases);
        finish();
    }
    public void addAuthors(View view) {
        Intent addauthor=new Intent(Authors.this, AddToDB.class);
        addauthor.putExtra("callback",  2);
        startActivity(addauthor);
    }

    View.OnClickListener btn_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int idv = v.getId();
            if (idv < 9999) {
                Intent check = new Intent(Authors.this, CheckView.class);
                check.putExtra("author_id", idv);
                check.putExtra("callback", 2);
                startActivity(check);
            }
        }
    };

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
}