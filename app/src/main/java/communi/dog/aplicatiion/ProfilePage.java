package communi.dog.aplicatiion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfilePage extends AppCompatActivity {

    // todo: why do i need it?
    EditText id;
    EditText password;
    EditText email;

    TextView username;
    EditText dog_name;
    EditText bio;
    EditText my_location;
    EditText contact;
    private ImageView editProfile;
    private boolean isEdit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // get data from the previous page
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");

        // todo: get from DB and don't pass with intent
        String userEmail = intent.getStringExtra("email");
        String userPassword = intent.getStringExtra("password");

        // todo: get the info from DB and

        username = findViewById(R.id.profile_user_name);
        dog_name = findViewById(R.id.profile_dog_name);
        bio = findViewById(R.id.profile_bio);
        my_location = findViewById(R.id.profile_location);
        contact = findViewById(R.id.profile_contact);

        ImageButton btnBackToMap = findViewById(R.id.backToMapFromProfile);
        editProfile = findViewById(R.id.profile_edit);

        dog_name.setEnabled(false);
        bio.setEnabled(false);
        my_location.setEnabled(false);
        contact.setEnabled(false);

        // todo: insert the data about the user from the db in the create

        editProfile.setOnLongClickListener(v -> {
            if (isEdit) {
                Toast.makeText(this, "click to save changes", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "click to edit", Toast.LENGTH_SHORT).show();
            }
            return false;
        });

        editProfile.setOnClickListener(v -> {
            if (isEdit) {
                //todo: save changes to DB
            }
            isEdit = !isEdit;
            dog_name.setEnabled(isEdit);
            bio.setEnabled(isEdit);
            my_location.setEnabled(isEdit);
            contact.setEnabled(isEdit);
            int edit_ic = isEdit ? R.drawable.ic_save_profile : R.drawable.ic_edit_profile;
            editProfile.setImageResource(edit_ic);
        });

        btnBackToMap.setOnClickListener(v -> {
            backToMap();
        });
    }

    private void backToMap() {
        Intent toMapIntent = new Intent(ProfilePage.this, MapScreenActivity.class);
        toMapIntent.putExtra("userId", getIntent().getStringExtra("userId"));
        startActivity(toMapIntent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_edit", isEdit);
        outState.putString("user_name", username.getText().toString());
        outState.putString("dog_name", dog_name.getText().toString());
        outState.putString("location", my_location.getText().toString());
        outState.putString("contact_info", contact.getText().toString());
        outState.putString("bio", bio.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean("is_edit")) {
            editProfile.setImageResource(R.drawable.ic_save_profile);
        } else {
            editProfile.setImageResource(R.drawable.ic_edit_profile);
        }
        username.setText(savedInstanceState.getString("user_name"));
        dog_name.setText(savedInstanceState.getString("dog_name"));
        my_location.setText(savedInstanceState.getString("location"));
        contact.setText(savedInstanceState.getString("contact_info"));
        bio.setText(savedInstanceState.getString("bio"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToMap();
    }
}