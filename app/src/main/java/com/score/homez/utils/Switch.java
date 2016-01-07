package com.score.homez.utils;

/**
 * Created by Anesu on 1/1/2016.
 */
public class Switch
{
    private String switchName;
    private int switchId;
    //status of 0 indicates off. status of 1 indicates on
    private int status;

    public Switch(String switchName, int switchId, int status)
    {
        this.switchId = switchId;
        this.switchName = switchName;
        this.status = status;
    }

    public void setSwitchName(String name)
    {
        this.switchName = name;
    }
    public void setSwitchId(int id)
    {
        this.switchId = id;
    }
    public void setStatus(int status)
    {
        this.status = status;
    }
    public String getSwitchName()
    {
        return switchName;
    }
    public int getSwitchId()
    {
        return switchId;
    }
    public int getStatus()
    {
        return status;
    }
}
