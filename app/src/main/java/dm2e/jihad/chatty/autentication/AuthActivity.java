package dm2e.jihad.chatty.autentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dm2e.jihad.chatty.chat.ChatActivity;
import dm2e.jihad.chatty.R;

public class AuthActivity extends AppCompatActivity {
    private EditText email,password;
    private Button btnLogin;
    private TextView registrarUsuario,forgotPassword;
    private String correoString,contraseniaString;
  //  private final int GOOGLE_SING_IN=3;loginWithGoogle,
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        email= findViewById(R.id.idEmailET);
        password= findViewById(R.id.idPasswordET);
        btnLogin= findViewById(R.id.idBtnLogin);
        registrarUsuario=findViewById(R.id.id_register_account_link);
        //loginWithGoogle=findViewById(R.id.login_with_google);
        forgotPassword=findViewById(R.id.id_forgot_password);

        sesion();

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
                View layoutPersonalizado = getLayoutInflater().inflate(R.layout.forgot_password_layout,null);
                builder.setView(layoutPersonalizado);
                AlertDialog dialog = builder.create();
                dialog.show();
                ImageView imagen=layoutPersonalizado.findViewById(R.id.id_cerrar_ventana_alert);
                Button btnResetear=layoutPersonalizado.findViewById(R.id.id_reset_password_button);

                TextView result=layoutPersonalizado.findViewById(R.id.id_result_restablecerPassword);



                btnResetear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        agregarAnimacion((Button) view);

                        EditText resetCorreo=layoutPersonalizado.findViewById(R.id.id_email_input_reset);
                        String resetCorreoString=resetCorreo.getText().toString();
                        if(resetCorreoString.isEmpty()){

                            resetCorreo.setError(getText(R.string.errorVacio));
                            result.setText(R.string.resetError);
                        }else{
                            FirebaseAuth.getInstance().sendPasswordResetEmail(resetCorreoString)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            result.setText(R.string.correoEnviado);
                                        } else {
                                            result.setText(R.string.correo_mal_formado);
                                        }
                                    });
                        }

                    }
                });
                imagen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {dialog.dismiss();
                    }
                });


            }
        });

        registrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AuthActivity.this, RegistarActivity.class));
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarAnimacion((Button) view);


                correoString = email.getText().toString();
                contraseniaString = password.getText().toString();
                if(emptyEmailPasswordET()){
                   FirebaseAuth.getInstance().signInWithEmailAndPassword(correoString
                            , contraseniaString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                         if(task.isSuccessful()){
                             startActivity(new Intent(AuthActivity.this, ChatActivity.class));
                             finish();
                           }else{
                               Toast.makeText(AuthActivity.this, R.string.loginError ,Toast.LENGTH_LONG).show();
                           }
                        }

                    });
                }else{
                    Toast.makeText(AuthActivity.this, R.string.campoVacio ,Toast.LENGTH_LONG).show();
               }


            }
        });

    }
    private void sesion() {
        // guardar datos de usuario autenticado
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.ficheroEstadoAutenticado), Context.MODE_PRIVATE);
      String email=sharedPref.getString("email", null);
   //   String proveedor=sharedPref.getString("proveedor", null);&& proveedor !=null

        if(email != null ){
            this.setVisible(false);

           startActivity(new Intent(AuthActivity.this, ChatActivity.class));
        }

    }

    private void agregarAnimacion(Button view) {
        Button button = view;
        Animation animation = AnimationUtils.loadAnimation(AuthActivity.this, R.anim.button_animation);
        button.startAnimation(animation);
    }


    private Context getActivity() {
        return this;
    }
    private Boolean emptyEmailPasswordET(){
        if(email.getText().toString().isEmpty()){
            email.setError(getText(R.string.errorEmailVacio));
        }
        if(password.getText().toString().isEmpty()){
            password.setError(getText(R.string.errorPassVacio));
        }


        if(email.getText().toString().isEmpty()){
            return false;
        }
        if(password.getText().toString().isEmpty()){
            return false;
        }
        return true;
    }
}