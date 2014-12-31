package com.seeyou.ui.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.seeyou.R;
import com.seeyou.ui.fragments.CYMapFragment;
import com.seeyou.ui.fragments.ContactsFragment;
import com.seeyou.ui.fragments.FeedFragment;
import com.seeyou.ui.fragments.InboxFragment;
import com.seeyou.ui.fragments.NavigationMenuFragment;
import com.seeyou.ui.fragments.SelectorFragment;
import com.seeyou.ui.view.TheButton;
import com.seeyou.ui.view.TheButtonTouchListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends Activity {
    public static final int REQUEST_CODE_TAKE_PHOTO = 101;

    @InjectView(R.id.theButton)
    TheButton theButton;
    @InjectView(R.id.hamburger)
    ImageButton hamburgerButton;
    @InjectView(R.id.content)
    View content;


    Uri mNewAvatarUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setTheButton();

        if (savedInstanceState == null) {
            Fragment mapFragment = CYMapFragment.newInstance();
            getFragmentManager().beginTransaction().add(R.id.container, mapFragment, "map").commit();
        }

    }

    @OnClick(R.id.hamburger)
    public void switchMainScreen() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Fragment navMenu = getFragmentManager().findFragmentByTag(NavigationMenuFragment.TAG);
        if (navMenu == null) {
            navMenu = NavigationMenuFragment.newInstance();
            transaction.add(R.id.container, navMenu, NavigationMenuFragment.TAG).addToBackStack(null).commit();
        } else {
            transaction.attach(navMenu).addToBackStack(null).commit();
        }
    }

    public void setControlsVisibility(boolean visible) {
        int flag = visible ? View.VISIBLE : View.GONE;
        theButton.setVisibility(flag);
        hamburgerButton.setVisibility(flag);
    }

    private void setTheButton() {
        theButton.setText("" + 17, "" + 3);
        theButton.setOnTouchListener(new TheButtonTouchListener(theButton));
        TheButton.TheButtonCommand left = new TheButton.TheButtonCommand() {
            @Override
            public void execute() {
                getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.container, new InboxFragment()).commit();
            }
        };

        TheButton.TheButtonCommand right = new TheButton.TheButtonCommand() {
            @Override
            public void execute() {
                getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.container, new ContactsFragment()).commit();
            }
        };

        TheButton.TheButtonCommand up = new TheButton.TheButtonCommand() {
            @Override
            public void execute() {
                getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.container, new SelectorFragment()).commit();
            }
        };

        TheButton.TheButtonCommand down = new TheButton.TheButtonCommand() {
            @Override
            public void execute() {
                getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.container, new FeedFragment()).commit();
            }
        };

        TheButton.TheButtonCommand none = new TheButton.TheButtonCommand() {
            @Override
            public void execute() {
                Toast.makeText(MainActivity.this, "none", Toast.LENGTH_SHORT).show();
            }
        };

        TheButton.TheButtonCommand click = new TheButton.TheButtonCommand() {
            @Override
            public void execute() {
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date());
                        String fileName = "JPEG_" + timeStamp;
                        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        photoFile = File.createTempFile(fileName, ".jpg", storageDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (photoFile != null) {
                        mNewAvatarUri = Uri.fromFile(photoFile);
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mNewAvatarUri);
                        startActivityForResult(takePhotoIntent, REQUEST_CODE_TAKE_PHOTO);
                    }
                }
            }
        };

        theButton.setButtonCommand(TheButton.Action.LEFT, left);
        theButton.setButtonCommand(TheButton.Action.RIGHT, right);
        theButton.setButtonCommand(TheButton.Action.UP, up);
        theButton.setButtonCommand(TheButton.Action.DOWN, down);
        theButton.setButtonCommand(TheButton.Action.NONE, none);
        theButton.setButtonCommand(TheButton.Action.CLICK, click);

    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                if (mNewAvatarUri != null) {
                   /* ImagesFileHandler.setRightRotationAndSave(mNewAvatarUri.getPath());

                    AvatarUpdate avatarUpdate = new AvatarUpdate(mNewAvatarUri.getPath(), mAvatarTimestamp);
                    mProgressFragment.show(getFragmentManager(), ProgressFragment.TAG);
                    mNetworkService.addAvatar(mAddAvatarListener, avatarUpdate);

                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(mNewAvatarUri);
                    getActivity().sendBroadcast(mediaScanIntent);*/
                } else {
                    //((BaseActivity) getActivity()).alertDialog(getString(R.string.could_not_decode_image) + "\n" + getString(R.string.try_again));
                }

            }
        }
    }
}
