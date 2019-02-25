package com.example.brainhealth_meeting;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.brainhealth_meeting.dummy.DummyContent;
import com.example.brainhealth_meeting.bookingFragment;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.sql.Types;

import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

public class MainActivity
        extends AppCompatActivity
        implements profileFragment.OnFragmentInteractionListener,
        bookingFragment.OnListFragmentInteractionListener,
        upcomingFragment.OnListFragmentInteractionListener,
        friendFragment.OnListFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener {

    private Bundle argsForProfile;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_booking:
                    Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.toBookingFragment);
                    return true;
                case R.id.navigation_upcoming:
                    Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.toUpcomingFragment);
                    return true;
                case R.id.navigation_friend:
                    Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.toFriendFragment);
                    return true;
                case R.id.navigation_profile:
                    SharedPreferences prefs = getSharedPreferences("savedata", Context.MODE_PRIVATE);
                    int id = prefs.getInt("my_user_id", -1);
                    //SharedPreferences.Editor editor =prefs.edit();
                    //editor.clear();
                    //editor.commit();
                    if (id == -1) {
                        Log.e("app", "user id does not exit");
                    }
                    argsForProfile.putInt(profileFragment.ARG_PARAM1, id);
                    Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.toProfileFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        argsForProfile = new Bundle();

        Context context = this;
        SharedPreferences prefs = context.getSharedPreferences("savedata", Context.MODE_PRIVATE);
        int id = prefs.getInt("my_user_id", -1);
        if (id == -1) {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.toRegisterFragment);
        }

        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).addOnNavigatedListener(new NavController.OnNavigatedListener() {
            @Override
            public void onNavigated(@NonNull NavController controller, @NonNull NavDestination destination) {
                switch (destination.getId()) {
                    case R.id.profileFragment:
                        destination.setDefaultArguments(argsForProfile);
                        break;
                }
            }
        });

        //mTextMessage = (TextView) findViewById(R.id.message);


    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem uri) {
        //you can leave it empty
    }

    @Override // booking fragment
    public void onListFragmentInteraction(SimpleProfileContent.SimpleProfileItem item) {
        //System.out.println(item.user_name);
        argsForProfile.putInt(profileFragment.ARG_PARAM1, item.user_id);
        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.toProfileFragment);
        //you can leave it empty

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RegisterFragment.reg_choose_photo_code:
                    if (data != null) {

                        try {
                            Uri uri = data.getData();
                            ParcelFileDescriptor pfd = null;
                            pfd = getContentResolver().openFileDescriptor(uri, "r");
                            if (pfd != null) {
                                FileDescriptor fd = pfd.getFileDescriptor();
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFileDescriptor(fd, null, options);
                                if (options.outWidth > options.outHeight) {
                                    options.inSampleSize = (int) Math.ceil(options.outWidth / 128.0);
                                } else {
                                    options.inSampleSize = (int) Math.ceil(options.outHeight / 128.0);
                                }
                                options.inJustDecodeBounds = false;
                                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
                                pfd.close();
                                ImageView iv = findViewById(R.id.reg_profile_photo);
                                iv.setImageBitmap(bitmap);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RegisterFragment.request_permission_code && grantResults[0] == getPackageManager().PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, Types.ARRAY);
            }
            this.startActivityForResult(Intent.createChooser(intent, null), RegisterFragment.reg_choose_photo_code);
        }
    }
}
