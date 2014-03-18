package vn.siliconstraits.airlight;


import Model.LightItem;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class FloorFragment extends Fragment {
    public int posFloor;
    public int posRoom;
    public static int pFloor;
    public static int pRoom;
    public static final String DATA_LIGHT = "DATA_LIGHT";
    public static final String SAVE_LIGHT = "SAVE_LIGHT";
    private static final String EXIST_LIGHT = "EXIST_LIGHT";
    public static final int ID_DEFAULT_BACKGROUND = 1000;
    private static final int DEFAULT_LOAD =6;
    public int ID_REAL_BACKGROUND;
    ImageView imageBackground;
    ImageView imageLight;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor shareEditor;
    private RelativeLayout relativeLayout;
    private RelativeLayout.LayoutParams lp;

    private ArrayList<LightItem> lightItemList;

    public FloorFragment(){}
    public static FloorFragment newInstance(int posFloor, int posRoom) {
        Bundle bundle = new Bundle();
        bundle.putInt("posFloor", posFloor);
        bundle.putInt("posRoom", posRoom);

        Log.i("RoomFragment", "check new Instance pos in fragment : " + posFloor);
        Log.i("RoomFragment", "check new Instance pos in fragment : " + posRoom);

        FloorFragment floorFragment = new FloorFragment();
        floorFragment.setArguments(bundle);
        return floorFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        sharedPreferences = this.getActivity().getSharedPreferences(DATA_LIGHT, 0);
        //set view layout
        View rootView = inflater.inflate(R.layout.fragment_light, container, false);
        relativeLayout = (RelativeLayout)rootView.findViewById(R.id.relativeRoom);
        Log.i("RoomFragment","check relativeLayout child count : "+relativeLayout.getChildCount());
        return rootView;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        posFloor = bundle.getInt("posFloor");
        posRoom = bundle.getInt("posRoom");
        pFloor = posFloor;
        pRoom = posRoom;
        Log.i("RoomFragment", "check pos in fragment : " + posFloor);
        Log.i("RoomFragment", "check pos in fragment : " + posRoom);
        ID_REAL_BACKGROUND = ID_DEFAULT_BACKGROUND+posFloor+posRoom;


    }
    @Override
    public void onStart()
    {
        super.onStart();
        Log.i("RoomFragment", "onStart");

        SharedPreferences sharedLight = getActivity().getSharedPreferences(SAVE_LIGHT,1);
        String exist = sharedLight.getString(EXIST_LIGHT,null);
        //check first create
        setImageBackground();
        if (exist !=null)
        {
            setImageBackgroundShared();
            setupLightSharedPrefer(posFloor, posRoom);
            Log.i("RoomFragment", "running load shared");
        }
        else
        {
            setImageBackground();
            setupLightData(posFloor, posRoom);
            Log.i("RoomFragment", "running load default");
        }

    }
    @Override
    public void onPause() {
        saveSharedPreference(lightItemList);
        super.onPause();
        Log.i("RoomFragment", "onPause");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.i("RoomFragment", "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("RoomFragment", "onDestroy");
    }

    public void setImageBackground()
    {
        imageBackground = new ImageView(getActivity());
        imageBackground.setId(ID_REAL_BACKGROUND);
        Log.i("RoomFragment", "check set id : " + imageBackground.getId());
        imageBackground.setTag(String.valueOf(ID_REAL_BACKGROUND));
        imageBackground.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageBackground.setImageResource(getResources().obtainTypedArray(R.array.list_image).getResourceId(12, 12));
        imageBackground.setScaleType(ImageView.ScaleType.FIT_XY);
        relativeLayout.addView(imageBackground);
        Log.d("RoomFragment", "REAL ID:"+imageBackground.getId());
    }

    //set image back ground with shared
    public void setImageBackgroundShared()
    {
        SharedPreferences sharedLight = getActivity().getSharedPreferences(SAVE_LIGHT,1);
        String url_background = sharedLight.getString(String.valueOf(ID_REAL_BACKGROUND),null);
        imageBackground = new ImageView(getActivity());
        imageBackground.setId(ID_REAL_BACKGROUND);
        imageBackground.setTag(String.valueOf(ID_REAL_BACKGROUND));
        imageBackground.setScaleType(ImageView.ScaleType.FIT_XY);
        File imgFile=null;
        if(url_background!=null)
            imgFile = new  File(url_background);
        try
        {
            if(imgFile!=null)
            {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageBackground.setImageBitmap(bitmap);
                imageBackground.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                Log.i("RoomFragment", "check setup background " + url_background);
                relativeLayout.addView(imageBackground);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            setImageBackground();
            Log.i("RoomFragment","Bug at setImageBackgroundShared");
        }
    }
    // set up light each room with data
    public void setupLightData(int posFloor, int posRoom)
    {
        try
        {   //get room
            JSONArray lightArray = getAllLight(posFloor, posRoom);
            lightItemList = new ArrayList<LightItem>();

            for (int i=0;i<lightArray.length();++i)
            {
                JSONObject lightObject = lightArray.getJSONObject(i);

                //add rule for light
                lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.ABOVE);

                //setup stuff image light
                imageLight = new ImageView(getActivity());
                imageLight.setLayoutParams(lp);
                imageLight.setMinimumHeight(100);
                imageLight.setMinimumWidth(100);
                imageLight.setId(i);
                imageLight.setTag(lightObject.getString("on_code"));

                //generate position
                Random random = new Random();
                int left = random.nextInt(500);
                int top = random.nextInt(500);
                lp.setMargins(left, top, 0, 0);

                //item light
                LightItem lightItem = new LightItem(i,imageLight.getLeft(),imageLight.getTop(),imageLight.getRotation());

                if (lightObject.getString("type").equals("Round"))
                {
                    imageLight.setImageResource(getResources().obtainTypedArray(R.array.list_image).getResourceId(0+DEFAULT_LOAD,0+DEFAULT_LOAD));
                    lightItem.setType("Round");
                    lightItem.setState(true);
                }
                else if (lightObject.getString("type").equals("Square"))
                {
                    imageLight.setImageResource(getResources().obtainTypedArray(R.array.list_image).getResourceId(2+DEFAULT_LOAD,2+DEFAULT_LOAD));
                    lightItem.setType("Square");
                    lightItem.setState(true);
                }
                else if (lightObject.getString("type").equals("Line"))
                {
                    imageLight.setImageResource(getResources().obtainTypedArray(R.array.list_image).getResourceId(4+DEFAULT_LOAD,4+DEFAULT_LOAD));
                    lightItem.setType("Line");
                    lightItem.setState(true);
                }
                Log.i("RoomFragment","check resource : "+imageLight.getDrawable().toString());
                //save name, x, y, rotation to list image item

                lightItem.setOn_Code(Integer.parseInt(lightObject.getString("on_code")));
                lightItem.setOff_code(Integer.parseInt(lightObject.getString("off_code")));
                lightItem.setState(true);
                lightItemList.add(lightItem);

                //set touch in image light
                //imageLight.setOnTouchListener(new MultiTouchListener());

                //add image light to layout
                relativeLayout.addView(imageLight,lp);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //setup light with shared prefer
    public void setupLightSharedPrefer(int posFloor, int posRoom)
    {
        RelativeLayout.LayoutParams layoutParamsShared;
        try
        {
            //get all light from data at position floor and room
            JSONArray lightArray = getAllLight(posFloor, posRoom);

            lightItemList = new ArrayList<LightItem>();

            for (int i=0;i<lightArray.length();++i)
            {
                //get all light saved
                SharedPreferences sharedLight = getActivity().getSharedPreferences(SAVE_LIGHT,1);
                layoutParamsShared = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                //get tag on light
                JSONObject lightObject = lightArray.getJSONObject(i);
                String on_code =  lightObject.getString("on_code");
                String value = sharedLight.getString(on_code,null);
                if (value !=null)
                {
                    imageLight = new ImageView(getActivity());

                    // x, y , rotation, width, height,state
                    String [] value_split = value.split(",");
                    Log.i("RoomFragment","onsetupLightSharedPrefer  "+value_split[0]+" "+ value_split[1]+" "+ value_split[2]+ " " + value_split[3]+" "+ value_split[4]+" "+value_split[5]);
                    layoutParamsShared.setMargins(Integer.parseInt(value_split[0]), Integer.parseInt(value_split[1]), 0, 0);
                    imageLight.setLayoutParams(layoutParamsShared);
                    imageLight.setId(i);
                    imageLight.setTag(lightObject.getString("on_code"));
                    imageLight.setRotation(Float.parseFloat(value_split[2]));
                    imageLight.setMinimumWidth(Integer.parseInt(value_split[3]));
                    imageLight.setMinimumHeight(Integer.parseInt(value_split[4]));

                    //set state and type of light
                    LightItem lightItem = new LightItem(i,imageLight.getLeft(),imageLight.getTop(),imageLight.getRotation());
                    if (lightObject.getString("type").equals("Round"))
                    {
                        Log.i("RoomFragment",value_split[5] );
                        if (value_split[5].equals("true"))
                        {
                            imageLight.setImageResource(R.drawable.light_round_on_blue);
                            lightItem.setType("Round");
                            lightItem.setState(true);
                        }
                        else if(value_split[5].equals("false"))
                        {
                            imageLight.setImageResource(R.drawable.light_round_off_blue);
                            lightItem.setType("Round");
                            lightItem.setState(false);
                        }
                    }
                    //set state and type of light
                    else if (lightObject.getString("type").equals("Square"))
                    {
                        if (value_split[5].equals("true"))
                        {
                            imageLight.setImageResource(R.drawable.light_square_on_blue);
                            lightItem.setType("Square");
                            lightItem.setState(true);
                        }
                        else if(value_split[5].equals("false"))
                        {
                            imageLight.setImageResource(R.drawable.light_square_off_blue);
                            lightItem.setType("Square");
                            lightItem.setState(false);
                        }
                    }
                    //set state and type of light
                    else if (lightObject.getString("type").equals("Line"))
                    {
                        if (value_split[5].equals("true"))
                        {
                            imageLight.setImageResource(R.drawable.light_straight_on_blue);
                            lightItem.setType("Line");
                            lightItem.setState(true);
                        }
                        else if(value_split[5].equals("false"))
                        {
                            imageLight.setImageResource(R.drawable.light_straight_off_blue);
                            lightItem.setType("Line");
                            lightItem.setState(false);
                        }
                    }
                    //add on/off code
                    lightItem.setOn_Code(Integer.parseInt(lightObject.getString("on_code")));
                    lightItem.setOff_code(Integer.parseInt(lightObject.getString("off_code")));

                    lightItemList.add(lightItem);

                    //set touch in image light
                    //imageLight.setOnTouchListener(new MultiTouchListener());

                    //add image light to layout
                    relativeLayout.addView(imageLight,layoutParamsShared);
                }
                else setupLightData(posFloor,posRoom);

            }
        }
        catch (Exception e)
        {
            Log.i("RoomFragment","Bug at setupLightSharedPrefer");
            e.printStackTrace();
        }
    }
    //save each light to shared preference with frame x(left with screen), y(top), rotation
    public void saveSharedPreference(ArrayList<LightItem> lightItemList)
    {
        SharedPreferences sharedLight = getActivity().getSharedPreferences(SAVE_LIGHT, 1);
        shareEditor = sharedLight.edit();
        //find image background with id default + pos floor and room
        Log.i("RoomFragment","check save bg : " + ID_REAL_BACKGROUND);
        ImageView imageBG = (ImageView)relativeLayout.findViewById(ID_REAL_BACKGROUND);
        //get image resource
        String tag_background = String.valueOf(imageBG.getTag());
        try
        {
            if (imageBG!= null)
            {
                Log.i("RoomFragment", "check tag background " + tag_background);
                Log.i("RoomFragment", "check id background" + ID_REAL_BACKGROUND);
                if (tag_background!="null")
                {
                    //save to shared prefer
                    shareEditor.putString(String.valueOf(ID_REAL_BACKGROUND), tag_background);
                    shareEditor.commit();
                }

            }
        }
        catch (Exception e)
        {
            Log.i("RoomFragment","check save image background");
            e.printStackTrace();
        }

        for (int i=0;i<lightItemList.size();++i)
        {
            final ImageView imageView = (ImageView)getView().findViewById(i);
            Rect bounds = imageView.getDrawable().getBounds();

            if(imageView!=null)
            {
                //get left and top with layout
                int x = (int) (imageView.getX() + bounds.left);
                int y = (int) (imageView.getY() + bounds.top);
                float scale = imageView.getScaleX();

                //get rotation
                float rotation = imageView.getRotation();

                //get width and height image scaled
                int width;
                int height;
                //check if width > max scaled
                if (imageView.getWidth()*scale>350)
                {
                    width = 100;
                }
                //check if height > max scaled
                else width = (int) (imageView.getWidth()*scale);
                if (imageView.getHeight()*scale>350)
                {
                    height = 100;
                }
                else height = (int)(imageView.getHeight()*scale);

                //state is on or off
                String state = String.valueOf(lightItemList.get(i).isState());
                Log.i("RoomFragment","onsaveSharedPreference : "+ x + ", " + y + ", " + rotation + ", " + width + ", " + height + ", " + String.valueOf(lightItemList.get(i).getOn_code()));

                //save x, y, rotation, width, height, state , scale,
                shareEditor.putString(String.valueOf(lightItemList.get(i).getOn_code()),
                        "" + x + "," + y + "," + rotation + "," + width + "," + height + "," + state);
                shareEditor.commit();
            }
        }
        shareEditor.putString(EXIST_LIGHT,"Exist");
        shareEditor.commit();
    }
    //get all light at pos floor and pos room
    private JSONArray getAllLight(int posFloor,int posRoom)
    {
        JSONArray lightArray = null;
        try
        {
            //get array json floor
            JSONArray floorsArray= new JSONArray(sharedPreferences.getString("floors_arduino", null));
            JSONObject floorObject = floorsArray.getJSONObject(posFloor);

            //get array json room
            JSONArray roomArray = floorObject.getJSONArray("rooms");
            JSONObject roomObject = roomArray.getJSONObject(posRoom);
            lightArray = roomObject.getJSONArray("lights");

            //return array light
            return lightArray;
        }
        catch (Exception e)
        {
            Log.i("RoomFragment", "Bug at getAllLight");
            e.printStackTrace();
        }
        return lightArray;
    }
}
