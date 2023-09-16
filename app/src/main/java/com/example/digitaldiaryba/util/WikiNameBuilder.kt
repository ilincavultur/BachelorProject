package com.example.digitaldiaryba.util

import android.content.ContentValues
import android.util.Log


fun chooseWikiName(building: String) : String {
    var buildingName = building
    if (buildingName.contains(" ")) {
        buildingName = building.replace(" ", "_")
    }
    Log.d(ContentValues.TAG, "chooseQueryString: " + buildingName)
    return buildingName
}