package communi.dog.aplicatiion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MyProfileActivity extends AppCompatActivity {

    private String userId;
    private User currentUser;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        TextView id = findViewById(R.id.textView);

        Intent intent = getIntent();
        String gotId = intent.getStringExtra("id");
        String gotEmail = intent.getStringExtra("email");
        String gotPassword = intent.getStringExtra("password");

        currentUser = new User(gotId, gotEmail, gotPassword);

        id.setText(currentUser.getId());

        TextView email = findViewById(R.id.textView2);
        email.setText(currentUser.getEmail());
        TextView password = findViewById(R.id.textView3);
        password.setText(currentUser.getPassword());

    }



}