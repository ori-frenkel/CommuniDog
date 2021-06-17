package communi.dog.aplicatiion;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RequestAccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_access);
        Button registerButton = findViewById(R.id.requestAccessRegister);
        Button askForAccess = findViewById(R.id.askForAccess);
        EditText accessCodeEditText = findViewById(R.id.enterAccessCode);
        registerButton.setOnClickListener(v -> {
            // todo: add isValidAccessCode method to the DB and check validation by it
            int accessCode = Integer.parseInt(accessCodeEditText.getText().toString());
            if (accessCode == 1234) {
                // move to register activity
                Intent successIntent = new Intent(this, RegisterActivity.class);
                startActivity(successIntent);
            }
        });
        askForAccess.setOnClickListener(v -> {
            showRequestAccessByEmailPopup();
        });


    }

    private void showRequestAccessByEmailPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Request Code");

        final EditText emailInput = new EditText(this);
        emailInput.setInputType(InputType.TYPE_CLASS_TEXT);
        emailInput.setHint("enter your email");
        builder.setView(emailInput);

        builder.setPositiveButton("request", (dialog, which) -> {
            String email = emailInput.getText().toString();
            // todo: send an email to the organization
        });
        builder.setNegativeButton("cancel", (dialog, which) -> {
            dialog.cancel();
        });

        final AlertDialog dialog = builder.create();
        dialog.show();


        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(isValidEmail(emailInput.getText().toString()));

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                positiveButton.setEnabled(isValidEmail(emailInput.getText().toString()));
            }
        });
    }

    boolean isValidEmail(String text) {
        return (!TextUtils.isEmpty(text) && Patterns.EMAIL_ADDRESS.matcher(text).matches());
    }


}
