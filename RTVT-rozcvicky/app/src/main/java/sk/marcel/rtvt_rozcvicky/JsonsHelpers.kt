package sk.marcel.rtvt_rozcvicky

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class JsonsHelpers(private var activity: MainActivity) {
    var resultsFile: File = File(activity.filesDir.absolutePath, "attendance.json")

    init {
        if(!resultsFile.exists()) {
            resultsFile.createNewFile()
            resetResultsFile()
        }
    }

    fun writeResults(person: JSONObject, money: Long?){
        val resultsJson = getResultsJson()
        val pattern = "yyyy.MM.dd HH:mm:ss"
        val simpleDateFormat = SimpleDateFormat(pattern)

        person.put("time", simpleDateFormat.format(Calendar.getInstance().time))
        person.put("money", money)
        resultsJson.getJSONArray(getResultsJson().length()-1).put(person)
        resultsFile.writeText(resultsJson.toString())
    }

    fun addNewActivity(){
        val resultsJson = getResultsJson()
        resultsJson.put(JSONArray())
        resultsFile.writeText(resultsJson.toString())
    }

    fun getLastResultsJson(): JSONArray {
        BufferedReader(resultsFile.reader()).use { reader ->
            val res = JSONArray(reader.readText())
            return res.getJSONArray(res.length()-1)
        }
    }

    fun getResultsJson(): JSONArray {
        BufferedReader(resultsFile.reader()).use { reader ->
            return JSONArray(reader.readText())
        }
    }

    fun isCheckedIn(id:String):JSONObject?{
        val results = getLastResultsJson()
        for(i in 0 until results.length()){
            if(id == results.getJSONObject(i).getString("id")){
                return results.getJSONObject(i)
            }
        }
        return null
    }

    fun isCheckedOut(id:String):Boolean{
        var result = false
        val results = getLastResultsJson()
        for(i in 0 until results.length()){
            if(id == results.getJSONObject(i).getString("id")){
                if(result)
                    return true
                else
                    result = true
            }
        }
        return false
    }

    fun getPersonById(id:String):JSONObject?{
        val results = getPeopleJson()
        for(i in 0 until results.length()){
            if(id == results.getJSONObject(i).getString("id")){
                return results.getJSONObject(i)
            }
        }
        return null
    }

    fun getCountPeopleInSameGroup(group:String):Int{
        var result = 0
        val results = getPeopleJson()
        for(i in 0 until results.length()){
            if(group == results.getJSONObject(i).getString("group")){
                result++
            }
        }
        return result
    }

    fun resetResultsFile(){
        resultsFile.writeText("[[]]")
    }

    fun addPerson(id: String, money: Long?){
        getPersonById(id)?.let { writeResults(it, money) }
    }

    fun getPeopleJson(): JSONArray {
        BufferedReader(activity.assets.open("people.json").reader()).use { reader ->
            return JSONArray(reader.readText())
        }
    }

    fun getPeopleList():List<Person>{
        val people = getPeopleJson()
        val peopleList = ArrayList<Person>()
        for(i in 0 until people.length()){
            peopleList.add(Person(people.getJSONObject(i).getString("id"), people.getJSONObject(i).getString("name"), people.getJSONObject(i).getString("group")))
        }
        return peopleList
    }
}