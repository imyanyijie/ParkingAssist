package com.cs528.parkingassist.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;

import com.cs528.parkingassist.Model.Parking;
import com.cs528.parkingassist.Database.ParkDbSchema.ParkTable;
import com.cs528.parkingassist.Util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParkPersistance {
    private static ParkPersistance parkPersistance;

    private Context mContext;
    private SQLiteDatabase mDatabase;


    public static ParkPersistance get_instance(Context context) {

        if (parkPersistance == null) {
            parkPersistance = new ParkPersistance(context);
        }
        return parkPersistance;
    }

    private ParkPersistance(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ParkBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addParking(Parking p) {
        ContentValues values = getContentValues(p);
        mDatabase.insert(ParkTable.NAME, null, values);
    }

    public List<Parking> getParkings() {
        List<Parking> parkings = new ArrayList<>();

        ParkCursorWrapper cursor = queryCrimes(null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            parkings.add(cursor.getParking());
            cursor.moveToNext();
        }
        cursor.close();
        return parkings;
    }


    public void removeParking() {
        mDatabase.execSQL("delete from " + ParkTable.NAME);
    }

    private ParkCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ParkTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new ParkCursorWrapper(cursor);
    }


    private static ContentValues getContentValues(Parking parking) {
        ContentValues values = new ContentValues();
        values.put(ParkTable.Cols.MAKER, parking.getMaker());
        values.put(ParkTable.Cols.MODEL, parking.getModel());
        values.put(ParkTable.Cols.COLOR, parking.getColor());
        values.put(ParkTable.Cols.LICENCE, parking.getLicence());
        values.put(ParkTable.Cols.DESCRIPTION, parking.getDescription());
        values.put(ParkTable.Cols.LAT, parking.getLat());
        values.put(ParkTable.Cols.LOGN, parking.getLon());
        values.put(ParkTable.Cols.PTIME, parking.getTime());

        return values;
    }

    public File addPhotoFile() {
        File storage = getStorage();
        if (storage == null) {
            return null;
        }
        return new File(storage,Constants.CAR_PIC_FILE_NAME);
    }

    public File getStorage(){
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }
        File storage = new File(externalFilesDir, Constants.APP_NAME);
        storage.mkdirs();
        return storage;
    }

    public File getPhotoFile(){
        File storage = getStorage();
        if (storage == null) {
            return null;
        }
        String[] photoFileName = storage.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jpg");
            }
        });

        if (photoFileName == null || photoFileName.length == 0) {
            return null;
        }

        return new File(storage, photoFileName[0]);
    }

    public void deletPhoto(){
        File file = getPhotoFile();
        file.delete();
    }

}
