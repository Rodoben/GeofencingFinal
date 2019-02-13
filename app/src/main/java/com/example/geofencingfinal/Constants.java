/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.geofencingfinal;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by lmoroney on 12/17/14.
 */
public final class Constants {

    private Constants() {
    }

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    //public static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km
    public static final float GEOFENCE_RADIUS_IN_METERS = 20; // 1 mile, 1.6 km

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */

    public static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<String, LatLng>();
    static {
        // San Francisco International Airport.
        BAY_AREA_LANDMARKS.put("audienter", new LatLng(12.936133, 77.606215));

        // Googleplex.
        BAY_AREA_LANDMARKS.put("puc", new LatLng(12.935561, 77.606208));

        // Test
        BAY_AREA_LANDMARKS.put(" parking", new LatLng(12.934890, 77.606192));

        BAY_AREA_LANDMARKS.put("centralblock", new LatLng(12.934529, 77.606197));

        BAY_AREA_LANDMARKS.put("block1", new LatLng(12.933932, 77.606426));
        BAY_AREA_LANDMARKS.put("block2", new LatLng(12.933357, 77.606238));
        BAY_AREA_LANDMARKS.put("block3", new LatLng(12.931893, 77.606396));
        BAY_AREA_LANDMARKS.put("block4", new LatLng(12.932131, 77.606000));
        BAY_AREA_LANDMARKS.put("block2nd", new LatLng(12.932797, 77.606291));

        BAY_AREA_LANDMARKS.put("park",new LatLng(12.934961, 77.606192));
    }



}
