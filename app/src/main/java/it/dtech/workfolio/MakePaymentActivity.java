package it.dtech.workfolio;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.razorpay.Checkout;
import org.json.JSONObject;

public class MakePaymentActivity extends AppCompatActivity {

    private EditText etName, etEmail, etAmount;
    private Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAmount = findViewById(R.id.etAmount);
        btnPay = findViewById(R.id.btnPay);

        Checkout.preload(getApplicationContext());

        btnPay.setOnClickListener(v -> startPayment());
    }

    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("your_razorpay_key");  // Replace with your key

        try {
            JSONObject options = new JSONObject();
            options.put("name", etName.getText().toString());
            options.put("description", "Freelancer Payment");
            options.put("currency", "INR");
            options.put("amount", Integer.parseInt(etAmount.getText().toString()) * 100);
            options.put("prefill.email", etEmail.getText().toString());

            checkout.open(MakePaymentActivity.this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
