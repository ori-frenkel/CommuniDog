package communi.dog.aplicatiion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;


public class MyProfileActivity extends AppCompatActivity {

    EditText id;
    EditText password;
    EditText email;
    Button buttonUpdateUSer;
    Button buttonDeleteUser;
    private String userId;
    private User currentUser;
    private DB appDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        this.appDB = new DB();
        Intent intent = getIntent();
        this.appDB.restoreState((DB.DBState) intent.getSerializableExtra("DB"));
        String gotId = intent.getStringExtra("userId");
        String gotEmail = intent.getStringExtra("email");
        String gotPassword = intent.getStringExtra("password");
        this.appDB.refreshDataUsers();


        currentUser = new User(gotId, gotEmail, gotPassword);

        id = findViewById(R.id.input_id);
        password = findViewById(R.id.input_password);
        email = findViewById(R.id.input_email);
        buttonUpdateUSer = findViewById(R.id.button);
        buttonDeleteUser = findViewById(R.id.button2);

        id.setText(currentUser.getId());
        password.setText(currentUser.getPassword());
        email.setText(currentUser.getEmail());

        buttonUpdateUSer.setOnClickListener(v -> {
            this.appDB.updateUser(id.getText().toString(), email.getText().toString(),
                    password.getText().toString());
        });

        //todo: Just for learning!!! it will be deleted!
        buttonDeleteUser.setOnClickListener(v -> {
            this.appDB.deleteUser(id.getText().toString());
            Intent intent1 = new Intent(this, LoginActivity.class);
            startActivity(intent1);
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent oldIntent = getIntent();
        Intent openMapIntent = new Intent(this, MapScreenActivity.class);
        if (oldIntent.hasExtra("map_old_state")) {
            openMapIntent.putExtra("map_old_state", oldIntent.getSerializableExtra("map_old_state"));
        }
        openMapIntent.putExtra("userId", oldIntent.getStringExtra("userId"));
        startActivity(openMapIntent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("userID", id.getText().toString());
        outState.putString("userPassword", password.getText().toString());
        outState.putString("userEmail", email.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        id.setText(savedInstanceState.getString("userID"));
        password.setText(savedInstanceState.getString("userPassword"));
        email.setText(savedInstanceState.getString("userEmail"));
    }
}