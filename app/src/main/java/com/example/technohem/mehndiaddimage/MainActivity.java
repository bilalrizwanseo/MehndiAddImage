package com.example.technohem.mehndiaddimage;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.technohem.mehndiaddimage.Model.MehndiImages;
import com.example.technohem.mehndiaddimage.ViewHolder.ImageViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    // Recycler View code
    private DatabaseReference imagesRef,categryRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    //delete image from storage
    private StorageReference photoRef;

    //toolbar
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagesRef = FirebaseDatabase.getInstance().getReference().child("Images");
        categryRef = FirebaseDatabase.getInstance().getReference();

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//

        // Recycler View code
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }//on create end

    // Recycler View code
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<MehndiImages> options =
                new FirebaseRecyclerOptions.Builder<MehndiImages>()
                        .setQuery(imagesRef, MehndiImages.class)
                        .build();

        FirebaseRecyclerAdapter<MehndiImages, ImageViewHolder> adapter =
                new FirebaseRecyclerAdapter<MehndiImages, ImageViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ImageViewHolder holder, final int position, @NonNull final MehndiImages model) {

                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        // set click Listener
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence option[] = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Do you want to delete product ?");

                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {

                                        if( i == 0 )
                                        {
                                            final String menuID = getRef(position).getKey();

                                            imagesRef.child(menuID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful())
                                                    {
                                                        categryRef.child(model.getCategory()).child(menuID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful())
                                                                {
                                                                    //delete image from storage
                                                                    photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(model.getImage());
                                                                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                            Toast.makeText(MainActivity.this, "The image is deleted successfully.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {

                                                                        }
                                                                    });

                                                                }
                                                            }
                                                        });

                                                    }

                                                }
                                            });
                                        }
                                        else {

                                            //do nothing when user press no
                                        }
                                    }
                                });

                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout, parent, false);
                        ImageViewHolder holder = new ImageViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }// Recycler View code

    //popUp menu (Ctrl+O)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }//

    //popUp menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.addImage_id:
                startActivity(new Intent(MainActivity.this, AddImageActivity.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }//

}
