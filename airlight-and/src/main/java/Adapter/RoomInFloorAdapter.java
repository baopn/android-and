package Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.ViewGroup;
import vn.siliconstraits.airlight.FloorFragment;


import java.lang.reflect.Field;
import java.util.List;

public class RoomInFloorAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments;
    String[] rooms;
    int floor;

    public RoomInFloorAdapter(FragmentManager fm) {
        super(fm);
    }
    public RoomInFloorAdapter(FragmentManager fm, String[] rooms, int floor) {
        super(fm);
        this.rooms=rooms;
        this.floor=floor;
    }

    public RoomInFloorAdapter(FragmentManager fm, List<Fragment> fragments, String[] rooms, int floor) {
        super(fm);
        if (fm.getFragments() != null) {
            fm.getFragments().clear();
        }
        this.fragments = fragments;
        this.rooms = rooms;
        this.floor = floor;
    }
    @Override
    public Fragment getItem(int position) {
        Log.i("RoomFragment","Check position : "+position);
        Log.i("RoomFragment","Check 2 position : "+floor +" " +position);
        //return fragments.get(position);
        return FloorFragment.newInstance(floor, position);
    }

    @Override
    public int getCount() {
        return this.rooms.length;
    }
    //set name of room
    @Override
    public CharSequence getPageTitle(int position) {

        return rooms[position];
    }
    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Object fragment = super.instantiateItem(container, position);
        try {
            final Field saveFragmentStateField = Fragment.class.getDeclaredField("mSavedFragmentState");
            saveFragmentStateField.setAccessible(true);
            final Bundle savedFragmentState = (Bundle) saveFragmentStateField.get(fragment);
            if (savedFragmentState != null) {
                savedFragmentState.setClassLoader(Fragment.class.getClassLoader());
            }
        } catch (Exception e) {
            Log.w("RoomFragment", "Could not get mSavedFragmentState field: " + e);
        }
        return fragment;
    }
    public void changeFloor(int floor, List<Fragment> fragments, String[] rooms)
    {
        this.floor = floor;
        this.fragments = fragments;
        this.rooms = rooms;
        notifyDataSetChanged();
    }
    public int getFloor()
    {
        return this.floor;
    }

}