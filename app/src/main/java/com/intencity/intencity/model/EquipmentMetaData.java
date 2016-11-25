package com.intencity.intencity.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The model class for the meta data for equipment.
 *
 * Created by Nick Piscopio on 11/25/16.
 */
public class EquipmentMetaData implements Parcelable
{
    private String displayName;
    private String location;

    public EquipmentMetaData(String displayName, String location)
    {
        this.displayName = displayName;
        this.location = location;
    }

    private EquipmentMetaData(Parcel in)
    {
        displayName = in.readString();
        location = in.readString();
    }

    public static final Creator<EquipmentMetaData> CREATOR = new Creator<EquipmentMetaData>()
    {
        @Override
        public EquipmentMetaData createFromParcel(Parcel in)
        {
            return new EquipmentMetaData(in);
        }

        @Override
        public EquipmentMetaData[] newArray(int size)
        {
            return new EquipmentMetaData[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(displayName);
        dest.writeString(location);
    }

    /**
     * Getters and setters for the exercise model.
     */
    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }
}