package com.example.calendar3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWindow;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Field;
import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ContactsActivity extends AppCompatActivity {

    com.example.calendar3.PhoneBookDB db;

    ArrayList<PhoneBook> phoneList = new ArrayList<>();

    RecyclerView recyclerView;

    PhoneBookAdapter adapter;

    TextView noDataText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        //이미지 크기 오류 방지
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }

        //데이터 유무 텍스트
        noDataText = findViewById(R.id.noData_text);

        //리스트 보여줄 화면
        recyclerView = findViewById(R.id.recyclerView);

        //어뎁터
        adapter = new PhoneBookAdapter(ContactsActivity.this);

        //어뎁터 등록
        recyclerView.setAdapter(adapter);

        //레이아웃 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(ContactsActivity.this));

        //DB 생성
        db = new PhoneBookDB(ContactsActivity.this);

        //데이터 가져오기
        storeDataInArrays();

        FloatingActionButton addBtn = findViewById(R.id.add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //등록화면으로 이동
                Intent intent = new Intent(ContactsActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();

                switch(direction){

                    case ItemTouchHelper.LEFT:

                        String deleteId = phoneList.get(position).getPhone_id();

                        //삭제 기능
                        phoneList.remove(position);
                        adapter.removeItem(position);
                        adapter.notifyItemRemoved(position);

                        db.deleteData(deleteId);
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder,
                        dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(Color.RED)
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .addSwipeLeftLabel("삭제")
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);
    } //onCreate


    /**
     * 데이터 가져오기
     */
    void storeDataInArrays(){

        Cursor cursor = db.readAllData();

        if(cursor.getCount() == 0){
            noDataText.setVisibility(noDataText.VISIBLE);
        }else{

            noDataText.setVisibility(noDataText.GONE);

            while (cursor.moveToNext()){

                com.example.calendar3.PhoneBook phone = new com.example.calendar3.PhoneBook(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getBlob(3));

                //데이터 등록
                phoneList.add(phone);
                adapter.addItem(phone);

                //적용
                adapter.notifyDataSetChanged();
            }
        }
    }

}