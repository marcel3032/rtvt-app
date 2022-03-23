package sk.marcel.rtvt_submit

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File

class JsonsHelpers(private var activity: MainActivity) {
    var resultsFile: File = File(activity.filesDir.absolutePath, "results.json")

    init {
        if(!resultsFile.exists()) {
            resultsFile.createNewFile()
            resetResultsFile()
        }
    }

    fun addSolvedPicture(picture: JSONObject){
        val resultsJson = getResultsJson()
        resultsJson.put(picture)
        resultsFile.writeText(resultsJson.toString())
    }

    fun resetResultsFile(){
        resultsFile.writeText("[]")
    }

    fun getResultsJson(): JSONArray {
        BufferedReader(resultsFile.reader()).use { reader ->
            return JSONArray(reader.readText())
        }
    }

    fun getPicturesJson(i:Int): JSONArray? {
        BufferedReader(activity.assets.open("pictures.json").reader()).use { reader ->
            return JSONArray(reader.readText()).getJSONArray(i)
        }
    }
}