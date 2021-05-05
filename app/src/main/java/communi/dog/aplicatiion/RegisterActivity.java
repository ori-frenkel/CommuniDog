package communi.dog.aplicatiion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText id;
    EditText emailAddress;
    EditText pass1;
    EditText pass2;
    Button register;
    TextView to_register_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        id = findViewById(R.id.input_id_register);
        emailAddress = findViewById(R.id.input_email_register);
        pass1 = findViewById(R.id.input_pass_reg);
        pass2 = findViewById(R.id.input_repass_reg);
        register = findViewById(R.id.register_bt);
        to_register_btn = findViewById(R.id.back_to_login);

        register.setOnClickListener(v -> checkDataEntered());

        to_register_btn.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        findViewById(R.id.registerConstraintLayout).setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            id.requestFocus();
            imm.hideSoftInputFromWindow(id.getWindowToken(), 0);
            id.clearFocus();
        });
    }


    void checkDataEntered() {
        boolean valid_input = true;
        boolean id_known = true;

        // input validation
        if (isEmpty(id)) {
            id.setError("id is required!");
            valid_input = false;
        }
        if (!isEmail(emailAddress)) {
            emailAddress.setError("email is not valid!");
            valid_input = false;
        }
        if (isEmpty(pass1)) {
            pass1.setError("password is required!");
            valid_input = false;
        } else {
            if (!pass1.getText().toString().equals(pass2.getText().toString())) {
                pass2.setError("does not match");
                valid_input = false;
            }
        }


        // DB validation
        if (!idExistsInDB(id)) {
            Toast t = Toast.makeText(this, "id is unknown", Toast.LENGTH_SHORT);
            t.show();
            valid_input = false;
        } else {
            if (idDoubleUser(id)) {
                Toast t = Toast.makeText(this, "id is already register", Toast.LENGTH_SHORT);
                t.show();
                valid_input = false;
            }
        }

        if (valid_input) {
            Toast t = Toast.makeText(this, "input is valid", Toast.LENGTH_SHORT);
            t.show();
        }

    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean idExistsInDB(EditText id) {
        // this method checks if the id is in the DB
        return true;
    }

    boolean idDoubleUser(EditText id) {
        // this method check if this id already has a user in the app
        return false;
    }

    void addUser() {
        // this method is called ones the data was successfully entered and the id in known
        // we add the new user's details to the user's DB
    }

}