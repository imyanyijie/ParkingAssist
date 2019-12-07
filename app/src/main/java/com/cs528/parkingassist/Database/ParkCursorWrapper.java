package com.cs528.parkingassist.Database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.cs528.parkingassist.Model.Parking;
import com.cs528.parkingassist.Database.ParkDbSchema.ParkTable;


public class ParkCursorWrapper extends CursorWrapper {
    public ParkCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Parking getParking() {
        int pID = getInt(getColumnIndex(ParkTable.Cols.PID));
        String maker = getString(getColumnIndex(ParkTable.Cols.MAKER));
        String model = getString(getColumnIndex(ParkTable.Cols.MODEL));
        String color = getString(getColumnIndex(ParkTable.Cols.COLOR));
        String licence = getString(getColumnIndex(ParkTable.Cols.LICENCE));
        String description = getString(getColumnIndex(ParkTable.Cols.DESCRIPTION));
        double lat = getDouble(getColumnIndex(ParkTable.Cols.LAT));
        double lon = getDouble(getColumnIndex(ParkTable.Cols.LOGN));
        String time = getString(getColumnIndex(ParkTable.Cols.PTIME));
        Parking parking = new Parking(pID,maker,model,color,licence,lat,lon,description,time);
        parking.setDescription(description);
        return parking;
    }




}
