package it.dtech.workfolio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class SignupFreelancer extends AppCompatActivity {
    private Button btn_next;
    private TextInputEditText linkedin, github, twitter;
    private String experienceLevel;
    private Spinner experience;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_freelancer);

        btn_next = findViewById(R.id.btn_next);
        linkedin = findViewById(R.id.linkedin);
        github = findViewById(R.id.github);
        twitter = findViewById(R.id.twitter);
        experience = findViewById(R.id.spinner_experience);

        // Set up the Spinner with experience options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.experience_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        experience.setAdapter(adapter);

        // Get selected experience level
        experience.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                experienceLevel = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                experienceLevel = "Beginner";
            }
        });

        // Button click to proceed to the next activity
        btn_next.setOnClickListener(v -> {
            Intent intent = new Intent(SignupFreelancer.this, SetPassword.class);
            SharedPreferences sp = getSharedPreferences(ConstantSp.EXPERIENCE, MODE_PRIVATE);
            sp.edit().putString(ConstantSp.EXPERIENCE, experienceLevel).apply();
            sp = getSharedPreferences(ConstantSp.LINKEDIN, MODE_PRIVATE);
            sp.edit().putString(ConstantSp.LINKEDIN, linkedin.getText().toString().trim()).apply();
            sp = getSharedPreferences(ConstantSp.TWITTER, MODE_PRIVATE);
            sp.edit().putString(ConstantSp.TWITTER, twitter.getText().toString().trim()).apply();
            sp = getSharedPreferences(ConstantSp.GITHUB, MODE_PRIVATE);
            sp.edit().putString(ConstantSp.GITHUB, github.getText().toString().trim()).apply();
            startActivity(intent);
            finish();
        });
    }
}
