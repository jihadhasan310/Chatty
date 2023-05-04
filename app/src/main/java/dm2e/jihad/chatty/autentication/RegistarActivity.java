package dm2e.jihad.chatty.autentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.hdodenhof.circleimageview.CircleImageView;
import dm2e.jihad.chatty.R;
import dm2e.jihad.chatty.model.Users;

public class RegistarActivity extends AppCompatActivity {
private Button btn_Registrar;
private EditText promptEmail,promptPassword, promptNombreApellido;
private String emailString,passwordString;
private TextView resultForAction;
private CircleImageView rg_profileImg;

    private Uri imageURI;
    private String imageuri;
    private  FirebaseDatabase database;
    private FirebaseStorage storage;
    ProgressDialog progressDialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Estableciendo la Cuenta ");
        progressDialog.setCancelable(false);

        btn_Registrar=findViewById(R.id.id_btn_register);
        promptEmail=findViewById(R.id.idEmailRegistarET);
        promptNombreApellido=findViewById(R.id.idNombreAplellidosET);
        promptPassword=findViewById(R.id.idPasswordRegisterET);
        resultForAction=findViewById(R.id.id_result_registrar);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        rg_profileImg = findViewById(R.id.profilerg0);
        auth=FirebaseAuth.getInstance();

        btn_Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarAnimacion((Button) view);
                if(emptyPrompt()){

                    emailString=promptEmail.getText().toString();
                    passwordString=promptPassword.getText().toString();
                    mostrarAlertDialog();
                }else{

                    resultForAction.setText(R.string.errorVacio);
                }

            }
        });

        rg_profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,getText(R.string.elijeFoto)),10);
            }
        });
    }
    public void mostrarAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layoutPersonalizado = getLayoutInflater().inflate(R.layout.aceptar_condiciones_layout, null);
        builder.setView(layoutPersonalizado);

        ImageView imagen=layoutPersonalizado.findViewById(R.id.id_cerrar_condiciones_alert);
        Button btnAceptar=layoutPersonalizado.findViewById(R.id.id_aceptar_condiciones_button);
        AlertDialog dialog = builder.create();
        dialog.show();
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        registrarUsuario(btnAceptar, dialog);


    }

    private void registrarUsuario(Button btnAceptar, AlertDialog dialog) {
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    String status = getString(R.string.status);
                    String namee=promptNombreApellido.getText().toString();
                    String encriptedPassword=encryptToSHA256Base64(passwordString);

                    auth.createUserWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("user").child(id);
                                StorageReference storageReference = storage.getReference().child("Upload").child(id);

                                if (imageURI!=null){
                                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageuri = uri.toString();
                                                        Users users = new Users(id,namee,emailString,encriptedPassword,imageuri,status);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    dialog.dismiss();
                                                                    progressDialog.show();
                                                                    Intent intent = new Intent(RegistarActivity.this, AuthActivity.class);
                                                                    startActivity(intent);

                                                                }else {
                                                                    Toast.makeText(RegistarActivity.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                                                    resultForAction.setText(R.string.errorRegistrar);
                                                                    dialog.dismiss();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else {
                                    String status = getString(R.string.status);
                                    imageuri = "https://firebasestorage.googleapis.com/v0/b/chatty-6185d.appspot.com/o/image.png?alt=media&token=f4dc4e05-9d5c-49a9-b4d4-ec112c25c136";
                                    Users users = new Users(id,namee,emailString,encriptedPassword,imageuri,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                dialog.dismiss();
                                                progressDialog.show();
                                                Intent intent = new Intent(RegistarActivity.this,AuthActivity.class);
                                                startActivity(intent);

                                            }else {
                                               // Toast.makeText(RegistarActivity.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                resultForAction.setText(R.string.errorRegistrar);

                                            }
                                        }
                                    });
                                }
                            }else {
                                Toast.makeText(RegistarActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                resultForAction.setText(R.string.errorRegistrar);
                                dialog.dismiss();
                            }
                        }
                    });

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                agregarAnimacion((Button) view);




            }
        });
    }


    private Boolean emptyPrompt(){

        if(promptNombreApellido.getText().toString().isEmpty()){
            promptNombreApellido.setError( getText(R.string.errorNombreVacio));
            progressDialog.dismiss();
        }
        if(promptEmail.getText().toString().isEmpty()){
            promptEmail.setError( getText(R.string.errorEmailVacio));
            progressDialog.dismiss();
        }
        if(promptPassword.getText().toString().isEmpty()){
            promptPassword.setError( getText(R.string.errorPassVacio));
            progressDialog.dismiss();
        }

        if(promptNombreApellido.getText().toString().isEmpty()){
        return false;
    }
        if(promptEmail.getText().toString().isEmpty()){
            return false;
        }
        if(promptPassword.getText().toString().isEmpty()){
            return false;
        }

        return true;
    }

    private void agregarAnimacion(Button view) {
        Button button = view;
        Animation animation = AnimationUtils.loadAnimation(RegistarActivity.this, R.anim.button_animation);
        button.startAnimation(animation);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10){
            if (data!=null){
                imageURI = data.getData();
                rg_profileImg.setImageURI(imageURI);
            }
        }
    }

    public  String encryptToSHA256Base64(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(text.getBytes());
        return Base64.encodeToString(hashedBytes ,Base64.NO_WRAP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}