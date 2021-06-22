package communi.dog.aplicatiion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Button;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private DB db;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.db = CommuniDogApp.getInstance().getDb();

        // fiend email and password views
        emailEditText = findViewById(R.id.input_email_login);
        passwordEditText = findViewById(R.id.user_password);

        // register button
        TextView to_register_btn = findViewById(R.id.register_now);
        to_register_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        // close keyboard when click outside editText
        findViewById(R.id.loginConstraintLayout).setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            emailEditText.requestFocus();
            imm.hideSoftInputFromWindow(emailEditText.getWindowToken(), 0);
            emailEditText.clearFocus();
        });

        // login button
        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> {
            FirebaseAuth auth = db.getUsersAuthenticator();
            auth.signInWithEmailAndPassword(emailEditText.getText().toString(),
                    passwordEditText.getText().toString()).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginActivity", "signInWithEmail:success");
                    FirebaseUser user = auth.getCurrentUser();
                    db.setCurrentUser(user);
                    updateUI();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                    Toast.makeText(this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            });

            updateLoginButtonState();
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                updateLoginButtonState();
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                updateLoginButtonState();
            }
        });
    }

    public void updateUI(){
        db.currentUserLiveData.observe(this, user -> {
            if(user.isApproved()){
                startActivity(new Intent(this, MapScreenActivity.class));
            }else{
                startActivity(new Intent(this, WaitForAccessActivity.class));
            }
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: {
                    Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent1.putExtra("LOGOUT", true);
                    startActivity(intent1);
                    break;
                }
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Close the app?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void updateLoginButtonState() {
        loginButton.setEnabled(!emailEditText.getText().toString().isEmpty() &&
                !passwordEditText.getText().toString().isEmpty());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("userEmail", emailEditText.getText().toString());
        outState.putString("userPassword", passwordEditText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        emailEditText.setText(savedInstanceState.getString("userEmail"));
        passwordEditText.setText(savedInstanceState.getString("userPassword"));
        updateLoginButtonState();
    }
}