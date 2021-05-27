package communi.dog.aplicatiion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ProfilePage extends AppCompatActivity {

    // todo: why do i need it?
    EditText id;
    EditText password;
    EditText email;

    EditText username;
    EditText dog_name;
    EditText bio;
    EditText my_location;
    EditText contact;
    private ImageButton btnBackToMap;
    private TextView editProfile;


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

        btnBackToMap = findViewById(R.id.backToMapFromProfil);
        editProfile = findViewById(R.id.profile_edit);

        editProfile.setText("Edit");
        username.setEnabled(false);
        dog_name.setEnabled(false);
        bio.setEnabled(false);
        my_location.setEnabled(false);
        contact.setEnabled(false);


        editProfile.setOnClickListener(v -> {
            if (contact.isEnabled()) {
                username.setTextColor(Color.BLACK);
                dog_name.setTextColor(Color.BLACK);
                bio.setTextColor(Color.BLACK);
                my_location.setTextColor(Color.BLACK);
                contact.setTextColor(Color.BLACK);

                username.setEnabled(false);
                dog_name.setEnabled(false);
                bio.setEnabled(false);
                my_location.setEnabled(false);
                contact.setEnabled(false);
                editProfile.setText("Edit");
                editProfile.setBackgroundColor(Color.TRANSPARENT);
                editProfile.setTypeface(null, Typeface.NORMAL);

                // todo: save the changes into DB
            } else {
                username.setTextColor(Color.WHITE);
                dog_name.setTextColor(Color.WHITE);
                bio.setTextColor(Color.WHITE);
                my_location.setTextColor(Color.WHITE);
                contact.setTextColor(Color.WHITE);

                username.setEnabled(true);
                dog_name.setEnabled(true);
                bio.setEnabled(true);
                my_location.setEnabled(true);
                contact.setEnabled(true);
                editProfile.setText("Done");
                editProfile.setBackgroundColor(Color.WHITE);
                editProfile.setTypeface(null, Typeface.BOLD);
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