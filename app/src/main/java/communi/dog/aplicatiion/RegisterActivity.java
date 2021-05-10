package communi.dog.aplicatiion;

import androidx.annotation.NonNull;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    EditText id;
    EditText emailAddress;
    EditText pass1;
    EditText pass2;
    Button register;
    TextView to_register_btn;
    private ArrayList<String> allIDs;
    private ArrayList<String> allInUseIDs;
    private DatabaseReference IdsRef;
    private DatabaseReference usersRef;

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

        allIDs = new ArrayList<>();
        allInUseIDs = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference iDsRef = database.getReference("ID's");
        this.IdsRef = iDsRef;
        DatabaseReference usersRef = database.getReference("Users");
        this.usersRef = usersRef;


        readDataIds(new FirebaseCallback() {
            @Override
            public void onCallback(List<String> list) {
            }
        });

        readDataIdsInUse(new FirebaseCallback() {
            @Override
            public void onCallback(List<String> list) {
            }
        });

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

        if (isEmpty(id) || id.getText().toString().length() != 9) {
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

        //todo: don't check the db if the user failed before?

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
            addUser();

            Intent successIntent = new Intent(this, MainActivity.class); //todo: Maybe to MapScreenActivity?
            successIntent.putExtra("userId", id.getText().toString());
            startActivity(successIntent);
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

    private boolean idDoubleUser(EditText id) {
        for (String item: allInUseIDs){
            if (item.equals(id.getText().toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean idExistsInDB(EditText id){
        for (String item: allIDs){
            if (item.equals(id.getText().toString())) {
                return true;
            }
        }
        return false;
    }

    private interface FirebaseCallback{
        void onCallback(List<String> list);
    }

    private void readDataIds(FirebaseCallback firebaseCallback){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    if (ds != null) {
                        allIDs.add((String)ds.getValue());
                    }
                }
                firebaseCallback.onCallback(allIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        IdsRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void readDataIdsInUse(FirebaseCallback firebaseCallback){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    if (ds != null) {
                        String id = ds.child("id").getValue(String.class);
                        allInUseIDs.add(id);
                    }
                }
                firebaseCallback.onCallback(allInUseIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        usersRef.addListenerForSingleValueEvent(valueEventListener);
    }

    void addUser() {
        User newUser = new User(this.id.getText().toString(),
                this.emailAddress.getText().toString(), this.pass1.getText().toString());
        this.usersRef.push().setValue(newUser);
    }

}