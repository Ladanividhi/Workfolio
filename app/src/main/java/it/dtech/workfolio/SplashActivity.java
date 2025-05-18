package it.dtech.workfolio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint({"MissingInflatedId", "LocalSuppress", "CustomSplashScreen"})
public class SplashActivity extends AppCompatActivity {
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView appLogo = findViewById(R.id.app_logo);

        // Fade-in animation for logo
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(2000); // 2 seconds
        appLogo.startAnimation(fadeIn);

        sp = getSharedPreferences(ConstantSp.SIGNUP, MODE_PRIVATE);
        String isSignup = sp.getString(ConstantSp.SIGNUP, "false");

        new Handler().postDelayed(() -> {
            Intent intent;

            if (isSignup.equals("true")) {
                SharedPreferences rememberPref = getSharedPreferences(ConstantSp.REMEMBER, MODE_PRIVATE);
                String isRemember = rememberPref.getString(ConstantSp.REMEMBER, "");

                if (isRemember.equals("true")) {
                    SharedPreferences rolePref = getSharedPreferences(ConstantSp.ROLE, MODE_PRIVATE);
                    String selectedRole = rolePref.getString(ConstantSp.ROLE, "");

                    if (selectedRole.equals("HR")) {
                        intent = new Intent(SplashActivity.this, DashboardHR.class);
                    } else {
                        intent = new Intent(SplashActivity.this, DashboardFreelancer.class);
                    }
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
            } else {
                intent = new Intent(SplashActivity.this, SelectRole.class);
            }

            startActivity(intent);
            finish();
        }, 2500);
    }
}
