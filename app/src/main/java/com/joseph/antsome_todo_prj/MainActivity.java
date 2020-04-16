package com.joseph.antsome_todo_prj;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


// 메인 액티비티
public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    EditText editText;
    ListView listView;
    ArrayAdapter<String> adapter;
    List<String> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.et);
        listView = findViewById(R.id.listView);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);


        // 버튼 클릭시 insert 함수 실행
        Button btn = (Button)findViewById(R.id.btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // insert 함수 실행
                    insert(view);
                    // insert 등록 후 select 해서 리스트 초기화
                    select();
                }
            });


        // 리스트에서 item 롱클릭하면 다이얼로그 창 띄우고 데이터 삭제
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final int idx = Integer.parseInt(list.get(position).split("\\.")[0]);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("메모 수정/삭제");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setMessage(idx+"번 메모를 수정 또는 삭제 하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //실제 데이터 삭제
                        //내장 클래스에서 지역변수에 접근하려면 반드시 final로 선언되어야함
                        String sql_delete = "delete from test where idx = " + idx;
                        db.execSQL(sql_delete);
                        Log.d("Sqllite",+idx+"번째 글 삭제 완료");

                        select();
                    }
                });
                builder.setNegativeButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 다이얼로그에서 수정 클릭시 버튼의 이름을 수정으로 바꿈
                        Button btn = (Button)findViewById(R.id.btn);
                        btn.setText("수정");
                        // list에서 내가 수정클릭한 놈의 문자열을 가져오는 nowData 변수 선언
                        String nowData = list.get(position).toString();
                        // editText창에 가져온 nowData 문자열을 넣어줌
                        editText.setText(nowData);
                        Log.d("nowData" ,nowData);
                        // 다시 수정 버튼 클릭시 update문 실행
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // editText창에 입력한 문자열 가져오는 data 변수 선언
                                String data = editText.getText().toString();
                                // update 쿼리 문을 담을 sql_update 변수 선언
                                String sql_update = "update test set title = '"+data+"' where idx =" +  idx;
                                // 쿼리 실행
                                db.execSQL(sql_update);
                                Log.d( "Sqllite",+idx+"번째 글 수정 완료!");
                                // 쿼리 실행 후 editText창 초기화
                                editText.setText("");
                                // 다시 select 해서 리스트 리셋
                                select();
                            }
                        });
                    }
                });
                builder.show(); //다이얼로그 띄우기
            }
        });

        //파일이름,허용범위,팩토리 사용유무
        db = openOrCreateDatabase("testdb.db",MODE_PRIVATE,null);
        Log.d("Sqllite","testdb 데이터베이스 생성 완료!");

        String sql_drop = "drop table test";
        db.execSQL(sql_drop);
        Log.d("Sqllite","테이블 DROP 완료!");

        //테이블 생성
        String sql = "create table if not exists test (idx integer primary key autoincrement,  title varchar(10))";
        db.execSQL(sql);
        Log.d("Sqllite","test테이블 생성 완료!");

        select();

    }

    //데이터 저장
    public void insert(View view) {
        String data = editText.getText().toString();
        if (data != null && data.trim().length()>0) {
            String sql = "insert into test (title) values ('" + data + "')";
            db.execSQL(sql);
            Log.d("Sqllite","test테이블에 " + data + " 저장 완료!");
            editText.setText("");
            editText.requestFocus();//커서 옮기기

            select();
        }
    }




    //데이터 조회
    private void select() {

        Button btn = (Button)findViewById(R.id.btn);
        btn.setText("저장");
        String sql = "select * from test order by idx";
        Cursor c1 = db.rawQuery(sql,new String[]{});
        //c1.moveToNext()
        list.clear();//리스트 비우기
        // 루프 돌면서 제목앞에 글번호 붙여줄 변수 i 값 선언
        int i=1;
        // while문을 돌면서 cursor 객체의 moveToNext()로 갯수만큼 돌면서 title 앞에 +i 를 붙여준다.
        while (c1.moveToNext()) {
            //String dbText = c1.getInt(0) + ". "; //idx번호

            String numText = i + ".";
            numText += c1.getString(c1.getColumnIndex("title"));
            i++;
/*
            dbText += c1.getString(c1.getColumnIndex("title"));
*/

            list.add(numText);
        }
        adapter.notifyDataSetChanged(); //데이터가 변경되었음을 알려줌(리스트 새로고침)
    }

    // 뒤로가기 버튼 누르면 앱 종료
    @Override
    public void onBackPressed(){
        // 백키 입력을 감지하면 다이얼로그 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage("앱을 종료하시겠습니까?");
        builder.setNegativeButton("취소", null);
        builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //다이얼로그가 PositiveButton 일때 앱 종료
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder.show();
    }
}
