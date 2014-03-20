package vn.siliconstraits.airlight;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends FragmentActivity {

    private LinearLayout linearFloor;
    public static final String DATA_LIGHT = "DATA_LIGHT";
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CHANGE_IMAGE_BACKGROUND_REQUEST_CODE = 101;
    private ArrayList<String> strFloor;
    private int maxFloor;
    private ImageButton btnSetting;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor shareEditor;
    ViewPagerFragment viewPagerFragment;
    private FragmentManager mFragmentManager;

    boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearFloor = (LinearLayout) findViewById(R.id.linearFloor);
        btnSetting = (ImageButton) findViewById(R.id.btnSettings);
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        sharedPreferences = getSharedPreferences(DATA_LIGHT, 0);
        //extract file data
        ExtractData(data);

        //On start
        strFloor = getNameFloor();

        //get max floor
        maxFloor = getMaxFloor();

        //set text floor in layout
        for (int i = 0; i < strFloor.size(); ++i) {
            System.out.println(strFloor.get(i));
            TextView txtFloor = new TextView(this);
            txtFloor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            txtFloor.setId(i);
            if (i == 0)
                txtFloor.setTextColor(Color.GREEN);
            txtFloor.setTag("txtFloor" + i);
            Log.i("RoomFragment", "Check tag txtFloor : " + txtFloor.getTag());
            txtFloor.setPadding(0, 150, 0, 0);
            txtFloor.setText(strFloor.get(i).toString());
            linearFloor.addView(txtFloor);
        }
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.dialog_setting);
                // Set dialog title
                dialog.setTitle("More Option");
                TextView txtCamera = (TextView) dialog.findViewById(R.id.txtCamera);
                TextView txtGallery = (TextView) dialog.findViewById(R.id.txtGallery);
                TextView txtDefault = (TextView) dialog.findViewById(R.id.txtDefault);

                txtCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intentCamera.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intentCamera, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                        }
                        dialog.dismiss();
                    }
                });

                txtGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        //intent.setType("image*//*");
                        startActivityForResult(intent, CHANGE_IMAGE_BACKGROUND_REQUEST_CODE);

                    }
                });
                txtDefault.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        //On resume
        Log.i("RoomFragment", "MainActivityonResume");

        mFragmentManager = getSupportFragmentManager();

        if (mFragmentManager != null) {
            Fragment test = mFragmentManager.findFragmentByTag("test");
            if (test == null)
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new ViewPagerFragment(), "test").commit();
            else
                mFragmentManager.beginTransaction().replace(R.id.fragment, test).commit();
        } else
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new ViewPagerFragment(), "test").commit();

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        Log.i("RoomFragment", "check MainActivityonDestroy");
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_CANCELED || data == null || data.equals(null)) {
                    Toast.makeText(getApplicationContext(), "Cancel camera", 1000).show();
                } else if (resultCode == RESULT_OK && data != null) {
                    try {
                        //Get image from camera and change background image
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        Log.i("RoomFragment", "check image camera : " + photo.toString());

                        //Save image into sd card
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Airlight/");
                        boolean success = true;

                        // create folder if not exist
                        if (!folder.exists()) success = folder.mkdirs();
                        //exist folder
                        if (success) {
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            File file = new File(folder, "IMG_" + timeStamp + ".jpg");
                            file.createNewFile();
                            FileOutputStream fo = new FileOutputStream(file);
                            fo.write(bytes.toByteArray());
                            fo.close();
                            Log.i("RoomFragment", "check file save : " + file.getPath().toString());

                            //update file to gallery
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));
                        } else {
                            Toast.makeText(this, "Failure create directory", 1000).show();
                        }
                    } catch (IOException e) {
                        Log.i("RoomFragment", "Bug at camera image");
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Failure capture image", 1000).show();
                }
            }

            //Change image background from gallery
            if (requestCode == CHANGE_IMAGE_BACKGROUND_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_CANCELED || data == null || data.equals(null)) {
                    Toast.makeText(getApplicationContext(), "Cancel Gallery", 1000).show();
                    Log.i("RoomFragment", "cancel gallery");

                } else if (resultCode == Activity.RESULT_OK) {
                    //int id = Integer.parseInt(RoomFragment.ID_DEFAULT_BACKGROUND + "" + RoomFragment.pFloor + "" + RoomFragment.pRoom);

                    //Log.d("RoomFragment", "check id change bg" + id);
                    //ImageView imageView = (ImageView) findViewById(id);
                    try {
                        Uri targetUri = data.getData();
                        Log.i("RoomFragment", "Check change : " + targetUri);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), targetUri);

                        //imageView.setImageBitmap(bitmap);
                        //imageView.setTag(targetUri);
                        //shareEditor = sharedPreferences.edit();
                        //shareEditor.putString(String.valueOf(id), String.valueOf(picturePath));
                    } catch (Exception e) {
                        Log.i("RoomFragment", "Bug at gallery");
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("RoomFragment", "Bug try settings");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("RoomFragment", "MainActivityonPause");
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.i("RoomFragment", "MainActivityonStop");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("message", "Da load roi");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    // use shared preferences to store data(string,string,string,arrayjson)
    public void ExtractData(String data) {

        shareEditor = sharedPreferences.edit();
        try {
            JSONArray jsonArray = new JSONArray(data);

            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonArduino = jsonArray.getJSONObject(i);
                shareEditor.putString("name_arduino", jsonArduino.getString("name"));
                shareEditor.putString("id_arduino", jsonArduino.getString("id"));
                shareEditor.putString("host_arduino", jsonArduino.getString("host"));
                shareEditor.putString("floors_arduino", jsonArduino.getString("floors").toString());
                shareEditor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get name of floor in data
    public ArrayList<String> getNameFloor() {
        ArrayList<String> listFloor = new ArrayList<String>();

        try {
            JSONArray floorsArray = new JSONArray(sharedPreferences.getString("floors_arduino", null));
            for (int i = 0; i < floorsArray.length(); ++i) {
                JSONObject floorObject = floorsArray.getJSONObject(i);
                listFloor.add(floorObject.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listFloor;
    }

    //get max floor with data
    private int getMaxFloor() {
        int maxFloor = 0;
        try {
            JSONArray floorsArray = new JSONArray(this.sharedPreferences.getString("floors_arduino", null));
            maxFloor = floorsArray.length();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Light", "Bug at getMaxFloor");
        }
        return maxFloor;
    }

}
