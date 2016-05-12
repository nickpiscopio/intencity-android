package com.intencity.intencity.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The model class for the rows in each routine group.
 *
 * Created by Nick Piscopio on 5/5/16.
 */
public class RoutineRow implements Parcelable
{
    private String title;

    private int rowNumber;

    private boolean selected = false;
    private boolean checked = true;

    public RoutineRow(String title, int rowNumber)
    {
        this.title = title;
        this.rowNumber = rowNumber;
    }

    private RoutineRow(Parcel in)
    {
        title = in.readString();
        rowNumber = in.readInt();
        selected = in.readInt() == 1;
        checked = in.readInt() == 1;
    }

    public static final Creator<RoutineRow> CREATOR = new Creator<RoutineRow>()
    {
        @Override
        public RoutineRow createFromParcel(Parcel in)
        {
            return new RoutineRow(in);
        }

        @Override
        public RoutineRow[] newArray(int size)
        {
            return new RoutineRow[size];
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
        dest.writeString(title);
        dest.writeInt(rowNumber);
        dest.writeInt(selected ? 1 : 0);
        dest.writeInt(checked ? 1 : 0);
    }

    /**
     * Getters and setters for the exercise model.
     */
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getRowNumber()
    {
        return rowNumber;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }
}