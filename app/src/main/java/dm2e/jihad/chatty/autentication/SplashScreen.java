package dm2e.jihad.chatty.autentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import dm2e.jihad.chatty.chat.ChatActivity;
import dm2e.jihad.chatty.R;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIMER=3000;
    ImageView logo,ig;
    TextView by, name;
    Animation topAnim, bottomAnim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        logo=findViewById(R.id.id_logo_splash);
        by=findViewById(R.id.id_tv1_splash);
        ig=findViewById(R.id.id_ig);

        name=findViewById(R.id.id_maintext_splash);
        logo.setAnimation(topAnim);
        by.setAnimation(bottomAnim);
        ig.setAnimation(bottomAnim);
        name.setAnimation(bottomAnim);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent=new Intent(SplashScreen.this, ChatActivity.class);
                startActivity(intent);
                finish();


            }
        },SPLASH_TIMER);





    }
}