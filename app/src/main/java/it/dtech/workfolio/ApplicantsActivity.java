package it.dtech.workfolio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ApplicantsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ApplicantPostAdapter ApplicantPostAdapter;
    private List<ApplicantPost> ApplicantPostList, filteredList;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicants);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        searchView = findViewById(R.id.searchView);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ApplicantPostList = new ArrayList<>();
        filteredList = new ArrayList<>();
        ApplicantPostAdapter = new ApplicantPostAdapter(filteredList);
        mRecyclerView.setAdapter(ApplicantPostAdapter);

        fetchDataFromDatabase();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String text) {
        filteredList.clear();
        for (ApplicantPost job : ApplicantPostList) {
            if (job.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    job.getCategory().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(job);
            }
        }
        ApplicantPostAdapter.notifyDataSetChanged();
    }

    private void fetchDataFromDatabase() {
        new Thread(() -> {
            Connection con = DatabaseConnection.connect();
            if (con == null) {
                return;
            }
            SharedPreferences sp = getSharedPreferences(ConstantSp.COMPANY_NAME, MODE_PRIVATE);
            String sql = "SELECT ID, JOB_TITLE, CATEGORY FROM WF_NEWPOST WHERE COMPANY_NAME = '" + sp.getString(ConstantSp.COMPANY_NAME, "") + "'";
            List<ApplicantPost> tempApplicantPostList = new ArrayList<>();

            try (Statement st = con.createStatement(); ResultSet resultSet = st.executeQuery(sql)) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    String jobTitle = resultSet.getString("JOB_TITLE");
                    String category = resultSet.getString("CATEGORY");
                    tempApplicantPostList.add(new ApplicantPost(id, jobTitle, category));
                }

                runOnUiThread(() -> {
                    ApplicantPostList.clear();
                    ApplicantPostList.addAll(tempApplicantPostList);
                    filteredList.clear();
                    filteredList.addAll(ApplicantPostList);
                    ApplicantPostAdapter.notifyDataSetChanged();
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
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

class ApplicantPost {
    private final int id;
    private final String title;
    private final String category;
    

    public ApplicantPost(int id, String title, String category) {
        this.id = id;
        this.title = title;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }
}

class ApplicantPostAdapter extends RecyclerView.Adapter<ApplicantPostAdapter.ViewHolder> {
    private final List<ApplicantPost> jobList;

    public ApplicantPostAdapter(List<ApplicantPost> jobList) {
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_post, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApplicantPost job = jobList.get(position);
        holder.jobTitle.setText(job.getTitle());
        holder.category.setText(job.getCategory());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ApplicantActivity2.class);
            intent.putExtra("ID", job.getId());
            intent.putExtra("JOB_TITLE", job.getTitle());
            intent.putExtra("CATEGORY", job.getCategory());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, category;

        ViewHolder(View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.tvJobTitle);
            category = itemView.findViewById(R.id.tvCategory);
        }
    }
}
