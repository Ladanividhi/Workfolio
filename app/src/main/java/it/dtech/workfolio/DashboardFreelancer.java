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
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardFreelancer extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    TextView fullname, fullNameTextView, emailTextView;
    private NavigationView navigationView;
    private Toolbar toolbar;
    CardView card1, card2;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_freelancer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        fullname = findViewById(R.id.fullname);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        toolbar = findViewById(R.id.toolbar);
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);

        SharedPreferences sp = getSharedPreferences(ConstantSp.NAME, MODE_PRIVATE);
        String name = sp.getString(ConstantSp.NAME, "User");
        fullname.setText(name);
        sp = getSharedPreferences(ConstantSp.EMAIL, Context.MODE_PRIVATE);
        String email = sp.getString(ConstantSp.EMAIL, "");

        // Get Header View of Navigation Drawer
        View headerView = navigationView.getHeaderView(0);
        fullNameTextView = headerView.findViewById(R.id.fullname);
        emailTextView = headerView.findViewById(R.id.email);

        // Set retrieved values
        fullNameTextView.setText(name);
        emailTextView.setText(email);


        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardFreelancer.this, EarningsActivity.class);
                startActivity(intent);
            }
        });

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        CircleImageView profileImage = findViewById(R.id.profile_image);
        profileImage.setImageResource(R.drawable.icon_freelancer);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardFreelancer.this,UpdateProfileFreelancer.class);
                startActivity(intent);
                finish();
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_projects) {
                    // startActivity(new Intent(DashboardFreelancer.this, ProjectsActivity.class));
                } else if (id == R.id.nav_earnings) {
                    // startActivity(new Intent(DashboardFreelancer.this, EarningsActivity.class));
                } else if (id == R.id.nav_update_profile) {
                    startActivity(new Intent(DashboardFreelancer.this, UpdateProfileFreelancer.class));
                } else if (id == R.id.nav_search) {
                    startActivity(new Intent(DashboardFreelancer.this, SearchActivity.class));
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(DashboardFreelancer.this, SettingsActivity.class));
                } else if (id == R.id.nav_about) {
                    startActivity(new Intent(DashboardFreelancer.this, AboutusActivity.class));
                } else if (id == R.id.nav_logout) {
                    startActivity(new Intent(DashboardFreelancer.this, LoginActivity.class));
                    SharedPreferences sp = getSharedPreferences(String.valueOf(ConstantSp.REMEMBER), MODE_PRIVATE);
                    sp.edit().putBoolean(String.valueOf(ConstantSp.REMEMBER), false).apply();
                    finish();
                }
                drawerLayout.closeDrawers();
                return true;
            }
       });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
