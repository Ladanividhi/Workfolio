package it.dtech.workfolio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    private Button btn_next;
    private String selectedRole;
    TextInputEditText fullname, mobile, city, email;
    RadioGroup rgGender;
    RadioButton rbMale, rbFemale, rbOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        fullname = findViewById(R.id.fullname);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        city = findViewById(R.id.city);
        rgGender = findViewById(R.id.rg_gender);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);
        rbOther = findViewById(R.id.rb_other);
        btn_next = findViewById(R.id.btn_next);

        // Get the role from intent
        SharedPreferences roleSP = getSharedPreferences(ConstantSp.ROLE, MODE_PRIVATE);
        selectedRole = roleSP.getString(ConstantSp.ROLE, "");

        btn_next.setOnClickListener(v -> {

            // Validate fields
            String name = fullname.getText().toString().trim();
            String mob = mobile.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String cityText = city.getText().toString().trim();
            String gender = getSelectedGender();

            if (name.isEmpty() || mob.isEmpty() || emailText.isEmpty() || cityText.isEmpty() || gender.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_LONG).show();
                return;
            }

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(SignupActivity.this, "Invalid email address", Toast.LENGTH_LONG).show();
                return;
            }

            // Validate mobile number (must be exactly 10 digits)
            if (!mob.matches("\\d{10}")) {
                Toast.makeText(SignupActivity.this, "Enter a valid 10-digit mobile number", Toast.LENGTH_LONG).show();
                return;
            }

            // Save data in SharedPreferences
            SharedPreferences sp = getSharedPreferences(ConstantSp.NAME, MODE_PRIVATE);
            sp.edit().putString(ConstantSp.NAME, fullname.getText().toString().trim()).apply();
            sp = getSharedPreferences(ConstantSp.EMAIL, MODE_PRIVATE);
            sp.edit().putString(ConstantSp.EMAIL, email.getText().toString().trim()).apply();
            sp = getSharedPreferences(ConstantSp.MOBILE, MODE_PRIVATE);
            sp.edit().putString(ConstantSp.MOBILE, mobile.getText().toString().trim()).apply();
            sp = getSharedPreferences(ConstantSp.GENDER, MODE_PRIVATE);
            sp.edit().putString(ConstantSp.GENDER, getSelectedGender()).apply();
            sp = getSharedPreferences(ConstantSp.CITY, MODE_PRIVATE);
            sp.edit().putString(ConstantSp.CITY, city.getText().toString().trim()).apply();

            // Choose next activity based on role
            Intent intent;
            if (selectedRole.equals("Freelancer")) {
                intent = new Intent(SignupActivity.this, SignupFreelancer.class);
            } else {
                intent = new Intent(SignupActivity.this, SignupHR.class);
            }
            startActivity(intent);
            finish();
        });
    }

    private String getSelectedGender() {
        int selectedId = rgGender.getCheckedRadioButtonId();
        if (selectedId == R.id.rb_male) return "Male";
        else if (selectedId == R.id.rb_female) return "Female";
        else if (selectedId == R.id.rb_other) return "Other";
        return "";
    }
}
