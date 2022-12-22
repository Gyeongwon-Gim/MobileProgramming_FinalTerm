package com.example.calendar3;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AddActivity extends AppCompatActivity {

    Uri uri;

    ImageView imageView;

    EditText addNameEdit, addPhoneEdit;

    Bitmap bitmap;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        addNameEdit = findViewById(R.id.add_name_edit);
        addPhoneEdit = findViewById(R.id.add_phone_edit);
        imageView = findViewById(R.id.imageView);

        dialog = new Dialog(AddActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog);

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDialog();
//            }
//        });

        //전화번호 하이픈(-) 자동입력
        addPhoneEdit.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        Button pictureBtn = findViewById(R.id.picture_btn); //사진찍기 버튼
        Button selectBtn = findViewById(R.id.select_btn);//사진선택 버튼

        //사진찍기
        pictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultPicture.launch(intent);
            }
        });

        //사진선택
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultSelect.launch(intent);
            }
        });

        //저장버튼
        Button addBtn = findViewById(R.id.add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //입력값 변수에 담기
                String name = addNameEdit.getText().toString();
                String phone = addPhoneEdit.getText().toString();

                byte[] data = imageViewToByte(imageView);

                //DB 객체 생성
                com.example.calendar3.PhoneBookDB addressDB = new com.example.calendar3.PhoneBookDB(AddActivity.this);

                //DB에 저장하기
                addressDB.addPhoneNumber(name, phone, data);
            }
        });
    }

//    // dialog를 디자인하는 함수
//    public void showDialog(){
//        TextView pictureTv = findViewById(R.id.picture_tv);
//        TextView selectTv = findViewById(R.id.select_tv);
//
//        dialog.show(); // 다이얼로그 띄우기
//
//        pictureTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                activityResultPicture.launch(intent);
//            }
//        });
//
//        selectTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                activityResultSelect.launch(intent);
//            }
//        });
//    }

    //사진 찍기
    ActivityResultLauncher<Intent> activityResultPicture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if( result.getResultCode() == RESULT_OK && result.getData() != null){

                        Bundle extras = result.getData().getExtras();

                        bitmap = (Bitmap) extras.get("data");

                        imageView.setImageBitmap(bitmap);
                    }
                }
            });

    //사진 가져오기
    ActivityResultLauncher<Intent> activityResultSelect = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if( result.getResultCode() == RESULT_OK && result.getData() != null){

                        try {
                            uri = result.getData().getData();

                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                            imageView.setImageBitmap(bitmap);

                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

    /**
     * 이미지 바이트로 변환
     * @param image 이미지
     * @return
     */
    public static byte[] imageViewToByte(ImageView image) {

        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();

        //사이즈 조절
        Bitmap resize = Bitmap.createScaledBitmap(bitmap, 300, 400, true);
        ByteArrayOutputStream stream =  new ByteArrayOutputStream();

        //압축
        resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}

// 다이얼 로그 하다가 빡쳐서 그만둔다... 2022-12-12 22:40