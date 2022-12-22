package com.example.btl_androidv1sever;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.app.ProgressDialog;

import com.example.btl_androidv1sever.Common.Common;
import com.example.btl_androidv1sever.Interface.ItemClick;
import com.example.btl_androidv1sever.Model.Category;
import com.example.btl_androidv1sever.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_androidv1sever.databinding.ActivityHomepageBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import android.app.ProgressDialog;

import java.util.UUID;

public class Activity_Homepage extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomepageBinding binding;
    TextView txtFullName;
    FirebaseDatabase database;
    DatabaseReference category;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
/*    ActivityResultLauncher<String> TakePhoto;*/

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MaterialEditText editText;
    Button btnUpload,btnSelect;

    Category newCategory;
    Uri saveUri;
    private final int IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarActivityHomepage.toolbar);
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        firebaseStorage= FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        binding.appBarActivityHomepage.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        final NavHostFragment navHostFragment =(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        final NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View headerView =navigationView.getHeaderView(0);
        txtFullName = (TextView)headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());


        recyclerView = (RecyclerView) findViewById(R.id.recycleMenu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        loadMenu();
    }

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Activity_Homepage.this);
        alertDialog.setTitle("Add new type !");
        alertDialog.setMessage("Please fill information");

        LayoutInflater inflater = this.getLayoutInflater();
        View addMenuLayout = inflater.inflate(R.layout.add_new_item,null);
        editText = addMenuLayout.findViewById(R.id.txtName);
        btnSelect = addMenuLayout.findViewById(R.id.btnChoice);
        btnUpload = addMenuLayout.findViewById(R.id.btnSelectUp);
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
                if(newCategory!= null){
                    category.push().setValue(newCategory);

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

    private void upLoadImage() {
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
                    Toast.makeText(Activity_Homepage.this, "Uploading !", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newCategory = new Category(editText.getText().toString(), uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDiaLog.dismiss();
                    Toast.makeText(Activity_Homepage.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST && resultCode ==RESULT_OK && data!= null && data.getData()!= null){

            saveUri = data.getData();
            btnSelect.setText("Image selected");
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
        startActivityForResult(Intent.createChooser(intent,"Select picture"),IMAGE_REQUEST);




    }

    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                Category.class,R.layout.menu_item,MenuViewHolder.class,category
        ) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Category category, int i) {
                menuViewHolder.txtMenuView.setText(category.getName());
                Picasso.get().load(category.getImage()).into(menuViewHolder.imageView);

                 menuViewHolder.setItemClick(new ItemClick() {
                     @Override
                     public void OnClick(View view, int position, boolean isLongClick) {
                         Intent stuffList = new Intent(Activity_Homepage.this,StuffList.class);
                         stuffList.putExtra("CategoryId",adapter.getRef(position).getKey());
                         startActivity(stuffList);
                     }
                 });


            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity__homepage, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        final NavHostFragment navHostFragment =(NavHostFragment) getSupportFragmentManager().findFragmentById( R.id.nav_host_fragment_activity_main);
        final NavController navController = navHostFragment.getNavController();
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(Common.Update)){
            showUpdate(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.Delete)){
            showDelete(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void showDelete(String key, Category item) {
        category.child(key).removeValue();
    }

    private void showUpdate(String key, Category item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Activity_Homepage.this);
        alertDialog.setTitle("update new type !");
        alertDialog.setMessage("Please fill information");

        LayoutInflater inflater = this.getLayoutInflater();
        View addMenuLayout = inflater.inflate(R.layout.add_new_item,null);
        editText = addMenuLayout.findViewById(R.id.txtName);
        btnSelect = addMenuLayout.findViewById(R.id.btnChoice);
        btnUpload = addMenuLayout.findViewById(R.id.btnSelectUp);
        editText.setText(item.getName());
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(item);
            }
        });

        alertDialog.setView(addMenuLayout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setName(editText.getText().toString());
                category.child(key).setValue(item);
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

    private void uploadImage(Category item) {
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
                    Toast.makeText(Activity_Homepage.this, "Uploading !", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Activity_Homepage.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
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