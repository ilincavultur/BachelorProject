package com.example.digitaldiaryba.util

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log

fun chooseQueryString(building: String) : String {
    var buildingName = building
    if (buildingName.contains(" ")) {
        buildingName = building.replace(" ", "_")
    }
    Log.d(TAG, "chooseQueryString: " + buildingName)
    return "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
            "PREFIX dbp: <http://dbpedia.org/property/>\n" +
            "\n" +
            "SELECT ?name ?location ?country ?year ?architect ?description WHERE {\n" +
            "    { <http://dbpedia.org/resource/${buildingName}> dbo:originalName ?name }\n" +
            "    UNION\n" +
            "    { <http://dbpedia.org/resource/${buildingName}> dbp:name ?name }\n" +
            "    UNION\n" +
            "    { <http://dbpedia.org/resource/${buildingName}> dbo:location ?location }\n" +
            "    UNION\n" +
            "    { <http://dbpedia.org/resource/${buildingName}> dbp:location ?location }\n" +
            "    UNION\n" +
            "    { <http://dbpedia.org/resource/${buildingName}> dbo:country ?country }\n" +
            "    UNION\n" +
            "    { <http://dbpedia.org/resource/${buildingName}> dbp:country ?country }\n" +
            "    UNION\n" +
            "    { <http://dbpedia.org/resource/${buildingName}> dbo:openingDate ?year }\n" +
            "    UNION\n" +
            "    { <http://dbpedia.org/resource/${buildingName}> dbp:built ?year }\n" +
            "    UNION\n" +
            "    { <http://dbpedia.org/resource/${buildingName}> dbo:architect ?architect }\n" +
            "    UNION\n" +
            "    { <http://dbpedia.org/resource/${buildingName}> dbp:builder ?architect }\n" +
            "    UNION\n" +
            "    { <http://dbpedia.org/resource/${buildingName}> dbo:abstract ?description " +
            " FILTER (langMatches(lang(?description), \"en\")) }    \n" +
            "}\n" +
            "\n"
}