package com.example.phraseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Books extends AppCompatActivity {
    private DatabaseHelper DBHelper;
    private SQLiteDatabase Db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
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
        ImageButton phrases_button = (ImageButton) findViewById(R.id.book_btn);
        phrases_button.setBackground(getDrawable(R.drawable.book_white));
        LinearLayout ContentLayout = (LinearLayout) findViewById(R.id.content_books);
        Cursor counter = Db.rawQuery("SELECT COUNT(*) FROM Books", null);
        counter.moveToFirst();
        int size=counter.getInt(0);
        counter.close();
        int[] book_id = new int[size];
        String[] book_name = new String[size];
        String[] author_name = new String[size];
        Bitmap[] book_img = new Bitmap[size];
        int len=0;
        Cursor query = Db.rawQuery("SELECT bo._book_id, bo.name, au.name, bo.img FROM Books bo\n" +
                "INNER JOIN Authors au ON bo._author_id=au._author_id;", null);
        if(query.moveToFirst()) {
            int i = 0;
            while (!query.isAfterLast()) {
                book_id[i] = query.getInt(0);
                book_name[i] = query.getString(1);
                author_name[i] = query.getString(2);
                byte[] imgByte = query.getBlob(3);
                if (imgByte != null) {
                    book_img[i] = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                } else {
                    book_img = null;
                }
                len++;
                i++;
                query.moveToNext();
            }
        }
        query.close();
        int i=0;
        LinearLayout[] ConstructLay = new LinearLayout[len];
        while (i<len) {
            ConstructLay[i] = buildConstructLay(ConstructLay[i]);
            Button BookName = buildButton(book_name[i], book_id[i]);
            TextView AuthorOfBook = buildTextView(author_name[i], 16, 72);
            AuthorOfBook.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            ConstructLay[i].addView(BookName);
            ConstructLay[i].addView(AuthorOfBook);
            ContentLayout.addView(ConstructLay[i]);
            i++;
            }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LinearLayout ContentLayout= (LinearLayout) findViewById(R.id.content_books);
        ContentLayout.removeAllViews();
    }

    public void goToAuthors(View view) {
        Intent authors=new Intent(this, Authors.class);
        startActivity(authors);
        finish();
    }
    public void goToPhrases(View view) {
        Intent phrases=new Intent(this, MainActivity.class);
        startActivity(phrases);
        finish();
    }
    public void addBooks(View view) {
        Intent addbook=new Intent(Books.this, AddToDB.class);
        addbook.putExtra("callback",  3);
        startActivity(addbook);
    }

    View.OnClickListener btn_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int idv = v.getId();
            if (idv < 9999) {
                Intent check = new Intent(Books.this, CheckView.class);
                check.putExtra("book_id", idv);
                check.putExtra("callback", 3);
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
        TxtViewParams.setMargins(32,16,32,8);
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