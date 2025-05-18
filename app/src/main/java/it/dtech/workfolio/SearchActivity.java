package it.dtech.workfolio;

import android.annotation.SuppressLint;
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

public class SearchActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SearchPostAdapter SearchPostAdapter;
    private List<SearchPost> SearchPostList, filteredList;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        searchView = findViewById(R.id.searchView);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SearchPostList = new ArrayList<>();
        filteredList = new ArrayList<>();
        SearchPostAdapter = new SearchPostAdapter(filteredList);
        mRecyclerView.setAdapter(SearchPostAdapter);

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
        for (SearchPost job : SearchPostList) {
            if (job.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    job.getCompanyName().toLowerCase().contains(text.toLowerCase()) ||
                    job.getCategory().toLowerCase().contains(text.toLowerCase()) ||
                    job.getBudget().toLowerCase().contains(text.toLowerCase()) ||
                    job.getCurrDate().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(job);
            }
        }
        SearchPostAdapter.notifyDataSetChanged();
    }

    private void fetchDataFromDatabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection con = DatabaseConnection.connect();
                if (con == null) {
                    return;
                }
                SharedPreferences sp = getSharedPreferences(ConstantSp.COMPANY_NAME, MODE_PRIVATE);
                String sql = "SELECT COMPANY_NAME, JOB_TITLE, CATEGORY, BUDGET, CURR_DATE FROM WF_NEWPOST";
                List<SearchPost> tempSearchPostList = new ArrayList<>();

                try (Statement st = con.createStatement(); ResultSet resultSet = st.executeQuery(sql)) {
                    while (resultSet.next()) {
                        String companyName = resultSet.getString("COMPANY_NAME");
                        String jobTitle = resultSet.getString("JOB_TITLE");
                        String category = resultSet.getString("CATEGORY");
                        String budget = resultSet.getString("BUDGET");
                        String currDate = resultSet.getString("CURR_DATE");
                        tempSearchPostList.add(new SearchPost(companyName, jobTitle, category, budget, currDate));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SearchPostList.clear();
                            SearchPostList.addAll(tempSearchPostList);
                            filteredList.clear();
                            filteredList.addAll(SearchPostList);
                            SearchPostAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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

// SearchPost Model
class SearchPost {
    private final String title;
    private final String category;
    private final String budget;
    private final String currDate;
    private final String companyName;

    public SearchPost(String companyName, String title, String category, String budget, String currDate) {
        this.title = title;
        this.category = category;
        this.budget = budget;
        this.currDate = currDate;
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
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
}

// RecyclerView Adapter
class SearchPostAdapter extends RecyclerView.Adapter<SearchPostAdapter.ViewHolder> {
    private final List<SearchPost> jobList;

    public SearchPostAdapter(List<SearchPost> jobList) {
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_search_post, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchPost job = jobList.get(position);
        holder.companyName.setText(job.getCompanyName());
        holder.jobTitle.setText("Job Title : " + job.getTitle());
        holder.category.setText(job.getCategory());
        holder.budget.setText("Cost : " + job.getBudget());
        holder.date.setText("Date : " + job.getCurrDate());
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView companyName, jobTitle, category, budget, date;

        ViewHolder(View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.tvCompanyName);
            jobTitle = itemView.findViewById(R.id.tvJobTitle);
            category = itemView.findViewById(R.id.tvCategory);
            budget = itemView.findViewById(R.id.tvBudget);
            date = itemView.findViewById(R.id.tvDate);
        }
    }
}
