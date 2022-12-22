package com.example.calendar3;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper{
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "daily.db";

    public DBHelper(@Nullable Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        // 디비가 생성될 때 호출
        // 디비 -> 테이블 -> 컬럼 -> 값
        db.execSQL("CREATE TABLE IF NOT EXISTS TodoList (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, writeDate TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onCreate(db);
    }

    // SELECT 문(할일 목록 조회)
    public ArrayList<ToDoItem> getToDoList() { //ArrayList<ToDoItem>데이터형식의 메소드
        ArrayList<ToDoItem> toDoItems = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM ToDoList ORDER BY writeDate DESC", null);
        if(cursor.getCount() != 0) { //데이터가 있으면
            while (cursor.moveToNext()){ //다음 데이터가 없을때까지
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String writeDate = cursor.getString(cursor.getColumnIndex("writeDate"));

                ToDoItem toDoItem = new ToDoItem();
                toDoItem.setId(id);
                toDoItem.setTitle(title);
                toDoItem.setContent(content);
                toDoItem.setWriteDate(writeDate);
                toDoItems.add(toDoItem); // 리스트에 추가해줌
            }
        }
        cursor.close(); //종료
        return  toDoItems;
    }


    //INSERT 문(할일 목록을 디비 넣는다)
    public void InsertTodo(String _title, String _content, String _writeDate){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO ToDoList (title, content, writeDate) VALUES('" + _title + "','" + _content + "' , '" + _writeDate + "');");
    }

    // UPDATE 문(할일 목록을 수정)
    public void UpdateToDo(String _title, String _content, String _writeDate, String _beforeDate){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE ToDoList SET title= '" + _title + "', content='" + _content + "', writeDate='" + _writeDate + "'WHERE writeDate='" + _beforeDate + "'");
    }

    // DELETE 문(할일 목록을 제거)
    public void DeleteToDo(String _beforeDate){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM ToDoList WHERE writeDate = '" + _beforeDate + "'");
    }
}
