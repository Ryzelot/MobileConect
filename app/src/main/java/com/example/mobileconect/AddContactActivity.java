package com.example.mobileconect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddContactActivity extends AppCompatActivity {

    ImageView profileimg;
    Button btn_add, btn_addimg, btn_deleteimg;
    EditText nombre, correo, numero;
    private FirebaseFirestore mfirestore;

    StorageReference storageReference;
    String storage_path = "contact/*";

    public static final int COD_SEL_IMAGE = 300;
    private  Uri image_url;
    String photo = "photo";
    String idd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        String id = getIntent().getStringExtra("id_contact");
        mfirestore = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();


        profileimg = findViewById(R.id.profileimg);
        btn_addimg = findViewById(R.id.imgadd);
        btn_deleteimg = findViewById(R.id.imgdelete);


        btn_add = findViewById(R.id.buttonInsertContact);
        nombre = findViewById(R.id.editTextText);
        correo = findViewById(R.id.editTextText2);
        numero = findViewById(R.id.editTextText3);

        btn_addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImg();
            }
        });


        if(id == null || id == ""){
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String conname = nombre.getText().toString().trim();
                    String concorreo = correo.getText().toString().trim();
                    String connumero = numero.getText().toString().trim();

                    if(conname.isEmpty() && concorreo.isEmpty() && connumero.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Ingresa los datos", Toast.LENGTH_SHORT).show();
                    }else {
                        postContact(conname,concorreo,connumero);
                    }
                }
            });
        }else {
            btn_add.setText("Actualizar contacto");
            getContact(id);
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String conname = nombre.getText().toString().trim();
                    String concorreo = correo.getText().toString().trim();
                    String connumero = numero.getText().toString().trim();

                    if(conname.isEmpty() && concorreo.isEmpty() && connumero.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Ingresa los datos", Toast.LENGTH_SHORT).show();
                    }else {
                        updateContact(conname,concorreo,connumero, id);
                    }
                }
            });
        }
    }

    private void uploadImg() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");

        startActivityForResult(i,COD_SEL_IMAGE);

    }

    @Override
    protected void onActivityResult(int RequestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(RequestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (resultCode == COD_SEL_IMAGE) {
                image_url = data.getData();
                subirPhoto(image_url);
            }
        }
    }

    private void subirPhoto(Uri imageUrl) {
        String rute_storage_photo = storage_path + "" + photo + idd;
        StorageReference reference = storageReference.child(rute_storage_photo);
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask =taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                if(uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_uri = uri.toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("photo",download_uri);
                            mfirestore.collection("contact").document(idd).update(map);
                            Toast.makeText(AddContactActivity.this, "Foto actualizada", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddContactActivity.this, "Error al cargar la imagen de perfil", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateContact(String conname, String concorreo, String connumero, String id) {

        Map<String,Object> map = new HashMap<>();
        map.put("name",conname);
        map.put("correo",concorreo);
        map.put("numero",connumero);

        mfirestore.collection("contact").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Contacto actualizado con exito", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al actualizar contacto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postContact(String conname, String concorreo, String connumero) {

        Map<String,Object> map = new HashMap<>();
        map.put("name",conname);
        map.put("correo",concorreo);
        map.put("numero",connumero);

        mfirestore.collection("contact").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Contacto agregado con exito", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al ingresar contacto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getContact (String id) {
        mfirestore.collection("contact").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String nameContact = documentSnapshot.getString("name");
                String emailContact = documentSnapshot.getString("correo");
                String numberContact = documentSnapshot.getString("numero");

                nombre.setText(nameContact);
                correo.setText(emailContact);
                numero.setText(numberContact);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddContactActivity.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

}