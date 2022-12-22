package com.example.btl_androidv1sever;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.btl_androidv1sever.Common.Common;
import com.example.btl_androidv1sever.Interface.ItemClick;
import com.example.btl_androidv1sever.Model.Category;
import com.example.btl_androidv1sever.Model.Stuff;
import com.example.btl_androidv1sever.ViewHolder.StuffViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class StuffList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton floatingActionButton;

    FirebaseDatabase db;
    DatabaseReference stuffList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId = "";
    FirebaseRecyclerAdapter<Stuff, StuffViewHolder> adapter;

    MaterialEditText editName, editDiscription, editPrice, editDiscount;
    Button btnSelect, btnUpload;

    Stuff newStuff;
    Uri saveUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuff_list);

        db = FirebaseDatabase.getInstance();
        stuffList = db.getReference("Stuff");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        recyclerView = (RecyclerView) findViewById(R.id.recycleStuffList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddStuff();
            }
        });
        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");

        }
        if (!categoryId.isEmpty()) {
            loadListFood(categoryId);
        }

    }

    private void showAddStuff() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StuffList.this);
        alertDialog.setTitle("Add new stuff !");
        alertDialog.setMessage("Please fill information");

        LayoutInflater inflater = this.getLayoutInflater();
        View addMenuLayout = inflater.inflate(R.layout.add_new_stuff, null);
        editName = addMenuLayout.findViewById(R.id.editName);
        editDiscription = addMenuLayout.findViewById(R.id.editDescription);
        editPrice = addMenuLayout.findViewById(R.id.editPrice);
        editDiscount = addMenuLayout.findViewById(R.id.editDiscount);
        btnSelect = addMenuLayout.findViewById(R.id.btnLoad);
        btnUpload = addMenuLayout.findViewById(R.id.btnUpload);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upLoadImage();
            }
        });

        alertDialog.setView(addMenuLayout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (newStuff != null) {
                    stuffList.push().setValue(newStuff);

                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();

    }

    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Stuff, StuffViewHolder>(Stuff.class, R.layout.stuff_item,
                StuffViewHolder.class, stuffList.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(StuffViewHolder stuffViewHolder, Stuff stuff, int i) {
                stuffViewHolder.stuff_name.setText(stuff.getName());
                Picasso.get().load(stuff.getImage()).into(stuffViewHolder.stuff_image);

                stuffViewHolder.setItemClick(new ItemClick() {
                    @Override
                    public void OnClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void upLoadImage() {
        if (saveUri != null) {
            ProgressDialog mDiaLog = new ProgressDialog(this);
            mDiaLog.setMessage("Uploading...");
            mDiaLog.show();
            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDiaLog.dismiss();
                    Toast.makeText(StuffList.this, "Uploading !", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newStuff = new Stuff();
                            newStuff.setName(editName.getText().toString());
                            newStuff.setDescription(editDiscription.getText().toString());
                            newStuff.setPrice(editPrice.getText().toString());
                            newStuff.setDiscount(editDiscount.getText().toString());
                            newStuff.setMenuId(categoryId);
                            newStuff.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDiaLog.dismiss();
                    Toast.makeText(StuffList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double process = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    mDiaLog.setMessage("Uploaded" + process + "%");
                }
            });
        }
    }

    private void loadImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

       /* TakePhoto = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if(result!= null){
                            saveUri = result;
                            btnUpload.setText("Image selected");
                        }


                    }
                });*/
        startActivityForResult(Intent.createChooser(intent, "Select picture"), Common.IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            saveUri = data.getData();
            btnSelect.setText("Image selected");
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(Common.Update)){
            showUpdateStuff(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.Delete)){

        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateStuff(String key, Stuff item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StuffList.this);
        alertDialog.setTitle("Edit stuff !");
        alertDialog.setMessage("Please fill information");

        LayoutInflater inflater = this.getLayoutInflater();
        View addMenuLayout = inflater.inflate(R.layout.add_new_stuff, null);
        editName = addMenuLayout.findViewById(R.id.editName);
        editDiscription = addMenuLayout.findViewById(R.id.editDescription);
        editPrice = addMenuLayout.findViewById(R.id.editPrice);
        editDiscount = addMenuLayout.findViewById(R.id.editDiscount);
        btnSelect = addMenuLayout.findViewById(R.id.btnLoad);
        btnUpload = addMenuLayout.findViewById(R.id.btnUpload);

        editName.setText(item.getName());
        editDiscount.setText(item.getDiscount());
        editPrice.setText(item.getPrice());
        editDiscription.setText(item.getDescription());
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upLoadImage(item);
            }
        });

        alertDialog.setView(addMenuLayout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (newStuff != null) {
                    item.setName(editName.getText().toString());
                    item.setPrice(editPrice.getText().toString());
                    item.setDiscount(editDiscount.getText().toString());
                    item.setDescription(editDiscription.getText().toString());


                    stuffList.child(key).setValue(newStuff);

                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void upLoadImage(final Stuff item) {
        if(saveUri!= null){
            ProgressDialog mDiaLog = new ProgressDialog(this);
            mDiaLog.setMessage("Uploading...");
            mDiaLog.show();
            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDiaLog.dismiss();
                    Toast.makeText(StuffList.this, "Uploading !", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDiaLog.dismiss();
                    Toast.makeText(StuffList.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double process = (100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    mDiaLog.setMessage("Uploaded"+process+"%");
                }
            });
        }
    }
}