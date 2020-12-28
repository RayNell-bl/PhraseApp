package com.example.phraseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CheckView extends AppCompatActivity {
    private SQLiteDatabase Db;
    private DatabaseHelper DBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_view);
        DBHelper= new DatabaseHelper(this);
        Db = DBHelper.getWritableDatabase();
    }

    @Override
    protected  void onResume() {
        super.onResume();
        LinearLayout ContentLay = (LinearLayout) findViewById(R.id.content_checkview);
        TextView header = findViewById(R.id.header_text);
        Bundle arguments = getIntent().getExtras();
        LinearLayout InfoLay = new LinearLayout(this);
        InfoLay = buildConstructLay(InfoLay);
        if (arguments != null) {
            int callback_number = arguments.getInt("callback");
            Cursor query;
            if (callback_number == 1) {
                int phrase_id = arguments.getInt("phrase_id");
                query = Db.rawQuery("SELECT ph.name, au.name, bo.name, ph.txt_phrase FROM Phrases ph\n" +
                        "INNER JOIN Authors au\n" +
                        "ON ph._author_id=au._author_id\n" +
                        "LEFT JOIN Books bo\n" +
                        "ON ph._book_id=bo._book_id\n" +
                        "WHERE _phrase_id=" + Integer.toString(phrase_id), null);
                query.moveToFirst();
                String PhraseName = query.getString(0);
                String AuthorName = query.getString(1);
                String BookName = query.getString(2);
                if (BookName == null) {
                    BookName = "...";
                }
                String PhraseText = query.getString(3);
                query.close();

                TextView AuthorNameView = buildTextView(("Автор: " + AuthorName), 18, 96);
                TextView BookNameView = buildTextView("Книга: " + BookName, 18, 96);
                TextView PhraseTextView = buildTextView(PhraseText, 20, 1024);

                PhraseTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                header.setText(PhraseName);
                InfoLay.addView(AuthorNameView);
                InfoLay.addView(BookNameView);
                InfoLay.addView(PhraseTextView);
                ContentLay.addView(InfoLay);
            }
            if (callback_number == 2) {
                int author_id = arguments.getInt("author_id");
                query = Db.rawQuery("SELECT name, born, dead_day, biography FROM Authors\n" +
                        "WHERE _author_id=" + Integer.toString(author_id), null);
                query.moveToFirst();
                String AuthorName = query.getString(0);
                String Born = query.getString(1);
                if (Born == null) {
                    Born = "...";
                }
                String DeadDay = query.getString(2);
                if (DeadDay == null) {
                    DeadDay = "...";
                }
                String Biography = query.getString(3);
                if (Biography == null) {
                    Biography = "Биография не указана";
                }
                query.close();

                query = Db.rawQuery("SELECT name FROM Books\n" +
                        "WHERE _author_id=" + Integer.toString(author_id), null);
                String BooksOfAuthor = "";
                if (query.moveToFirst()) {
                    int i = 0;
                    String Book;
                    while (!query.isAfterLast()) {
                        Book = query.getString(0);
                        if (Book != null) {
                            BooksOfAuthor = BooksOfAuthor + Book + "; ";
                        }
                        i++;
                        query.moveToNext();
                    }
                }
                query.close();

                TextView DayOfLife = buildTextView(Born + " - " + DeadDay, 18, 96);
                TextView BiographyView = buildTextView(Biography, 16, 1024);
                TextView BooksOfAuthorView = buildTextView("Книги: " + BooksOfAuthor, 16, 1024);

                DayOfLife.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                BiographyView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                header.setText(AuthorName);
                InfoLay.addView(DayOfLife);
                InfoLay.addView(BiographyView);
                InfoLay.addView(BooksOfAuthorView);
                ContentLay.addView(InfoLay);
            }
            if (callback_number == 3) {
                int book_id = arguments.getInt("book_id");
                Bitmap ImageBook;
                query = Db.rawQuery("SELECT bo.name, au.name, bo.img FROM Books bo\n" +
                        "INNER JOIN Authors au ON bo._author_id=au._author_id\n" +
                        "WHERE _book_id=" + Integer.toString(book_id), null);
                query.moveToFirst();
                String BookName = query.getString(0);
                String AuthorName = query.getString(1);
                if (query.getBlob(2) != null) {
                    byte[] imgbyte = query.getBlob(2);
                    ImageBook = BitmapFactory.decodeByteArray(imgbyte, 0, imgbyte.length);
                } else {
                    ImageBook = null;
                }
                query.close();

                header.setText(BookName);
                TextView AuthorNameView = buildTextView("Автор: "+ AuthorName, 18, 96);
                ImageView ImageBookView = new ImageView(this);
                if (ImageBook != null) {
                    ImageBookView.setImageBitmap(ImageBook);
                    LinearLayout.LayoutParams imgparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    ImageBookView.setLayoutParams(imgparams);
                    ImageBookView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }

                InfoLay.addView(AuthorNameView);
                InfoLay.addView(ImageBookView);
                ContentLay.addView(InfoLay);
            }
        }
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