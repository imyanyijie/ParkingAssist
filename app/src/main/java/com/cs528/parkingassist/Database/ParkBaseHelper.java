package com.cs528.parkingassist.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.cs528.parkingassist.Database.ParkDbSchema.ParkTable;

public class ParkBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "ParkBaseHelper";
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "parkBase.db";

    public ParkBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + ParkTable.NAME + "(" +
                ParkTable.Cols.PID + " integer primary key autoincrement, " +
                ParkTable.Cols.MAKER + ", " +
                ParkTable.Cols.MODEL + ", " +
                ParkTable.Cols.COLOR + ", " +
                ParkTable.Cols.LICENCE + ", " +
                ParkTable.Cols.LAT + ", " +
                ParkTable.Cols.LOGN + ", " +
                ParkTable.Cols.DESCRIPTION + ", " +
                ParkTable.Cols.PTIME +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
