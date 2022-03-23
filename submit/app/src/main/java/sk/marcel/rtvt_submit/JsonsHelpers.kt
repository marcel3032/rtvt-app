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

    fun isAlreadySolved(picture: JSONObject): Boolean {
        val results = getResultsJson()
        for(i in 0 until results.length()){
            if(results.getJSONObject(i).getString("team")==picture.getString("team")){
                if(results.getJSONObject(i).getString("picture-number")==picture.getString("picture-number")){
                    return true
                }
            }
        }
        return false
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

    fun getPicturesNum(): Int{
        BufferedReader(activity.assets.open("pictures.json").reader()).use { reader ->
            return JSONArray(reader.readText()).length()
        }
    }

    fun getPicturesJson(i:Int): JSONArray? {
        BufferedReader(activity.assets.open("pictures.json").reader()).use { reader ->
            return JSONArray(reader.readText()).getJSONArray(i)
        }
    }
}