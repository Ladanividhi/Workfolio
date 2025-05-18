package it.dtech.workfolio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewPostActivity extends AppCompatActivity {

    EditText jobTitle, description, budget, duration;
    Button btnUpload;
    Spinner category;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        jobTitle = findViewById(R.id.job_title);
        description = findViewById(R.id.description);
        budget = findViewById(R.id.budget);
        duration = findViewById(R.id.duration);
        category = findViewById(R.id.category);
        btnUpload = findViewById(R.id.btn_upload);

        // Set up category dropdown
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.job_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences(ConstantSp.NAME, MODE_PRIVATE);
                String name = sp.getString(ConstantSp.NAME, "");
                sp = getSharedPreferences(ConstantSp.COMPANY_NAME, MODE_PRIVATE);
                String companyName = sp.getString(ConstantSp.COMPANY_NAME, "");
                String vJobTitle = jobTitle.getText().toString().trim();
                String vCategory = category.getSelectedItem().toString().trim();
                String vDescription = description.getText().toString().trim();
                String vBudget = budget.getText().toString().trim();
                String vDuration = duration.getText().toString().trim();

                // Get current date in "YYYY-MM-DD" format
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currDate = sdf.format(Calendar.getInstance().getTime());

                if (vJobTitle.isEmpty() || vDescription.isEmpty() || vBudget.isEmpty() || vDuration.isEmpty() || category.getSelectedItem().toString().equals("Select Category")) {
                    Toast.makeText(NewPostActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    insertJobPost(name, companyName, vJobTitle, vCategory, vDescription, vBudget, vDuration, currDate);
                }
            }
        });
    }

    public void insertJobPost(String name, String companyName, String jobTitle, String vCategory, String jobDescription, String budget, String duration, String currDate) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = DatabaseConnection.connect();
                    String query = "INSERT INTO WF_NewPost (name, company_name, job_title, category, description, budget, deadline, curr_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, name);
                    statement.setString(2, companyName);
                    statement.setString(3, jobTitle);
                    statement.setString(4, vCategory);
                    statement.setString(5, jobDescription);
                    statement.setString(6, budget);
                    statement.setString(7, duration);
                    statement.setString(8, currDate); // ✅ Storing date as a string

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        runOnUiThread(() -> Toast.makeText(NewPostActivity.this, "Job Posted Successfully!", Toast.LENGTH_SHORT).show());
                        finish();
                    } else {
                        runOnUiThread(() -> Toast.makeText(NewPostActivity.this, "Failed to Post Job!", Toast.LENGTH_SHORT).show());
                    }

                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(NewPostActivity.this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }
}
