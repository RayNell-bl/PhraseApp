package com.example.phraseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddToDB extends AppCompatActivity {

    private DatabaseHelper DBHelper;
    private SQLiteDatabase Db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_d_b);
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
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            int callback_number = arguments.getInt("callback");
            TextView textu = findViewById(R.id.textu);
            LinearLayout content = findViewById(R.id.add_content);
            if (callback_number == 1) {
                textu.setText("Добавить изречение:");
            }

            if (callback_number == 2) {
                textu.setText("Добавить автора:");
                RelativeLayout addphrase = (RelativeLayout) getLayoutInflater().inflate(R.layout.addauthor, null);
                addphrase.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                content.addView(addphrase);
                Button btn = (Button) findViewById(R.id.add_author);
                btn.setOnClickListener(addAuthor);
                DatePicker BornDay = (DatePicker) this.findViewById(R.id.born_author);
                BornDay.init(2020, 00, 01, new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    }
                });
                DatePicker DeadDay = (DatePicker) this.findViewById(R.id.dead_author);
                DeadDay.init(2020, 00, 01, new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    }
                });
            }

            if (callback_number == 3) {
                textu.setText("Добавить книгу:");
            }
        }
        else {
            finish();
        }
    }

    protected String getAuthorName() {
        String AuthorName;
        EditText author = (EditText) findViewById(R.id.name_author);
        AuthorName=author.getText().toString();
        return AuthorName;
    }

    protected String getBornDayDate() {
        String BornDayDate;
        DatePicker BornDay = (DatePicker) this.findViewById(R.id.born_author);
        BornDayDate = BornDay.getDayOfMonth() + "-" + (+BornDay.getMonth() + 1) + "-" + BornDay.getYear();
        if (BornDayDate=="1-1-2020") {
            return null;
        } else {return BornDayDate;}
    }

    protected String getDeadDayDate() {
        String DeadDayDate;
        DatePicker DeadDay = (DatePicker) this.findViewById(R.id.dead_author);
        DeadDayDate = DeadDay.getDayOfMonth() + "-" + (+DeadDay.getMonth() + 1) + "-" + DeadDay.getYear();
        if (DeadDayDate =="1-1-2020") {
            return null;
        } else {return DeadDayDate;}
    }

    protected String getAuthorBiography() {
        String Biography;
        EditText biography = (EditText) findViewById(R.id.author_biography);
        Biography=biography.getText().toString();
        if (Biography.length()==0) {
            return null;
        } else { return Biography; }
    }

    View.OnClickListener addAuthor = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String Name= getAuthorName();
            String BornDay= getBornDayDate();
            String DeadDay = getDeadDayDate();
            String Biography = getAuthorBiography();
            if (Name.length()<1) {
                Toast.makeText(AddToDB.this, "Нет имени автора!", Toast.LENGTH_LONG).show();
            } else {
                ContentValues cv = new ContentValues();
                cv.put("name", Name);
                cv.put("born", BornDay);
                cv.put("dead_day", DeadDay);
                cv.put("biography", Biography);
                long res= Db.insert("Authors", null, cv);
                if (res>0) {
                    Toast.makeText(AddToDB.this, (Long.toString(res)+" id Успешно!"), Toast.LENGTH_LONG).show();
                } else { Toast.makeText(AddToDB.this,"FUFLO", Toast.LENGTH_LONG).show(); }
            }
        }
    };
}