package it.dtech.workfolio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import android.app.AlertDialog;


public class EditPost extends AppCompatActivity {

    private EditText jobTitle, jobDescription, budget, duration;
    private Spinner category;
    private Button btnEditPost, btnDeletePost;
    private int postId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        jobTitle = findViewById(R.id.job_title);
        category = findViewById(R.id.category);
        jobDescription = findViewById(R.id.description);
        budget = findViewById(R.id.budget);
        duration = findViewById(R.id.duration);
        btnEditPost = findViewById(R.id.btn_edit_post);
        btnDeletePost = findViewById(R.id.btn_delete_post);

        // Get post details from Intent
        Intent intent = getIntent();
        postId = intent.getIntExtra("ID", 0);

        jobTitle.setText(intent.getStringExtra("JOB_TITLE"));
        jobDescription.setText(intent.getStringExtra("DESCRIPTION"));
        budget.setText(intent.getStringExtra("BUDGET"));
        duration.setText(intent.getStringExtra("DEADLINE"));

        // Set spinner value
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.job_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setSelection(adapter.getPosition(intent.getStringExtra("CATEGORY")));

        btnEditPost.setOnClickListener(v -> updatePost());
        btnDeletePost.setOnClickListener(v -> deletePost());
    }

    private void updatePost() {
        String updatedTitle = jobTitle.getText().toString().trim();
        String updatedCategory = category.getSelectedItem().toString();
        String updatedDescription = jobDescription.getText().toString().trim();
        String updatedBudget = budget.getText().toString().trim();
        String updatedDuration = duration.getText().toString().trim();

        if (updatedTitle.isEmpty() || updatedDescription.isEmpty() || updatedBudget.isEmpty() || updatedDuration.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            Connection con = DatabaseConnection.connect();
            if (con == null) {
                runOnUiThread(() -> Toast.makeText(EditPost.this, "Database connection failed", Toast.LENGTH_SHORT).show());
                return;
            }

            try {
                // Convert postId safely
                int postIdInt = postId;

                // Check if the post exists before updating
                String checkSql = "SELECT COUNT(*) FROM WF_NEWPOST WHERE ID=?";
                try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, postIdInt);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        runOnUiThread(() -> Toast.makeText(EditPost.this, "Post not found!", Toast.LENGTH_SHORT).show());
                        return;
                    }
                }

                Log.d("EditPost", "Updating Post with ID: " + postIdInt);

                // Update query
                String sql = "UPDATE WF_NEWPOST SET JOB_TITLE=?, CATEGORY=?, DESCRIPTION=?, BUDGET=?, DEADLINE=? WHERE ID=?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, updatedTitle);
                    ps.setString(2, updatedCategory);
                    ps.setString(3, updatedDescription);
                    ps.setString(4, updatedBudget);
                    ps.setString(5, updatedDuration);
                    ps.setInt(6, postIdInt);

                    int rowsAffected = ps.executeUpdate();
                    runOnUiThread(() -> {
                        if (rowsAffected > 0) {
                            Toast.makeText(EditPost.this, "Post updated successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditPost.this, DashboardHR.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            Toast.makeText(EditPost.this, "Update failed! No changes detected.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            } catch (NumberFormatException e) {
                Log.e("EditPost", "Invalid postId format: " + postId);
                runOnUiThread(() -> Toast.makeText(EditPost.this, "Error: Invalid post ID", Toast.LENGTH_SHORT).show());
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(EditPost.this, "Error updating post: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void deletePost() {
        // Show confirmation dialog
        runOnUiThread(() -> {
            new AlertDialog.Builder(EditPost.this)
                    .setTitle("Delete Post")
                    .setMessage("Are you sure you want to delete this post?")
                    .setPositiveButton("Yes", (dialog, which) -> performDeletePost()) // Call delete function
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Cancel action
                    .show();
        });
    }

    private void performDeletePost() {
        new Thread(() -> {
            Connection con = DatabaseConnection.connect();
            if (con == null) {
                runOnUiThread(() -> Toast.makeText(EditPost.this, "Database connection failed", Toast.LENGTH_SHORT).show());
                return;
            }

            String sql = "DELETE FROM WF_NEWPOST WHERE ID=?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, postId);

                int rowsAffected = ps.executeUpdate();
                runOnUiThread(() -> {
                    if (rowsAffected > 0) {
                        Toast.makeText(EditPost.this, "Post deleted successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditPost.this, DashboardHR.class);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        Toast.makeText(EditPost.this, "Delete failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(EditPost.this, "Error deleting post", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
