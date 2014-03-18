package vn.siliconstraits.airlight;

import Adapter.RoomInFloorAdapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.viewpagerindicator.TitlePageIndicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    ViewPager viewPager;
    Fragment fragment;
    TitlePageIndicator titlePageIndicator;

    public static final String DATA_LIGHT = "DATA_LIGHT";
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

        viewPager = (ViewPager)getView().findViewById(R.id.vpager);
        titlePageIndicator = (TitlePageIndicator)getView().findViewById(R.id.titleIndicator);

    }
    @Override
    public void onStart()
    {
        super.onStart();
        String[] nameroom = getAllNameRoom(0);
        List<Fragment> fragments = setupFragmentInFloor(0);
        RoomInFloorAdapter roomInFloorAdapter = new RoomInFloorAdapter(getFragmentManager(), fragments, nameroom,0);
        viewPager.setAdapter(roomInFloorAdapter);
        titlePageIndicator.setViewPager(viewPager,0);
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
