package communi.dog.aplicatiion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class RegisterActivity extends AppCompatActivity {

    EditText id;
    EditText emailAddress;
    EditText pass1;
    EditText pass2;
    Button register;
    TextView to_register_btn;
    private DB appDB;

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

        register.setEnabled(false);

        Intent intent = getIntent();
        this.appDB = new DB();
        this.appDB.restoreState((DB.DBState) intent.getSerializableExtra("DB"));
        this.appDB.refreshDataUsers();

        register.setOnClickListener(v -> checkDataEntered());
        to_register_btn.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        findViewById(R.id.registerConstraintLayout).setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            id.requestFocus();
            imm.hideSoftInputFromWindow(id.getWindowToken(), 0);
            id.clearFocus();
        });

        id.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            public void afterTextChanged(Editable s) {
                register.setEnabled(checkButtonRegisterEnable());
            }
        });

        emailAddress.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            public void afterTextChanged(Editable s) {
                register.setEnabled(checkButtonRegisterEnable());
            }
        });

        pass1.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            public void afterTextChanged(Editable s) {
                register.setEnabled(checkButtonRegisterEnable());
            }
        });

        pass2.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            public void afterTextChanged(Editable s) {
                register.setEnabled(checkButtonRegisterEnable());
            }
        });
    }

    boolean checkButtonRegisterEnable() {
        return !isEmpty(id) && !isEmpty(emailAddress) && !isEmpty(pass1) && !isEmpty(pass2);
    }


    void checkDataEntered() {
        boolean valid_input = true;
        boolean id_known = true;
        if (!isId(id)) {
            id.setError("id is invalid!");
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

        //todo: don't check the db if the user failed before?

        // DB validation
        if (!this.appDB.idExistsInDB(id.getText().toString())) {
            Toast.makeText(this, "id is unknown", Toast.LENGTH_SHORT).show();
            valid_input = false;
        } else {
            if (this.appDB.idDoubleUser(id.getText().toString())) {
                Toast.makeText(this, "id is already register", Toast.LENGTH_SHORT).show();
                valid_input = false;
            }
        }

        if (valid_input) {
            Toast.makeText(this, "input is valid", Toast.LENGTH_SHORT).show();
            this.appDB.addUser(this.id.getText().toString(), this.emailAddress.getText().toString(),
                    this.pass1.getText().toString());
            Intent successIntent = new Intent(this, MapScreenActivity.class); //todo: Maybe to MapScreenActivity?
            successIntent.putExtra("userId", id.getText().toString());
            successIntent.putExtra("DB", this.appDB.currentState());
            startActivity(successIntent);
        }
    }

    boolean isId(EditText text)
    {
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

    boolean isEmpty(EditText text) {
        return text.getText().toString().isEmpty();
    }

    /*
    private boolean idDoubleUser(EditText id) {
        // todo: is there a better way to check that? do we really need to hold all id's in memory? why not a simple DB query??
        //  also, why don't we have a class for all the db queries? e.g. getPassword(id), isRegistered(id) etc.
        return allInUseIDs.contains(id.getText().toString());
    }

     */

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("userID", id.getText().toString());
        outState.putString("userEmail", emailAddress.getText().toString());
        outState.putString("userPass1", pass1.getText().toString());
        outState.putString("userPass2", pass2.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        id.setText(savedInstanceState.getString("userID"));
        id.setText(savedInstanceState.getString("userEmail"));
        id.setText(savedInstanceState.getString("userPass1"));
        id.setText(savedInstanceState.getString("userPass2"));
    }
}