package xyz.codecanvas.paypalexample;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {
    public static final String clientKey = "YOUR_CLIENT_ID_HERE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private static final PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(clientKey);


    public void pay(View view) {
        String amount = "100";
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(amount), "USD", "Movie Subscription",
                PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        paymentIntentLauncher.launch(intent);

    }

    ActivityResultLauncher<Intent> paymentIntentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Log.e("MainActivity", "onActivityResult: success");
                    assert result.getData() != null;
                    processPayment(result.getData());
                } else {
                    Log.e("MainActivity", "onActivityResult: failed");
                }
            }
    );

    private void processPayment(Intent data) {
        PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
        if (confirmation != null){
            try {
                String paymentDetails = confirmation.toJSONObject().toString(4);
                JSONObject payObj = new JSONObject(paymentDetails);
                String payID = payObj.getJSONObject("response").getString("id");
                String state = payObj.getJSONObject("response").getString("state");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("PayPalPaymentError", "an extremely unlikely failure occurred: ", e);
            }
        }
    }
}