package communi.dog.aplicatiion;

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
                isEdit = false;
                dog_name.setEnabled(false);
                bio.setEnabled(false);
                my_location.setEnabled(false);
                contact.setEnabled(false);
                editProfile.setImageResource(R.drawable.ic_edit_profile);
                // todo: save the changes into DB
            } else {
                isEdit = true;
                dog_name.setEnabled(true);
                bio.setEnabled(true);
                my_location.setEnabled(true);
                contact.setEnabled(true);
                editProfile.setImageResource(R.drawable.ic_save_profile);
            }
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
    public void onBackPressed() {
        super.onBackPressed();
        backToMap();
    }
}