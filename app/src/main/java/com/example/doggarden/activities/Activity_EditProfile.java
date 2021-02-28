package com.example.doggarden.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doggarden.R;
import com.example.doggarden.Objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Activity_EditProfile extends BaseActivity {

    private static final int IMAGE_REQUEST = 2;

    private TextInputLayout edit_MTE_dogName;
    private TextInputLayout edit_MTE_ownerName;
    private TextInputLayout edit_MTE_age;
    private TextInputLayout edit_MTE_breed;
    private TextInputLayout park_MTE_about;

    private MaterialButtonToggleGroup edit_BTN_gender;
    private ImageButton edit_IMG_add;
    private ImageView edit_IMGG_add;

    private MaterialButton park_MBTN_finish;

    private User currentUser;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__edit_profile);
        isDoublePressToClose = true;
        findViews();
        getCurrentUserFromDatabase();
        init();
    }

    private void init() {
        //add image
        edit_IMG_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        //dave all the details
        park_MBTN_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails();
                finish();
            }
        });


        edit_MTE_dogName.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    clearFocus(edit_MTE_dogName.getEditText());
                }
                return false;
            }
        });


        edit_MTE_breed.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    clearFocus(edit_MTE_breed.getEditText());
                }
                return false;
            }
        });

        edit_MTE_age.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    clearFocus(edit_MTE_age.getEditText());
                }
                return false;
            }
        });

        edit_MTE_ownerName.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    clearFocus(edit_MTE_ownerName.getEditText());
                }
                return false;
            }
        });

    }

    public void clearFocus(EditText EditText) {
        EditText.clearFocus();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();

            uploadImage();

        }
    }

    private void openImage() {

        Intent myIntent = new Intent();
        myIntent.setType("image/");
        myIntent.setAction(myIntent.ACTION_GET_CONTENT);
        startActivityForResult(myIntent, IMAGE_REQUEST);
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (imageUri != null) {
            StorageReference sRef = FirebaseStorage.getInstance().getReference().child(currentUser.getUid()).child("picture");
            // put file in cloud
            sRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            currentUser.setImageUrl(url);
                            progressDialog.dismiss();
                            Toast.makeText(Activity_EditProfile.this, "Image Upload Successful", Toast.LENGTH_SHORT).show();
                            showImage();
                        }
                    });
                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void showImage() {
        if (currentUser.getImageUrl() != null) {
            Glide.with(this)
                    .load(currentUser.getImageUrl())
                    .into(edit_IMG_add);
        }
    }

    private void getCurrentUserFromDatabase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                baseUser = currentUser;
                setUserText(currentUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveDetails() {
        currentUser.setAbout(park_MTE_about.getEditText().getText().toString());
        currentUser.setOwnerName(edit_MTE_ownerName.getEditText().getText().toString());
        currentUser.setDogName(edit_MTE_dogName.getEditText().getText().toString());
        currentUser.setAge(edit_MTE_age.getEditText().getText().toString());
        currentUser.setBreed(edit_MTE_breed.getEditText().getText().toString());

        if (edit_BTN_gender.getCheckedButtonId() == R.id.edit_TBTN_Male) {
            currentUser.setDogGender("MALE");
        } else if (edit_BTN_gender.getCheckedButtonId() == R.id.edit_TBTN_Female) {
            currentUser.setDogGender("FEMALE");
        }
        updateUserDatabase();

    }

    private void setUserText(User currentUser) {
        if (currentUser.getOwnerName() == null) {
            edit_MTE_ownerName.getEditText().setText("New Member");
        } else {
            edit_MTE_ownerName.getEditText().setText(currentUser.getOwnerName());
        }
        edit_MTE_dogName.getEditText().setText(currentUser.getDogName());
        edit_MTE_breed.getEditText().setText(currentUser.getBreed());
        park_MTE_about.getEditText().setText(currentUser.getAbout());
        edit_MTE_age.getEditText().setText(currentUser.getAge());


        showImage();

        if (currentUser.getDogGender().equals("MALE")) {
            edit_BTN_gender.check(R.id.edit_TBTN_Male);
        } else if (currentUser.getDogGender().equals("FEMALE")) {
            edit_BTN_gender.check(R.id.edit_TBTN_Female);
        }
    }

    private void findViews() {

        park_MBTN_finish = findViewById(R.id.park_MBTN_finish);
        edit_BTN_gender = findViewById(R.id.edit_BTN_gender);
        edit_MTE_breed = findViewById(R.id.edit_MTE_breed);
        edit_MTE_age = findViewById(R.id.edit_MTE_age);
        edit_MTE_ownerName = findViewById(R.id.edit_MTE_ownerName);
        edit_MTE_dogName = findViewById(R.id.edit_MTE_dogName);
        park_MTE_about = findViewById(R.id.park_MTE_about);
        edit_IMG_add = findViewById(R.id.edit_IMG_add);
        edit_IMGG_add = findViewById(R.id.edit_IMGG_add);

    }


    private void updateUserDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(currentUser.getUid());
        myRef.setValue(currentUser);
    }
}