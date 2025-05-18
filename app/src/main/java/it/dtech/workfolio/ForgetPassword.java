package it.dtech.workfolio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ForgetPassword extends AppCompatActivity {

    private TextInputEditText mobileInput, emailInput;
    private Button btnNext;
    private Handler mainHandler = new Handler(Looper.getMainLooper()); // Handler to update UI from thread

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mobileInput = findViewById(R.id.oldpassword); // Mobile Number field
        emailInput = findViewById(R.id.newpassword);  // Email field
        btnNext = findViewById(R.id.btnChangePassword);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = mobileInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();

                if (mobile.isEmpty() || email.isEmpty()) {
                    Toast.makeText(ForgetPassword.this, "Please enter both fields", Toast.LENGTH_SHORT).show();
                } else {
                    checkUserExists(mobile, email);
                }
            }
        });
    }

    private void checkUserExists(String mobile, String email) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean userExists = false;
                Connection conn = null;
                PreparedStatement stmt = null;
                ResultSet rs = null;

                try {
                    // Azure SQL Database Connection
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    String dbURL = "jdbc:jtds:sqlserver://your-server-name.database.windows.net:1433;DatabaseName=your-database-name;user=your-username;password=your-password;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
                    conn = DriverManager.getConnection(dbURL);

                    String query = "SELECT * FROM WF_HR WHERE mobile = ? AND email = ?";
                    stmt = conn.prepareStatement(query);
                    stmt.setString(1, mobile);
                    stmt.setString(2, email);
                    rs = stmt.executeQuery();

                    if (rs.next()) {
                        userExists = true; // User exists in the database
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (rs != null) rs.close();
                        if (stmt != null) stmt.close();
                        if (conn != null) conn.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                boolean finalUserExists = userExists;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (finalUserExists) {
                            Intent intent = new Intent(ForgetPassword.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ForgetPassword.this, "Invalid mobile number or email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        thread.start();
    }
}
