package sk.marcel.rtvt_editor

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class JsonsHelpers(private var activity: MainActivity) {
    var resultsFile: File = File(activity.filesDir.absolutePath, "attendance.json")

    init {
        if(!resultsFile.exists()) {
            resultsFile.createNewFile()
            resetResultsFile()
        }
    }

    fun writeResults(person: JSONObject){
        val resultsJson = getResultsJson()
        person.put("time", Calendar.getInstance().time.toGMTString())
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

    fun isNotIdInResults(id:String):Boolean{
        val results = getLastResultsJson()
        for(i in 0 until results.length()){
            if(id == results.getJSONObject(i).getString("id")){
                return false
            }
        }
        return true
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

    fun getPersonByIdObject(id:String):Person?{
        val results = getPeopleJson()
        for(i in 0 until results.length()){
            if(id == results.getJSONObject(i).getString("id")){
                return Person(results.getJSONObject(i).getString("id"), results.getJSONObject(i).getString("name"), results.getJSONObject(i).getString("group"))
            }
        }
        return null
    }

    fun resetResultsFile(){
        resultsFile.writeText("[[]]")
    }

    fun addPerson(id: String){
        getPersonById(id)?.let { writeResults(it) }
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

    fun getItemsJson(): JSONArray {
        BufferedReader(activity.assets.open("shop.json").reader()).use { reader ->
            return JSONArray(reader.readText())
        }
    }

    fun getItemsList():List<Item>{
        val items = getItemsJson()
        val itemsList = ArrayList<Item>()
        for(i in 0 until items.length()){
            itemsList.add(Item(i, items.getJSONObject(i).getString("name"), items.getJSONObject(i).getLong("price")))
        }
        return itemsList
    }
}