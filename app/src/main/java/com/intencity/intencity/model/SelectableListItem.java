package com.intencity.intencity.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The model class for the rows in each routine group.
 *
 * Created by Nick Piscopio on 5/5/16.
 */
public class SelectableListItem implements Parcelable
{
    private String title;
    private String description;

    private int rowNumber;

    private boolean selected = false;
    private boolean checked = true;

    public SelectableListItem(String title)
    {
        this.title = title;
    }

    public SelectableListItem(String title, String description)
    {
        this.title = title;
        this.description = description;
    }

    public SelectableListItem(String title, int rowNumber)
    {
        this.title = title;
        this.rowNumber = rowNumber;
    }

    private SelectableListItem(Parcel in)
    {
        title = in.readString();
        description = in.readString();
        rowNumber = in.readInt();
        selected = in.readInt() == 1;
        checked = in.readInt() == 1;
    }

    public static final Creator<SelectableListItem> CREATOR = new Creator<SelectableListItem>()
    {
        @Override
        public SelectableListItem createFromParcel(Parcel in)
        {
            return new SelectableListItem(in);
        }

        @Override
        public SelectableListItem[] newArray(int size)
        {
            return new SelectableListItem[size];
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
        dest.writeString(description);
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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
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