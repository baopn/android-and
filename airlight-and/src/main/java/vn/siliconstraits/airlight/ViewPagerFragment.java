package vn.siliconstraits.airlight;

import Adapter.RoomInFloorAdapter;
import Adapter.SwipeDetect;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.viewpagerindicator.TitlePageIndicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerFragment extends Fragment {
    private SharedPreferences sharedPreferences;

    ViewPager viewPager;
    private ImageButton btnSetting;
    TitlePageIndicator titlePageIndicator;
    RoomInFloorAdapter roomInFloorAdapter;
    public static final String DATA_LIGHT = "DATA_LIGHT";
    int currentFloor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        //set view layout
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(DATA_LIGHT, 0);
        btnSetting = (ImageButton)getView().findViewById(R.id.btnSettings);
        viewPager = (ViewPager)getView().findViewById(R.id.vpager);
        titlePageIndicator = (TitlePageIndicator)getView().findViewById(R.id.titleIndicator);
        currentFloor = 0;
    }
    @Override
    public void onStart()
    {
        super.onStart();
        String[] nameroom = getAllNameRoom(0);
        List<Fragment> fragments = setupFragmentInFloor(0);
        roomInFloorAdapter = new RoomInFloorAdapter(getActivity().getSupportFragmentManager(), fragments, nameroom,0);
        viewPager.setAdapter(roomInFloorAdapter);
        titlePageIndicator.setViewPager(viewPager,0);


        SwipeDetect swipeDetector = new SwipeDetect(new SwipeDetect.OnSwipeListener() {
            @Override
            public void callback(String swipe) {
                if(swipe.equals("Down"))
                {
                    currentFloor -=1;
                    String[] nameroom = getAllNameRoom(currentFloor);
                    Log.i("RoomFragment", "check nameroom : "+nameroom.length);
                    List<Fragment> fragments = setupFragmentInFloor(currentFloor);
                    Log.i("RoomFragment", "check length fragment  : "+fragments.size());
                    roomInFloorAdapter.changeFloor(currentFloor, fragments,nameroom);
                    titlePageIndicator.setViewPager(viewPager, 0);
                    titlePageIndicator.notifyDataSetChanged();

                    Animation slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
                    viewPager.startAnimation(slideDown);
                }
                if(swipe.equals("Up"))
                {
                    currentFloor +=1;
                    String[] nameroom = getAllNameRoom(currentFloor);
                    Log.i("RoomFragment", "check nameroom : "+nameroom.length);
                    List<Fragment> fragments = setupFragmentInFloor(currentFloor);
                    Log.i("RoomFragment", "check length fragment  : "+fragments.size());
                    roomInFloorAdapter.changeFloor(currentFloor, fragments,nameroom);
                    titlePageIndicator.setViewPager(viewPager, 0);
                    titlePageIndicator.notifyDataSetChanged();

                    Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
                    viewPager.startAnimation(slideUp);
                }
            }
        });

        //settouch vp
        viewPager.setOnTouchListener(swipeDetector);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //get all name room of floor
    private String[] getAllNameRoom(int posFloor) {
        String[] nameRoom = null;
        try {
            JSONArray floorsArray = new JSONArray(this.sharedPreferences.getString("floors_arduino", null));
            JSONObject floorObject = floorsArray.getJSONObject(posFloor);
            JSONArray roomArray = new JSONArray(floorObject.getString("rooms"));
            nameRoom = new String[roomArray.length()];

            for (int j = 0; j < roomArray.length(); ++j) {
                JSONObject roomObject = roomArray.getJSONObject(j);
                nameRoom[j] = roomObject.getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nameRoom;
    }
    //add all fragment in floor
    private List<Fragment> setupFragmentInFloor(int position) {
        Log.i("RoomFragment", "check setup pos :" + position);
        List<Fragment> fragments = new ArrayList<Fragment>();
        try {
            JSONArray floorsArray = new JSONArray(this.sharedPreferences.getString("floors_arduino", null));
            JSONObject floorObject = floorsArray.getJSONObject(position);
            JSONArray roomArray = floorObject.getJSONArray("rooms");
            for (int i = 0; i < roomArray.length(); ++i) {
                FloorFragment roomFragment = FloorFragment.newInstance(position, i);
                fragments.add(roomFragment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fragments;
    }
}
