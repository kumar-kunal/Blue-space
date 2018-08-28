package com.space.blue.bluespace;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.TouchTypeDetector;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Button dialButton;
    Button messageButton;
    Button cameraButton;
    Button galleryButton;
    TextView timeTextView;
    TextView dateTextView;
    ArrayList<View> viewsMainScreen;
    LinearLayout dateTimeLayout;
    Timer t = new Timer();
    SimpleDateFormat simpleDateFormat;
    String time;
    String date;
    Calendar calander;
    RelativeLayout parentLayout;
    SharedPreferences sharedPreferences;
    LinearLayout mainScreenButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        dialButton = findViewById(R.id.dial_button);
        messageButton=findViewById(R.id.message_button);
        cameraButton=findViewById(R.id.camera_button);
        galleryButton=findViewById(R.id.gallery_button);
        timeTextView=findViewById(R.id.time_text_view);
        dateTextView=findViewById(R.id.date_text_view);
        dateTimeLayout = findViewById(R.id.date_time_layout);
        parentLayout=findViewById(R.id.parentLayout);
        mainScreenButtonLayout = findViewById(R.id.main_screen_button_layout);

        Sensey.getInstance().init(MainActivity.this);

        t.scheduleAtFixedRate(new TimerClass(),0,1000);


        new LovelyInfoDialog(MainActivity.this)
                .setTitle("Blue Space Tutorial")
                .setMessage("1. Swipe Up to open App Drawer\n2. You can customize Gesture Apps by long pressing the App Icon in the app drawer and choosing appropriate option\n" +
                        "You can change Gesture Apps by selecting another app for a particular Gesture")
                .setIcon(getResources().getDrawable(R.drawable.ic_launcher_background))
                .setTopColor(getResources().getColor(R.color.colorPrimaryDark))
                .setNotShowAgainOptionEnabled(0)
                .setNotShowAgainOptionChecked(false)
                .show();


        viewsMainScreen = new ArrayList<View>();
        for (int x = 0; x < parentLayout.getChildCount(); x++) {
            viewsMainScreen.add(parentLayout.getChildAt(x));
        }


        dateTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
                startActivity(i);
            }
        });


        dialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                startActivity(intent);
            }
        });

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setType("vnd.android-dir/mms-sms");
                startActivity(intent);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                startActivity(intent);
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivity(galleryIntent);
            }
        });


        TouchTypeDetector.TouchTypListener touchTypeDetector = new TouchTypeDetector.TouchTypListener(){

            @Override
            public void onTwoFingerSingleTap() {

            }

            @Override
            public void onThreeFingerSingleTap() {


            }

            @Override
            public void onDoubleTap() {

            }

            @Override
            public void onScroll(int i) {

            }

            @Override
            public void onSingleTap() {

            }

            @Override
            public void onSwipe(int i) {
                if(i == TouchTypeDetector.SWIPE_DIR_UP){
                    Intent intent = new Intent(getApplicationContext(),AppGrid.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onLongPress() {

            }
        };

        Sensey.getInstance().startTouchTypeDetection(MainActivity.this,touchTypeDetector);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Setup onTouchEvent for detecting type of touch gesture
        Sensey.getInstance().setupDispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }


    @Override
    protected void onPause() {
        super.onPause();
        PowerManager powerManager = (PowerManager)MainActivity.this.getSystemService(Context.POWER_SERVICE);
        Boolean isSceenAwake = (Build.VERSION.SDK_INT < 20? powerManager.isScreenOn():powerManager.isInteractive());

        if(!isSceenAwake){
            System.gc();
            Sensey.getInstance().stop();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        System.gc();
        Sensey.getInstance().stop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(sharedPreferences.getBoolean("showAppBar",false)){

            mainScreenButtonLayout.setBackgroundColor(getResources().getColor(R.color.appBarColor));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mainScreenButtonLayout.setElevation(4);
            }
        }
        else{
            mainScreenButtonLayout.setBackgroundColor(Color.TRANSPARENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mainScreenButtonLayout.setElevation(0);
            }
        }

        if(sharedPreferences.getBoolean("showClock",false)){
            dateTimeLayout.setVisibility(View.GONE);
        }
        else {
            dateTimeLayout.setVisibility(View.VISIBLE);
        }



    }

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                Intent it = new Intent(android.content.Intent.ACTION_VIEW);
                it.setData(Uri.parse("https://play.google.com/store/apps/details?id="+packageName));
                context.startActivity(it);
                return false;
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);

        } catch (ActivityNotFoundException e) {
            Intent i = new Intent(android.content.Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://play.google.com/store/apps/details?id="+packageName));
            context.startActivity(i);

        }
        return true;
    }


    private void setDateTime(){
        calander = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("h:mm a");
        time = simpleDateFormat.format(calander.getTime());
        simpleDateFormat = new SimpleDateFormat("EEE, dd MMM");
        date = simpleDateFormat.format(calander.getTime());
        timeTextView.setText(time);
        dateTextView.setText(date);
    }
    class TimerClass extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setDateTime();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

    }




}
