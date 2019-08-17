package itachi_waiyan.com.assignment2_implicitintent;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import itachi_waiyan.com.assignment2_implicitintent.util.Constants;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnTimer,btnCalendar,btnVideo,btnContact,btnWeb;
    VideoView videoView;
    LinearLayout llContact;
    TextView tvName,tvPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTimer = findViewById(R.id.btn_timer);
        btnCalendar = findViewById(R.id.btn_calendar);
        btnVideo = findViewById(R.id.btn_video);
        btnContact = findViewById(R.id.btn_contact);
        btnWeb = findViewById(R.id.btn_web);

        videoView = findViewById(R.id.video_view);
        llContact = findViewById(R.id.ll_contact);
        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);

        btnTimer.setOnClickListener(this);
        btnCalendar.setOnClickListener(this);
        btnVideo.setOnClickListener(this);
        btnContact.setOnClickListener(this);
        btnWeb.setOnClickListener(this);

        checkPermission();
    }

    @Override
    public void onClick(View view) {
        videoView.setVisibility(View.GONE);
        llContact.setVisibility(View.GONE);
        switch (view.getId()){
            case R.id.btn_timer : startTimer("Wake up",160);
                break;
            case R.id.btn_calendar : addEvent("Trip","Kalaw",123,234);
                break;
            case R.id.btn_video : captureVideo();
                break;
            case R.id.btn_contact : selectContact();
                break;
            case R.id.btn_web : webSearch();
                break;
        }
    }

    public void startTimer(String message, int seconds) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    public void addEvent(String title, String location, long begin, long end) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SET_ALARM) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(MainActivity.this,"Permission granted.",Toast.LENGTH_SHORT).show();
        }else {
            requestPermission();
            Toast.makeText(MainActivity.this,"Permission not granted.",Toast.LENGTH_SHORT).show();
        }
    }
    public void requestPermission(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Constants.ALARM_PERMISSION},1);
    }
    public void captureVideo(){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, Constants.REQUEST_VIDEO_CAPTURE);
        }
    }
    public void selectContact() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, Constants.REQUEST_SELECT_CONTACT);
        }
    }
    public void webSearch(){
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH );
        intent.putExtra(SearchManager.QUERY, "www.google.com");
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK){
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(data.getData());
            videoView.setMediaController(new MediaController(this));
            videoView.requestFocus();
            videoView.start();
        }
        else if(requestCode == Constants.REQUEST_SELECT_CONTACT && resultCode == RESULT_OK){

            Uri contactUri = data.getData();
            Log.d("uri---",contactUri.toString());
            String[] projection = new String []{ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
            Cursor cursor = getContentResolver().query(contactUri,projection,null,null,null);
            if(cursor != null && cursor.moveToFirst()){
                String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                llContact.setVisibility(View.VISIBLE);
                tvName.setText("Name - "+name);
                tvPhone.setText("Phone - "+phone);
            }
        }
    }
}
