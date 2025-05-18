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

public class ManagePostActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private JobPostAdapter jobPostAdapter;
    private List<JobPost> jobPostList, filteredList;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        searchView = findViewById(R.id.searchView);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        jobPostList = new ArrayList<>();
        filteredList = new ArrayList<>();
        jobPostAdapter = new JobPostAdapter(filteredList);
        mRecyclerView.setAdapter(jobPostAdapter);

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
        for (JobPost job : jobPostList) {
            if (job.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    job.getCategory().toLowerCase().contains(text.toLowerCase()) ||
                    job.getBudget().toLowerCase().contains(text.toLowerCase()) ||
                    job.getCurrDate().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(job);
            }
        }
        jobPostAdapter.notifyDataSetChanged();
    }

    private void fetchDataFromDatabase() {
        new Thread(() -> {
            Connection con = DatabaseConnection.connect();
            if (con == null) {
                return;
            }
            SharedPreferences sp = getSharedPreferences(ConstantSp.COMPANY_NAME, MODE_PRIVATE);
            String sql = "SELECT ID, JOB_TITLE, CATEGORY, BUDGET, CURR_DATE, DEADLINE, DESCRIPTION FROM WF_NEWPOST WHERE COMPANY_NAME = '" + sp.getString(ConstantSp.COMPANY_NAME, "") + "'";
            List<JobPost> tempJobPostList = new ArrayList<>();

            try (Statement st = con.createStatement(); ResultSet resultSet = st.executeQuery(sql)) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    String jobTitle = resultSet.getString("JOB_TITLE");
                    String category = resultSet.getString("CATEGORY");
                    String budget = resultSet.getString("BUDGET");
                    String currDate = resultSet.getString("CURR_DATE");
                    String deadline = resultSet.getString("DEADLINE");
                    String description = resultSet.getString("DESCRIPTION");
                    tempJobPostList.add(new JobPost(id, jobTitle, category, budget, currDate, deadline, description));
                }

                runOnUiThread(() -> {
                    jobPostList.clear();
                    jobPostList.addAll(tempJobPostList);
                    filteredList.clear();
                    filteredList.addAll(jobPostList);
                    jobPostAdapter.notifyDataSetChanged();
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

class JobPost {
    private final int id;
    private final String title;
    private final String category;
    private final String budget;
    private final String currDate;
    private final String deadline;
    private final String description;

    public JobPost(int id, String title, String category, String budget, String currDate, String deadline, String description) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.budget = budget;
        this.currDate = currDate;
        this.deadline = deadline;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getBudget() {
        return budget;
    }

    public String getCurrDate() {
        return currDate;
    }

    public String getDescription() {
        return description;
    }
}

class JobPostAdapter extends RecyclerView.Adapter<JobPostAdapter.ViewHolder> {
    private final List<JobPost> jobList;

    public JobPostAdapter(List<JobPost> jobList) {
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_manage_post, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobPost job = jobList.get(position);
        holder.jobTitle.setText(job.getTitle());
        holder.category.setText(job.getCategory());
        holder.budget.setText("Cost : " + job.getBudget());
        holder.date.setText("Date : " + job.getCurrDate());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditPost.class);
            intent.putExtra("ID", job.getId());
            intent.putExtra("JOB_TITLE", job.getTitle());
            intent.putExtra("CATEGORY", job.getCategory());
            intent.putExtra("BUDGET", job.getBudget());
            intent.putExtra("DEADLINE", job.getDeadline());
            intent.putExtra("DESCRIPTION", job.getDescription());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, category, budget, date;

        ViewHolder(View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.tvJobTitle);
            category = itemView.findViewById(R.id.tvCategory);
            budget = itemView.findViewById(R.id.tvBudget);
            date = itemView.findViewById(R.id.tvDate);
        }
    }
}
