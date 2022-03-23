package sk.marcel.rtvt

import android.view.View
import org.json.JSONArray
import java.io.BufferedReader
import java.io.File
import java.lang.Exception

class JsonsHelpers(private var activity: MainActivity) {
    var progressFile: File = File(activity.filesDir.absolutePath, "progress.json")
    var teamFile: File = File(activity.filesDir.absolutePath, "team.txt")

    init {
        if(!progressFile.exists()) {
            progressFile.createNewFile()
            resetProgressFile()
        }
        if(!teamFile.exists()) {
            teamFile.createNewFile()
            activity.setTeamName(View(activity))
        }
    }

    fun addSolvedColor(i:Int, j:Int){
        val progressJson = getProgressJson()
        progressJson.getJSONArray(progressJson.length()-1).put(JSONArray(listOf(i,j)))
        progressFile.writeText(progressJson.toString())
    }

    fun startNewPicture(){
        try{
            getPicturesJson(getProgressNum()+1)
            val progressJson = getProgressJson()
            progressJson.put(JSONArray())
            progressFile.writeText(progressJson.toString())
        } catch (ignored:Exception){ }
    }

    fun resetProgressFile(){
        progressFile.writeText("[[]]")
    }

    fun getProgressJson(): JSONArray {
        BufferedReader(progressFile.reader()).use { reader ->
            return JSONArray(reader.readText())
        }
    }

    fun getProgressNum() = getProgressJson().length()-1

    fun getColoredPixels(): ArrayList<Pair<Int, Int>> {
        val imageNumber = getProgressJson().length()-1
        val coloredPixels = ArrayList<Pair<Int, Int>>()
        val progressJson = getProgressJson().getJSONArray(imageNumber)
        for (i in 0 until progressJson.length()) {
            coloredPixels.add(Pair(progressJson.getJSONArray(i).getString(0).toInt(), progressJson.getJSONArray(i).getString(1).toInt()))
        }
        return coloredPixels
    }

    fun getPicturesJson(i:Int): JSONArray? {
        BufferedReader(activity.assets.open("pictures.json").reader()).use { reader ->
            return JSONArray(reader.readText()).getJSONArray(i)
        }
    }

    fun setTeamName(name:String){
        teamFile.writeText(name)
    }

    fun getTeamName(): String {
        BufferedReader(teamFile.reader()).use { reader ->
            return reader.readText()
        }
    }
}