package communi.dog.aplicatiion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class NotificationsActivity extends AppCompatActivity {
    ListView listView;
    Mail m;
    boolean success;
    String mailUserName;
    String mailPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        listView = (ListView)findViewById(R.id.listView);
        mailUserName = "sagi5362@gmail.com";
        mailPW = "0546906507";

        // initialize general account that will send mail
        m = new Mail(mailUserName, mailPW);

        ArrayList<String> arrayList = new ArrayList<>();

        //TODO need to fill this with applications in DB
        arrayList.add("sagi5362@gmail.com");
        arrayList.add("sagi.dekel@mail.huji.ac.il");
        arrayList.add("sagi5362@gmail.com");
        arrayList.add("sagi5362@gmail.com");

        //Assign listview
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Initialize confirmation mail
                String[] to =new String[1];
                to[0] = arrayList.get(i).toString();
                m.setTo(to);
                m.setFrom(mailUserName);
                m.setSubject("Confirmation from CommuniDog");

                //get confirmation code to send
                Random rand = new Random();
                int confirmationCode = rand.nextInt(899999)+100000;
                m.setBody(Integer.toString(confirmationCode));

                //TODO add confirmation code to DB

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try  {
                            success = m.send();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(success) {
                    // Mail was sent, remove the reciever from listview
                    arrayList.remove(i);
                    arrayAdapter.notifyDataSetChanged();

                    // Dialog for mail sent succussfully
                    AlertDialog.Builder builder = new AlertDialog.Builder(NotificationsActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle(Html.fromHtml("<font color='#509324'>Success</font>"));
                    builder.setMessage("Mail sent successfully");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }
                else{
                    // Dialog for mail failed to send
                    AlertDialog.Builder builder = new AlertDialog.Builder(NotificationsActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle(Html.fromHtml("<font color='#509324'>Failure</font>"));
                    builder.setMessage("Failed to send mail");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });
    }
}