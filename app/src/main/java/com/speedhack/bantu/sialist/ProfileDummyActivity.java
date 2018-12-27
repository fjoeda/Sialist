package com.speedhack.bantu.sialist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.net.ConnectException;
import java.util.concurrent.ExecutionException;

public class ProfileDummyActivity extends AppCompatActivity {

    FaceDetectionHandler faceDetectionHandler;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    Bitmap[] bitmaps = new Bitmap[2];
    String user = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_dummy);
        bitmaps[0] = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.anton);
        bitmaps[1] = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.fatur);
        Button scanPerson = findViewById(R.id.scanPersonButton);
        faceDetectionHandler = new FaceDetectionHandler();
        scanPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            TextView tv = findViewById(R.id.namaUser);
            try {
                if(faceDetectionHandler.isFaceIdentical(faceDetectionHandler.getFaceUUID(imageBitmap),
                        faceDetectionHandler.getFaceUUID(bitmaps[0]))){
                    //anton
                    tv.setText("Antonius Yonanda");
                    user = "aa";
                }else if(faceDetectionHandler.isFaceIdentical(faceDetectionHandler.getFaceUUID(imageBitmap),
                        faceDetectionHandler.getFaceUUID(bitmaps[1]))){
                    tv.setText("Faturahman Yudanto");
                    user = "bb";
                }

                if(user.equals("aa")){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ugm.id/sialist"));
                    startActivity(browserIntent);
                }else if(user.equals("bb")){
                    Intent intent = new Intent(ProfileDummyActivity.this,ProfileDummyActivity.class);
                    System.out.println("goto account");
                    startActivity(intent);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ConnectException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
