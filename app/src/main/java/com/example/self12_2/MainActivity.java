package com.example.self12_2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    myDBHelper myHelper;
    EditText edtName, edtNumber, edtNameResult, edtNumberResult;
    Button btnInit, btnInsert, btnUpdate, btnDelete, btnSelect;
    SQLiteDatabase sqlDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.firefox);
        setTitle("가수 그룹 관리 DB (수정)");

        edtName = (EditText) findViewById(R.id.edtName);
        edtNumber = (EditText) findViewById(R.id.edtNumber);
        edtNameResult = (EditText) findViewById(R.id.edtNameResult);
        edtNumberResult = (EditText) findViewById(R.id.edtNumberResult);

        btnInit = (Button) findViewById(R.id.btnInit);
        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnSelect = (Button) findViewById(R.id.btnSelect);

        myHelper = new myDBHelper(this);

        btnInit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sqlDB = myHelper.getWritableDatabase();//데이터 베이스 파일을 생성하기 위하여 쓰기 모드로 설정
                // 인수는 아무거나 입력하면 됨.
                //"groupDB"파일에 생성된 "groupTBL" 테이블을 삭제하고 다시 "groupTBL"테이블 생성
                myHelper.onUpgrade(sqlDB, 1, 2);
                //쓰기모드로 열은 데이터 베이스를 사용후 닫음
                sqlDB.close();
                btnSelect.callOnClick();
            }
        });

        btnInsert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sqlDB = myHelper.getWritableDatabase();
                //sqlDB.execSQL - SQL문을 사용하기 위한 메소드
                sqlDB.execSQL("INSERT INTO groupTBL VALUES ( '"
                        + edtName.getText().toString() + "' , "
                        + edtNumber.getText().toString() + ");");
                sqlDB.close();
                Toast.makeText(getApplicationContext(), "입력됨", Toast.LENGTH_SHORT).show();
                btnSelect.callOnClick();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sqlDB = myHelper.getWritableDatabase();
                if (edtName.getText().toString() != "") {
                    sqlDB.execSQL("UPDATE groupTBL SET gNumber ="
                            + edtNumber.getText() + " WHERE gName = '"
                            + edtName.getText().toString() + "';");
                }
                sqlDB.close();

                Toast.makeText(getApplicationContext(), "수정됨", Toast.LENGTH_SHORT).show();
                btnSelect.callOnClick();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sqlDB = myHelper.getWritableDatabase();
                if (edtName.getText().toString() != "") {
                    sqlDB.execSQL("DELETE FROM groupTBL WHERE gName = '"
                            + edtName.getText().toString() + "';");
                }
                sqlDB.close();

                Toast.makeText(getApplicationContext(), "삭제됨", Toast.LENGTH_SHORT).show();
                btnSelect.callOnClick();
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sqlDB = myHelper.getReadableDatabase();
                Cursor cursor;
                //cursor - 쿼리결과 저장
                //rawQuery - SQL쿼리사용 메소드
                //SELECT * FROM groupTBL - SQL의 "SELECT" 명령어를 사용하여 데이터 조회
                cursor = sqlDB.rawQuery("SELECT * FROM groupTBL;", null);

                String strNames = "그룹이름" + "\r\n" + "--------" + "\r\n";
                String strNumbers = "인원" + "\r\n" + "--------" + "\r\n";

                while (cursor.moveToNext()) {
                    strNames += cursor.getString(0) + "\r\n";
                    strNumbers += cursor.getString(1) + "\r\n";
                }

                edtNameResult.setText(strNames);
                edtNumberResult.setText(strNumbers);

                //사용 후 "cursor"와 데이터 베이스 닫음
                cursor.close();
                sqlDB.close();
            }
        });
    }

    public class myDBHelper extends SQLiteOpenHelper {
        //프로젝트 디렉토리에 "databases" 디렉토리 아래에 "groupDB" 파일 생성
        public myDBHelper(Context context) {
            super(context, "groupDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // "groupDB"파일에 "groupTBL" 테이블 생성
            db.execSQL("CREATE TABLE  groupTBL ( gName CHAR(20) PRIMARY KEY, gNumber INTEGER);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // 이전에 "groupDB"파일에 생성된 "groupTBL"테이블 삭제
            db.execSQL("DROP TABLE IF EXISTS groupTBL");
            //"groupDB"파일에 "groupTBL"테이블 생성
            onCreate(db);
        }
    }
}
