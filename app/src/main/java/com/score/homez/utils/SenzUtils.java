package com.score.homez.utils;

import android.content.Context;
import android.util.Log;

import com.score.homez.db.HomezDbSource;
import com.score.homez.exceptions.NoUserException;
import com.score.homez.pojos.Switch;
import com.score.senzc.enums.SenzTypeEnum;
import com.score.senzc.pojos.Senz;
import com.score.senzc.pojos.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by eranga on 2/2/16.
 */
public class SenzUtils {
    /**
     * Create PUT senz via switch
     *
     * @param aSwitch switch
     * @param context context
     * @return senz
     */

    public static Senz createPutSenz(Switch aSwitch, Context context) {
        HashMap<String, String> senzAttributes = new HashMap<>();
        senzAttributes.put(aSwitch.getName(), aSwitch.getStatus() == 1 ? "off" : "on");
        senzAttributes.put("time", ((Long) (System.currentTimeMillis() / 10)).toString());

        try {
            // get receiver
            User receiver = PreferenceUtils.getUser(context);

            // new senz
            String id = "_ID";
            String signature = "_SIGNATURE";
            SenzTypeEnum senzType = SenzTypeEnum.PUT;

            return new Senz(id, signature, senzType, null, receiver, senzAttributes);
        } catch (NoUserException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Senz createPutSenz(ArrayList<Switch> switchList, Context context) {
        HashMap<String, String> senzAttributes = new HashMap<>();
        for(Switch aSwitch:switchList) {
            senzAttributes.put(aSwitch.getName(), aSwitch.getStatus() == 1 ? "off" : "on");
        }
        senzAttributes.put("time", ((Long) (System.currentTimeMillis() / 10)).toString());

        try {
            // get receiver
            User receiver = PreferenceUtils.getUser(context);

            // new senz
            String id = "_ID";
            String signature = "_SIGNATURE";
            SenzTypeEnum senzType = SenzTypeEnum.PUT;

            return new Senz(id, signature, senzType, null, receiver, senzAttributes);
        } catch (NoUserException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create GET senz to find status of the switches
     *
     * @param ArrayList<Switch> switchList
     * @param context    context
     * @return senz
     */
    public static Senz createGetSenz(ArrayList<Switch> switchList,Context context) {
        try {
            // get receiver
            User receiver = PreferenceUtils.getUser(context);
            // new senz
            String id = "_ID";
            String signature = "_SIGNATURE";
            SenzTypeEnum senzType = SenzTypeEnum.GET;

            HashMap<String, String> senzAttributes = new HashMap<>();
            if (!switchList.isEmpty())
                for(Switch s:switchList) senzAttributes.put(s.getName(),"");
            senzAttributes.put("time", ((Long) (System.currentTimeMillis() / 10)).toString());
            return new Senz(id, signature, senzType, null, receiver, senzAttributes);
        } catch (NoUserException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Create GET senz to find get a photo
     * @param context    context
     * @return senz
     */
    public static Senz createGetPhotoSenze(Context context) {
        try {
            // get receiver
            User receiver = PreferenceUtils.getUser(context);
            // new senz
            String id = "_ID";
            String signature = "_SIGNATURE";
            SenzTypeEnum senzType = SenzTypeEnum.GET;
            HashMap<String, String> senzAttributes = new HashMap<>();
            senzAttributes.put("photo","");
            senzAttributes.put("time", ((Long) (System.currentTimeMillis() / 10)).toString());
            return new Senz(id, signature, senzType, null, receiver, senzAttributes);
        } catch (NoUserException e) {
            e.printStackTrace();
        }
        return null;
    }
}
