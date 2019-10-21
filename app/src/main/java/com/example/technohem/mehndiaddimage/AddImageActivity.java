package com.example.technohem.mehndiaddimage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddImageActivity extends AppCompatActivity {

    private Button AddNewImageButton;
    private ImageView InputMehndiImage;
    private Uri ImageUri;
    private static final int GalleryPick = 1;
    private Spinner spinnerCategory;


    private String mehndiCategory,saveCurrentDate,saveCurrentTime;

    private String menuRandomKey,downloadImageUrl;

    private StorageReference StorageImageRef;
    private DatabaseReference DatabaseRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);

        StorageImageRef = FirebaseStorage.getInstance().getReference().child("Mehndi Images");
        DatabaseRef = FirebaseDatabase.getInstance().getReference().child("Images");

        AddNewImageButton = (Button)findViewById(R.id.add_new_image);
        InputMehndiImage = (ImageView)findViewById(R.id.select_menu_image);
        spinnerCategory = (Spinner)findViewById(R.id.spinnerCategories);
        loadingBar = new ProgressDialog(this);

        InputMehndiImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenGallery();
            }
        });

        AddNewImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidateProductData();
            }
        });
    } // on create end

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GalleryPick && resultCode==RESULT_OK && data !=null)
        {

            ImageUri = data.getData();
            InputMehndiImage.setImageURI(ImageUri);
        }
    }

    private void ValidateProductData()
    {
        mehndiCategory = spinnerCategory.getSelectedItem().toString();

        if(ImageUri == null)
        {
            Toast.makeText(this, "Image is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (mehndiCategory.equals("Select Category"))
        {
            Toast.makeText(this, "Please Select Category", Toast.LENGTH_SHORT).show();
        }
        else{
            StoreMenuInformation();
        }

    }

    private void StoreMenuInformation() {

        loadingBar.setTitle("Add New Image");
        loadingBar.setMessage("Please wait while we are adding the new image.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy ");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat(" HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        menuRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = StorageImageRef.child(ImageUri.getLastPathSegment()+ menuRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(AddImageActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(AddImageActivity.this, "Menu Image Uploaded Successfully...", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {

                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AddImageActivity.this, "Got the Menu Image URL Successfully...", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();

                        }
                    }
                });
            }
        });

    }

    private void SaveProductInfoToDatabase() {

        //add to category
        final DatabaseReference categoryRef;
        categoryRef = FirebaseDatabase.getInstance().getReference().child(mehndiCategory);

        final HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", menuRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", mehndiCategory);

        DatabaseRef.child(menuRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    categoryRef.child(menuRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                Intent intent = new Intent(AddImageActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                loadingBar.dismiss();
                                Toast.makeText(AddImageActivity.this, "Image is added Successfully..", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
                else
                {
                    loadingBar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AddImageActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddImageActivity.this, MainActivity.class));
        finish();
    }
}
