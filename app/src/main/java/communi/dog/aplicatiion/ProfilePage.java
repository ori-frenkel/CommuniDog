package communi.dog.aplicatiion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfilePage extends AppCompatActivity {
    User currentUser;

    TextView usernameEditText;
    EditText dogNameEditText;
    EditText emailEditText;
    EditText phoneEditText;
    EditText bioEditText;
    private ImageView editProfile;
    private boolean isEdit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        currentUser = CommuniDogApp.getInstance().getDb().getUser();

        // todo: get the info from DB and

        usernameEditText = findViewById(R.id.profile_user_name);
        dogNameEditText = findViewById(R.id.profile_dog_name);
        emailEditText = findViewById(R.id.usersEmailMyProfile);
        phoneEditText = findViewById(R.id.usersPhoneMyProfile);
        bioEditText = findViewById(R.id.profile_bio);

        TextView btnMYMarker = findViewById(R.id.profile_to_my_marker);
        ImageButton btnBackToMap = findViewById(R.id.backToMapFromProfile);
        editProfile = findViewById(R.id.profile_edit);

        dogNameEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        phoneEditText.setEnabled(false);
        bioEditText.setEnabled(false);

        // todo: insert the data about the user from the db in the create
        usernameEditText.setText(currentUser.getId()); // todo: change to getName
//        dogNameEditText.setText(currentUser.getDogName()); // todo: update User class + DB
        emailEditText.setText(currentUser.getEmail());
        phoneEditText.setText(currentUser.getPhoneNumber());
//        bioEditText.setText(currentUser.getBio()); // todo: update User class + DB

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
            dogNameEditText.setEnabled(isEdit);
            bioEditText.setEnabled(isEdit);
            emailEditText.setEnabled(isEdit);
            phoneEditText.setEnabled(isEdit);
            int edit_ic = isEdit ? R.drawable.ic_save_profile : R.drawable.ic_edit_profile;
            editProfile.setImageResource(edit_ic);
        });

        btnBackToMap.setOnClickListener(v -> backToMap());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_edit", isEdit);
        outState.putString("user_name", usernameEditText.getText().toString());
        outState.putString("dog_name", dogNameEditText.getText().toString());
        outState.putString("email", emailEditText.getText().toString());
        outState.putString("phone", phoneEditText.getText().toString());
        outState.putString("bio", bioEditText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isEdit = savedInstanceState.getBoolean("is_edit");
        int edit_ic = isEdit ? R.drawable.ic_save_profile : R.drawable.ic_edit_profile;
        editProfile.setImageResource(edit_ic);
        usernameEditText.setText(savedInstanceState.getString("user_name"));
        dogNameEditText.setText(savedInstanceState.getString("dog_name"));
        emailEditText.setText(savedInstanceState.getString("email"));
        phoneEditText.setText(savedInstanceState.getString("phone"));
        bioEditText.setText(savedInstanceState.getString("bio"));
    }

    private void backToMap() {
        Intent toMapIntent = new Intent(ProfilePage.this, MapScreenActivity.class);
        // todo: no need to pass userId anymore
        toMapIntent.putExtra("userId", getIntent().getStringExtra("userId"));
        startActivity(toMapIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToMap();
    }
}