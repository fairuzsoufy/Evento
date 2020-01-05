package com.sf.evento.Activites;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sf.evento.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class Profile extends AppCompatActivity {

    StorageReference mStorageRef;
    private static final int PICK_IMAGE=1;


    Uri imgURI;
    EditText full_name, mobile_number;
    ImageView profile_image;
    Button update_button;
    ImageView edit_button, delete_button;
    FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");
        }

        NavigationView nav_view = findViewById(R.id.nav_view);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };



        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                if(menuItem.getItemId()==R.id.create_event)
                {
                    Intent i = new Intent(Profile.this, MapsActivity.class);
                    startActivity(i);
                }
                else if(menuItem.getItemId()==R.id.my_invitations)
                {
                    Intent i = new Intent(Profile.this, EventRequests.class);
                    startActivity(i);
                }
                else if(menuItem.getItemId()==R.id.home)
                {
                    Intent i = new Intent(Profile.this, MainActivity.class);
                    startActivity(i);
                }
                else if(menuItem.getItemId()==R.id.friends)
                {
                    Intent i = new Intent(Profile.this, FriendsActivity.class);
                    startActivity(i);
                }
                else if(menuItem.getItemId()==R.id.logout)
                {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(Profile.this, SplashActivity.class));
                }
                else if(menuItem.getItemId()==R.id.my_profile)
                {
                    Intent i = new Intent(Profile.this, Profile.class);
                    startActivity(i);
                }

                //Toast.makeText(getApplicationContext(), menuItem.getTitle() + " Selected",Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                return true;
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        db = FirebaseFirestore.getInstance();

        full_name=(EditText)findViewById(R.id.full_name);
        mobile_number=(EditText)findViewById(R.id.mobile_number);
        profile_image=(ImageView) findViewById(R.id.profile_image);
        edit_button=(ImageView) findViewById(R.id.edit_button);
        delete_button=(ImageView) findViewById(R.id.delete_button);
        update_button=(Button) findViewById(R.id.update_button);
        user = FirebaseAuth.getInstance().getCurrentUser();
        update_button.setVisibility(View.GONE);

        String name = user.getDisplayName();
        String number= user.getPhoneNumber();

        if(user.getPhotoUrl() != null) {
            mStorageRef.child(user.getPhotoUrl().getLastPathSegment()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    Picasso.get().load(uri).into(profile_image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }

        full_name.setText(name);
        mobile_number.setText(number);
        edit_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                full_name.setEnabled(true);
                edit_button.setVisibility(View.GONE);
                update_button.setVisibility(View.VISIBLE);

            }

        });

        delete_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User account deleted.");
                                                    Intent i = new Intent(Profile.this, SplashActivity.class);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(i);

                                                }
                                            }
                                        });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }

        });

        update_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(imgURI==null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(full_name.getText().toString())
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                        Map<String, Object> userMap = new HashMap<>();
                                        userMap.put("name", full_name.getText().toString());
                                        db.collection("users").document(user.getUid())
                                                .update(userMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error writing document", e);
                                                    }
                                                });

                                        Intent i = new Intent(Profile.this, MainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }
                                }
                            });
                }
                else
                {
                    fileUploader().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (task.isSuccessful()) {
                                final Uri downloadUri = task.getResult();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(full_name.getText().toString())
                                        .setPhotoUri(Uri.parse(downloadUri.getPath()))
                                        .build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User profile updated.");

                                                    Map<String, Object> userMap = new HashMap<>();
                                                    userMap.put("name", full_name.getText().toString());
                                                    userMap.put("profilePicture", downloadUri.getPath());

                                                    db.collection("users").document(user.getUid())
                                                            .update(userMap)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.w(TAG, "Error writing document", e);
                                                                }
                                                            });

                                                    Intent i = new Intent(Profile.this, MainActivity.class);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(i);
                                                }
                                            }
                                        });
                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });

                }

            }

        });

        profile_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                fileChooser();
            }
        });


    }

    private void fileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE)
        {
            imgURI=data.getData();
            profile_image.setImageURI(imgURI);
        }
        else
        {
            profile_image.setImageURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/evento-d4c29.appspot.com/o/Images%2Fprofile.png?alt=media&token=fdc66cd9-ddbc-44d4-a058-6fd14a988ab2"));
        }
    }

    private String getExtension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private Task<Uri> fileUploader()
    {
        final StorageReference ref=mStorageRef.child(System.currentTimeMillis()+"."+ getExtension(imgURI));
        return ref.putFile(imgURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(Profile.this,"image uploaded successfully",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(Profile.this,"fashal ya homar",Toast.LENGTH_LONG).show();
                    }
                }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                });

    }






}