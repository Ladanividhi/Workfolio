package it.dtech.workfolio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class SignupHR extends AppCompatActivity {
    Button btn_next;
    TextInputEditText companysize, companyname, turnover, description;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_hr);

        btn_next = findViewById(R.id.btn_next);
        companyname = findViewById(R.id.companyname);
        companysize = findViewById(R.id.companysize);
        turnover = findViewById(R.id.turnover);
        description = findViewById(R.id.description);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupHR.this, SetPassword.class);
                SharedPreferences sp = getSharedPreferences(ConstantSp.COMPANY_NAME, MODE_PRIVATE);
                sp.edit().putString(ConstantSp.COMPANY_NAME, companyname.getText().toString().trim()).apply();
                sp = getSharedPreferences(ConstantSp.TURNOVER, MODE_PRIVATE);
                sp.edit().putString(ConstantSp.TURNOVER, turnover.getText().toString().trim()).apply();
                sp = getSharedPreferences(ConstantSp.COMPANY_SIZE, MODE_PRIVATE);
                sp.edit().putString(ConstantSp.COMPANY_SIZE, companysize.getText().toString().trim()).apply();
                sp = getSharedPreferences(ConstantSp.DESCRIPTION, MODE_PRIVATE);
                sp.edit().putString(ConstantSp.DESCRIPTION, description.getText().toString().trim()).apply();
                startActivity(intent);
                finish();
            }
        });
    }
}