package it.dtech.workfolio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button btnLogin;
    private CheckBox cbRememberMe;
    private TextView tvForgotPassword, tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPassword.class));
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SelectRole.class));
            }
        });
    }

    private void loginUser() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Run database operations in a separate thread
        new Thread(() -> {
            SharedPreferences sp = getSharedPreferences(ConstantSp.ROLE, MODE_PRIVATE);
            String selectedRole = sp.getString(ConstantSp.ROLE, "");

            String query;
            if (selectedRole.equals("HR")) {
                query = "SELECT COUNT(*) FROM WF_HR WHERE email = ? and password = ?";
            } else {
                query = "SELECT COUNT(*) FROM WF_Freelancer WHERE email = ? and password = ?";
            }

            boolean loginSuccess = false;
            try (Connection con = DatabaseConnection.connect();
                 PreparedStatement stmt = con.prepareStatement(query)) {

                stmt.setString(1, userEmail);
                stmt.setString(2, userPassword);
                try (ResultSet rs = stmt.executeQuery()) {
                    loginSuccess = rs.next() && rs.getInt(1) > 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            boolean finalLoginSuccess = loginSuccess;

            // Update UI on the main thread
            runOnUiThread(() -> {
                if (finalLoginSuccess) {
                    SharedPreferences sp_1 = getSharedPreferences(ConstantSp.REMEMBER, MODE_PRIVATE);
                    sp_1.edit().putString(ConstantSp.REMEMBER, cbRememberMe.isChecked() ? "true" : "false").apply();
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = selectedRole.equals("HR")
                            ? new Intent(LoginActivity.this, DashboardHR.class)
                            : new Intent(LoginActivity.this, DashboardFreelancer.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

}
