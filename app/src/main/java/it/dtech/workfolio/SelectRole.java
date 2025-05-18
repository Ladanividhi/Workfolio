package it.dtech.workfolio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SelectRole extends AppCompatActivity {

    private CardView cardFreelancer, cardHire;
    private Button btnNext;
    private String selectedRole = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_role);

        cardFreelancer = findViewById(R.id.cardFreelancer);
        cardHire = findViewById(R.id.cardHire);
        btnNext = findViewById(R.id.btnNext);

        cardFreelancer.setOnClickListener(view -> selectRole(cardFreelancer, "Freelancer"));
        cardHire.setOnClickListener(view -> selectRole(cardHire, "HR"));

        btnNext.setOnClickListener(view -> {
            if (selectedRole == null) {
                Toast.makeText(SelectRole.this, "Please select a role", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences sp = getSharedPreferences(ConstantSp.ROLE, MODE_PRIVATE);
            sp.edit().putString(ConstantSp.ROLE, selectedRole).apply();
            Intent intent = new Intent(SelectRole.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });

    }
    private void selectRole(CardView selectedCard, String role) {
        cardFreelancer.setBackgroundResource(R.drawable.border_selector);
        cardHire.setBackgroundResource(R.drawable.border_selector);

        selectedCard.setBackgroundResource(R.drawable.border_selector_selected);
        selectedRole = role;

        btnNext.setEnabled(true);
    }

}