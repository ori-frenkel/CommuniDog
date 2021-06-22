package communi.dog.aplicatiion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.*;

public class RegisterActivity extends AppCompatActivity {

    EditText idEditText;
    EditText emailEditText;
    EditText passwordEditText;
    EditText rePasswordEditText;
    EditText userNameEditText;
    Button registerBtn;
    TextView to_register_btn;
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        idEditText = findViewById(R.id.input_id_register);
        emailEditText = findViewById(R.id.input_email_register);
        passwordEditText = findViewById(R.id.input_pass_reg);
        rePasswordEditText = findViewById(R.id.input_repass_reg);
        registerBtn = findViewById(R.id.register_bt);
        to_register_btn = findViewById(R.id.back_to_login);
        userNameEditText = findViewById(R.id.input_user_name_register);

        registerBtn.setEnabled(false);

        this.db = CommuniDogApp.getInstance().getDb();
        this.db.refreshDataUsers();

        registerBtn.setOnClickListener(v -> tryToRegister());
        to_register_btn.setOnClickListener(v ->

                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        findViewById(R.id.registerConstraintLayout).setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            idEditText.requestFocus();
            imm.hideSoftInputFromWindow(idEditText.getWindowToken(), 0);
            idEditText.clearFocus();
        });

        idEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                registerBtn.setEnabled(checkButtonRegisterEnable());
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                registerBtn.setEnabled(checkButtonRegisterEnable());
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                registerBtn.setEnabled(checkButtonRegisterEnable());
            }
        });

        rePasswordEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                registerBtn.setEnabled(checkButtonRegisterEnable());
            }
        });
    }

    boolean checkButtonRegisterEnable() {
        return !(isEmptyEditText(idEditText) || isEmptyEditText(emailEditText) || isEmptyEditText(passwordEditText) || isEmptyEditText(rePasswordEditText));
    }


    void tryToRegister() {
        boolean valid_input = true;
        if (!isId(idEditText)) {
            idEditText.setError("invalid id");
            valid_input = false;
        }
        if (!isEmail(emailEditText)) {
            emailEditText.setError("invalid email");
            valid_input = false;
        }
        if (!isValidPassword(passwordEditText.getText().toString())) {
            passwordEditText.setError("password should has least 6 characters");
            valid_input = false;
        } else {
            if (!passwordEditText.getText().toString().equals(rePasswordEditText.getText().toString())) {
                rePasswordEditText.setError("does not match");
                valid_input = false;
            }
        }

        if (!valid_input) return;

        // DB validation
        if (!this.db.idExistsInDB(idEditText.getText().toString())) {
//            Toast.makeText(this, "id is unknown", Toast.LENGTH_SHORT).show();
//            valid_input = false;
        } else {
            if (this.db.idDoubleUser(idEditText.getText().toString())) {
                Toast.makeText(this, "id is already register", Toast.LENGTH_SHORT).show();
                valid_input = false;
            }
        }

        if (valid_input) {
            // fireBase authentication
            FirebaseAuth auth = db.getUsersAuthenticator();
            auth.createUserWithEmailAndPassword(emailEditText.getText().toString(),
                    passwordEditText.getText().toString()).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("RegisterActivity", "createUserWithEmail:success");

                    // add user
                    FirebaseUser user = auth.getCurrentUser();
                    db.addUser(user.getUid(), this.emailEditText.getText().toString(),
                            this.userNameEditText.getText().toString());
                    db.setCurrentUser(user);

                    // update UI
                    startActivity(new Intent(this, MapScreenActivity.class));
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("RegisterActivity", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean isValidPassword(String pass) {
        return pass.length() >= 6;
    }

    boolean isId(EditText text) {
        String input = text.getText().toString();
        String regex = "[0-9]+";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.matches() && input.length() == 9;
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean isEmptyEditText(EditText text) {
        return text.getText().toString().isEmpty();
    }

}