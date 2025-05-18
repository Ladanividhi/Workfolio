package it.dtech.workfolio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardHR extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView fullNameTextView, emailTextView;
    CardView newpost, managepost, applicants, projectstatus;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_hr);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        toolbar = findViewById(R.id.toolbar);

        newpost = findViewById(R.id.card_newpost);
        managepost = findViewById(R.id.card_managepost);
        applicants = findViewById(R.id.card_applicants);
        projectstatus = findViewById(R.id.card_projectstatus);

        // Setup Toolbar
        setSupportActionBar(toolbar);

        // Enable Toggle for Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        CircleImageView profileImage = findViewById(R.id.profile_image);
        profileImage.setImageResource(R.drawable.icon_hr);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardHR.this, UpdateProfileHR.class);
                startActivity(intent);
                finish();
            }
        });

        // Retrieve SharedPreferences
        SharedPreferences sp = getSharedPreferences(ConstantSp.NAME, Context.MODE_PRIVATE);
        String fullName = sp.getString(ConstantSp.NAME, "");
        sp = getSharedPreferences(ConstantSp.EMAIL, Context.MODE_PRIVATE);
        String email = sp.getString(ConstantSp.EMAIL, "");

        // Get Header View of Navigation Drawer
        View headerView = navigationView.getHeaderView(0);
        fullNameTextView = headerView.findViewById(R.id.fullname);
        emailTextView = headerView.findViewById(R.id.email);

        // Set retrieved values
        fullNameTextView.setText(fullName);
        emailTextView.setText(email);

        // Handle Navigation Item Clicks
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.new_post) {
                    startActivity(new Intent(DashboardHR.this, NewPostActivity.class));
                } else if (id == R.id.manage_post) {
                    startActivity(new Intent(DashboardHR.this, ManagePostActivity.class));
                } else if (id == R.id.applicants) {
                    startActivity(new Intent(DashboardHR.this, ApplicantsActivity.class));
                } else if (id == R.id.project_status) {
                    // startActivity(new Intent(DashboardHR.this, ProjectStatusActivity.class));
                } else if (id == R.id.post_history) {
                    startActivity(new Intent(DashboardHR.this, ManagePostActivity.class));
                } else if (id == R.id.past_transactions) {
                    // startActivity(new Intent(DashboardHR.this, PastTransactionsActivity.class));
                } else if (id == R.id.settings) {
                    startActivity(new Intent(DashboardHR.this, SettingsActivity.class));
                } else if (id == R.id.about_us) {
                    startActivity(new Intent(DashboardHR.this, AboutusActivity.class));
                } else if (id == R.id.nav_logout) {
                    // Logout logic (Optional: Clear session and navigate to login)
                    startActivity(new Intent(DashboardHR.this, LoginActivity.class));
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // CardView Click Listeners
        newpost.setOnClickListener(v -> startActivity(new Intent(DashboardHR.this, NewPostActivity.class)));
        managepost.setOnClickListener(v -> startActivity(new Intent(DashboardHR.this, ManagePostActivity.class)));
        applicants.setOnClickListener(v -> startActivity(new Intent(DashboardHR.this, ApplicantsActivity.class)));
        projectstatus.setOnClickListener(v -> {
            // startActivity(new Intent(DashboardHR.this, ProjectStatusActivity.class));
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
