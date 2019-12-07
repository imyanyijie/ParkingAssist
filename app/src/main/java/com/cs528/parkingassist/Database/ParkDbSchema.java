package com.cs528.parkingassist.Database;

public class ParkDbSchema {
    public static final class ParkTable {
        public static final String NAME = "parking";

        public static final class Cols {
            public static final String PID = "pid";
            public static final String MAKER = "maker";
            public static final String MODEL = "model";
            public static final String COLOR = "color";
            public static final String LICENCE = "licence";
            public static final String LAT = "lat";
            public static final String LOGN = "lon";
            public static final String DESCRIPTION = "description";
            public static final String PTIME = "ptime";
        }
    }
}
