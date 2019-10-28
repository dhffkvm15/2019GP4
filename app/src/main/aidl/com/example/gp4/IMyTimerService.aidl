// IMyTimerService.aidl
package com.example.gp4;

// Declare any non-default types here with import statements

interface IMyTimerService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

     int getTime();

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
