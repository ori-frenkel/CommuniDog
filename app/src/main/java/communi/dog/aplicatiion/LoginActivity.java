package communi.dog.aplicatiion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.widget.Button;
import android.text.Editable;
import android.text.TextWatcher;

public class LoginActivity extends AppCompatActivity {

    private HashMap<String, String> allUsers;
    private DatabaseReference usersRef;
    EditText idEditText;
    EditText userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        allUsers = new HashMap<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference iDsRef = database.getReference("Users");
        this.usersRef = iDsRef;

        Intent activityIntent = getIntent();

        idEditText = findViewById(R.id.input_id_login);
        userPassword = findViewById(R.id.user_password);

        TextView to_register_btn = findViewById(R.id.register_now);
        to_register_btn.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        findViewById(R.id.loginConstraintLayout).setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            idEditText.requestFocus();
            imm.hideSoftInputFromWindow(idEditText.getWindowToken(), 0);
            idEditText.clearFocus();
        });

        findViewById(R.id.login_button).setOnClickListener(v -> { //todo: check
            if (isUserExists(idEditText, userPassword)) {
                Intent successIntent = new Intent(this, MapScreenActivity.class);
                successIntent.putExtra("userId", idEditText.getText().toString());
                if (activityIntent.hasExtra("map_old_state")) {
                    successIntent.putExtra("map_old_state", activityIntent.getSerializableExtra("map_old_state"));
                }
                startActivity(successIntent);
                // todo: Move to other activity?
            } else {
                Toast.makeText(this, "id is unknown", Toast.LENGTH_SHORT).show(); //todo: new Toast?
            }
        });

        readDataIdsInUse(new FirebaseCallback() {
            @Override
            public void onCallback(HashMap<String, String> allUsers) {
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
                loginButton.setEnabled(!idEditText.getText().toString().equals("") && !userPassword.getText().toString().equals(""));
            }
        });

        userPassword.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                loginButton.setEnabled(!idEditText.getText().toString().equals("") && !userPassword.getText().toString().equals(""));
            }
        });
    }

    private boolean isUserExists(EditText id, EditText password) {
        String inputId = id.getText().toString();
        String inputPassword = password.getText().toString();
        return allUsers.get(inputId) != null &&
                Objects.equals(allUsers.get(inputId), inputPassword);
    }

    private interface FirebaseCallback {
        void onCallback(HashMap<String, String> allUsers);
    }

    private void readDataIdsInUse(LoginActivity.FirebaseCallback firebaseCallback) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds != null) {
                        String id = ds.child("id").getValue(String.class);
                        String password = ds.child("password").getValue(String.class);
                        allUsers.put(id, password);
                    }
                }
                firebaseCallback.onCallback(allUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        usersRef.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //todo: exit app - maybe ask if the user is sure
    }


    void checkDataEntered() {
        boolean valid_input = true;
        if (!isId(idEditText))
        {
            idEditText.setError("id is invalid!");
            valid_input = false;
        }
        if (isEmpty(userPassword))
        {
            userPassword.setError("password is missing");
            valid_input = false;
        }

        if (valid_input)
        {
            // todo: check in DB
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
    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
}