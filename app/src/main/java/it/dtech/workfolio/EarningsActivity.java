package it.dtech.workfolio;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class EarningsActivity extends AppCompatActivity {

    private TextView tvTotalEarnings;
    private RecyclerView rvMonthlyEarnings, rvTransactions;
    private EarningsAdapter monthlyAdapter, transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings);
        tvTotalEarnings = findViewById(R.id.tvTotalEarnings);
        rvMonthlyEarnings = findViewById(R.id.rvMonthlyEarnings);
        rvTransactions = findViewById(R.id.rvTransactions);

        // Set total earnings
        tvTotalEarnings.setText("Total Earnings: $5,200.00");

        // Set up RecyclerViews
        rvMonthlyEarnings.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));

        // Load dummy data
        List<EarningItem> monthlyEarnings = new ArrayList<>();
        monthlyEarnings.add(new EarningItem("January", "$1,200"));
        monthlyEarnings.add(new EarningItem("February", "$900"));
        monthlyEarnings.add(new EarningItem("March", "$1,500"));
        monthlyEarnings.add(new EarningItem("April", "$1,600"));

        List<EarningItem> transactions = new ArrayList<>();
        transactions.add(new EarningItem("Finance Android App", "$500"));
        transactions.add(new EarningItem("Porter App", "$700"));
        transactions.add(new EarningItem("GPS Tracker", "$900"));
        transactions.add(new EarningItem("Translator", "$200"));

        // Set adapters
        monthlyAdapter = new EarningsAdapter(monthlyEarnings);
        transactionAdapter = new EarningsAdapter(transactions);

        rvMonthlyEarnings.setAdapter(monthlyAdapter);
        rvTransactions.setAdapter(transactionAdapter);

    }
}

class EarningItem {
    private final String label;
    private final String amount;

    public EarningItem(String label, String amount) {
        this.label = label;
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public String getAmount() {
        return amount;
    }
}

class EarningsAdapter extends RecyclerView.Adapter<EarningsAdapter.ViewHolder> {
    private final List<EarningItem> earningsList;

    public EarningsAdapter(List<EarningItem> earningsList) {
        this.earningsList = earningsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_earning, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EarningItem item = earningsList.get(position);
        holder.tvLabel.setText(item.getLabel());
        holder.tvAmount.setText(item.getAmount());
    }

    @Override
    public int getItemCount() {
        return earningsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}