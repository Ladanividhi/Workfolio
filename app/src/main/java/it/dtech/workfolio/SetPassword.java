package it.dtech.workfolio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SetPassword extends AppCompatActivity {
    private TextInputEditText password, confirmPassword;
    private Button btnSignup;
    private String selectedRole;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmpassword);
        btnSignup = findViewById(R.id.btnSignup);

        SharedPreferences sp = getSharedPreferences(ConstantSp.ROLE, MODE_PRIVATE);
        selectedRole = sp.getString(ConstantSp.ROLE, "");

        btnSignup.setOnClickListener(v -> {
            String pass = password.getText().toString().trim();
            String confirmPass = confirmPassword.getText().toString().trim();

            if (pass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(SetPassword.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmPass)) {
                Toast.makeText(SetPassword.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> signup(selectedRole, pass)).start();
        });
    }

    private void signup(String role, String pass) {
        SharedPreferences sp = getSharedPreferences(ConstantSp.EMAIL, MODE_PRIVATE);
        String email = sp.getString(ConstantSp.EMAIL, "");

        if (email.isEmpty()) {
            runOnUiThread(() -> Toast.makeText(SetPassword.this, "Email not found. Please enter email.", Toast.LENGTH_SHORT).show());
            return;
        }

        if (checkEmailExists(role, email)) {
            runOnUiThread(() -> Toast.makeText(SetPassword.this, "Email already exists. Try logging in.", Toast.LENGTH_SHORT).show());
        } else {
            boolean success = insertUser(role, pass);
            runOnUiThread(() -> {
                if (success) {
                    SharedPreferences.Editor editor = getSharedPreferences(ConstantSp.SIGNUP, MODE_PRIVATE).edit();
                    editor.putString(ConstantSp.SIGNUP, "true");
                    editor.apply();
                    Toast.makeText(SetPassword.this, "Signup Successful", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SetPassword.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(SetPassword.this, "Signup Failed", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean checkEmailExists(String role, String email) {
        String query = "SELECT COUNT(*) FROM " + (role.equals("HR") ? "WF_HR" : "WF_Freelancer") + " WHERE email = ?";
        try (Connection con = DatabaseConnection.connect();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean insertUser(String role, String pass) {
        SharedPreferences sp = getSharedPreferences(ConstantSp.NAME, MODE_PRIVATE);
        String name = sp.getString(ConstantSp.NAME, "");
        sp = getSharedPreferences(ConstantSp.EMAIL, MODE_PRIVATE);
        String email = sp.getString(ConstantSp.EMAIL, "");
        sp = getSharedPreferences(ConstantSp.MOBILE, MODE_PRIVATE);
        String mobile = sp.getString(ConstantSp.MOBILE, "");
        sp = getSharedPreferences(ConstantSp.GENDER, MODE_PRIVATE);
        String gender = sp.getString(ConstantSp.GENDER, "");
        sp = getSharedPreferences(ConstantSp.CITY, MODE_PRIVATE);
        String city = sp.getString(ConstantSp.CITY, "");

        if (email.isEmpty() || name.isEmpty()) {
            runOnUiThread(() -> Toast.makeText(SetPassword.this, "User details missing.", Toast.LENGTH_SHORT).show());
            return false;
        }

        String query;
        if ("HR".equals(role)) {
            query = "INSERT INTO WF_HR (name, email, mobile, city, gender, role, password, company_name, turnover, company_size, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            query = "INSERT INTO WF_Freelancer (name, email, mobile, city, gender, role, password, experience, linkedin_profile, github_profile, twitter_profile) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }

        try (Connection con = DatabaseConnection.connect();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, mobile);
            pstmt.setString(4, city);
            pstmt.setString(5, gender);
            pstmt.setString(6, role);
            pstmt.setString(7, pass);

            if ("HR".equals(role)) {
                sp = getSharedPreferences(ConstantSp.COMPANY_NAME, MODE_PRIVATE);
                pstmt.setString(8, sp.getString(ConstantSp.COMPANY_NAME, ""));
                sp = getSharedPreferences(ConstantSp.TURNOVER, MODE_PRIVATE);
                pstmt.setString(9, sp.getString(ConstantSp.TURNOVER, ""));
                sp = getSharedPreferences(ConstantSp.COMPANY_SIZE, MODE_PRIVATE);
                pstmt.setString(10, sp.getString(ConstantSp.COMPANY_SIZE, ""));
                sp = getSharedPreferences(ConstantSp.DESCRIPTION, MODE_PRIVATE);
                pstmt.setString(11, sp.getString(ConstantSp.DESCRIPTION, ""));
            } else {
                sp = getSharedPreferences(ConstantSp.EXPERIENCE, MODE_PRIVATE);
                pstmt.setString(8, sp.getString(ConstantSp.EXPERIENCE, ""));
                sp = getSharedPreferences(ConstantSp.LINKEDIN, MODE_PRIVATE);
                pstmt.setString(9, sp.getString(ConstantSp.LINKEDIN, ""));
                sp = getSharedPreferences(ConstantSp.GITHUB, MODE_PRIVATE);
                pstmt.setString(10, sp.getString(ConstantSp.GITHUB, ""));
                sp = getSharedPreferences(ConstantSp.TWITTER, MODE_PRIVATE);
                pstmt.setString(11, sp.getString(ConstantSp.TWITTER, ""));
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}