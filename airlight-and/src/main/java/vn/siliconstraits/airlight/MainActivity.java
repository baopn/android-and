package vn.siliconstraits.airlight;

import android.app.Activity;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private LinearLayout linearFloor;
    public static final String DATA_LIGHT = "DATA_LIGHT";

    private ArrayList<String> strFloor;
    private int maxFloor;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor shareEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new ViewPagerFragment()).commit();
        }
        linearFloor = (LinearLayout) findViewById(R.id.linearFloor);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        sharedPreferences = getSharedPreferences(DATA_LIGHT, 0);
        //extract file data
        ExtractData(data);


    }
    @Override
    public void onStart()
    {
        super.onStart();

        //get all name floor
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
