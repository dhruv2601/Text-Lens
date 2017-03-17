package datapole.ocrtext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;

import me.wangyuwei.particleview.ParticleView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        ParticleView mParticleView = (ParticleView) findViewById(R.id.splash);
        mParticleView.startAnim();

        mParticleView.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {

                SharedPreferences sref;
                sref = SplashScreen.this.getSharedPreferences("entered", 0);
                SharedPreferences.Editor editor1 = sref.edit();

                if (sref.getBoolean("entered", false) == false) {
//                    editor1.putBoolean("entered", true);
//                    editor1.putInt("status", 2601);
//                    editor1.commit();

                    Intent intent = new Intent(SplashScreen.this, ActivityIntro.class);
                    startActivity(intent);
                    SplashScreen.this.finish();
                } else {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    SplashScreen.this.finish();
                }
            }
        });
    }
}
