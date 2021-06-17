package communi.dog.aplicatiion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.widget.Button;
import android.text.Editable;
import android.text.TextWatcher;

public class LoginActivity extends AppCompatActivity {

    private DB appDB;
    EditText idEditText;
    EditText userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        this.appDB = CommuniDogApp.getInstance().getDb();

        Intent activityIntent = getIntent();

        idEditText = findViewById(R.id.input_id_login);
        userPassword = findViewById(R.id.user_password);

        TextView to_register_btn = findViewById(R.id.register_now);
        to_register_btn.setOnClickListener(v -> {
            Intent newIntent = new Intent(this, RegisterActivity.class);
            startActivity(newIntent);
        });

        findViewById(R.id.loginConstraintLayout).setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            idEditText.requestFocus();
            imm.hideSoftInputFromWindow(idEditText.getWindowToken(), 0);
            idEditText.clearFocus();
        });

        findViewById(R.id.login_button).setOnClickListener(v -> { //todo: check
            DB.UserIdAndPasswordValidation userIdAndPasswordValidation = this.appDB.isValidUserPassword(idEditText.getText().toString(), userPassword.getText().toString());
            if (userIdAndPasswordValidation== DB.UserIdAndPasswordValidation.VALID) {
                this.appDB.setCurrentUser(idEditText.getText().toString());
                Intent successIntent = new Intent(this, MapScreenActivity.class);
                if (activityIntent.hasExtra("map_old_state")) {
                    successIntent.putExtra("map_old_state", activityIntent.getSerializableExtra("map_old_state"));
                }
                startActivity(successIntent);
                // todo: Move to other activity?
            } else {
                String msg = "";
                if(userIdAndPasswordValidation== DB.UserIdAndPasswordValidation.INCORRECT_ID){
                    msg = "incorrect id";
                }
                else if(userIdAndPasswordValidation== DB.UserIdAndPasswordValidation.INCORRECT_PASSWORD){
                    msg = "incorrect password";
                }
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); //todo: new Toast?
            }
        });

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setEnabled(false);

        idEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                loginButton.setEnabled(!isEmpty(idEditText) && !isEmpty(userPassword));
            }
        });

        userPassword.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                loginButton.setEnabled(!isEmpty(idEditText) && !isEmpty(userPassword));
            }
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


    void checkDataEntered() {
        boolean valid_input = true;
        if (!isId(idEditText)) {
            idEditText.setError("id is invalid!");
            valid_input = false;
        }
        if (isEmpty(userPassword)) {
            userPassword.setError("password is missing");
            valid_input = false;
        }

        if (valid_input) {
            // todo: check in DB
        }

    }

    boolean isId(EditText text) {
        String input = text.getText().toString();
        String regex = "[0-9]+";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.matches() && input.length() == 9;
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("userID", idEditText.getText().toString());
        outState.putString("userPassword", userPassword.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        idEditText.setText(savedInstanceState.getString("userID"));
        userPassword.setText(savedInstanceState.getString("userPassword"));
    }
}