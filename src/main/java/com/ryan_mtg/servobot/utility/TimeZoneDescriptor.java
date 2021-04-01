package com.ryan_mtg.servobot.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter @AllArgsConstructor
public class TimeZoneDescriptor {
    public static List<TimeZoneDescriptor> TIME_ZONES = new ArrayList<>();

    static  {
        TIME_ZONES.add(new TimeZoneDescriptor("America/New_York", "Eastern", "ET"));
        TIME_ZONES.add(new TimeZoneDescriptor("America/Chicago", "Central", "CT"));
        TIME_ZONES.add(new TimeZoneDescriptor("America/Denver", "Mountain", "MT"));
        TIME_ZONES.add(new TimeZoneDescriptor("America/Vancouver", "Pacific", "PT"));
        TIME_ZONES.add(new TimeZoneDescriptor("Europe/London", "Western European", "WET"));
        TIME_ZONES.add(new TimeZoneDescriptor("Europe/Paris", "Central European", "CET"));
        TIME_ZONES.add(new TimeZoneDescriptor("Asia/Tokyo", "Japan", "JST"));
    }

    private final String value;
    private final String name;
    private final String abbreviation;
}

