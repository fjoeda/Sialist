package com.speedhack.bantu.sialist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {



    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    public String user = "";
    FaceDetectionHandler faceDetectionHandler;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    Bitmap[] bitmaps = new Bitmap[2];



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //database = FirebaseDatabase.getInstance();
        bitmaps[0] = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.anton);
        bitmaps[1] = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.fatur);

        faceDetectionHandler = new FaceDetectionHandler();
        Button takephoto = findViewById(R.id.scanFace);
        final ImageView faceImage = findViewById(R.id.faceImage);
        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });


        Button signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InvokeLogin();
            }
        });
        Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView faceImage = findViewById(R.id.faceImage);
            faceImage.setImageBitmap(imageBitmap);
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

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ConnectException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }


    public void getIdIdentity(final Bitmap image)throws InterruptedException,ConnectException,ExecutionException{
        final UUID uuid = faceDetectionHandler.getFaceUUID(image);
        System.out.println(uuid);
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("Person");
        final ArrayList<String> item = new ArrayList<>();
        final String result = "tese";


        ValueEventListener listener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            item.add(postSnapshot.getKey());
                            String res = "";
                            for(String id : item){
                                UUID uuid2 = UUID.fromString(id);
                                //System.out.println(uuid2);
                                try {
                                    if(faceDetectionHandler.isFaceIdentical(uuid,uuid2)){
                                        res = id;
                                        break;
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ConnectException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                                System.out.print("res ");
                                System.out.println(res);
                            }
                            DatabaseReference ref = database.getRef().child(res);
                            TextView namaUser = findViewById(R.id.namaUser);
                            namaUser.setText(ref.child("Nama").getKey());
                            System.out.println(ref.child("Nama").toString());
                            System.out.println(res);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        database.addListenerForSingleValueEvent(listener);

    }

    String[] pass = {"12345","qwerty"};
    public void InvokeLogin(){
        System.out.println("pressed");
        TextView tv = findViewById(R.id.namaUser);
        EditText passw = findViewById(R.id.password);
        if(user.equals("aa")){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ugm.id/sialist"));
            startActivity(browserIntent);
        }else if(user.equals("bb")){
            Intent intent = new Intent(LoginActivity.this,ProfileDummyActivity.class);
            System.out.println("goto account");
            startActivity(intent);
        }
    }

}

