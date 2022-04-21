package sk.marcel.rtvt_editor

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class JsonsHelpers(private var activity: MainActivity) {
    private var shopFile: File = File(activity.filesDir.absolutePath, "shop.json")
    private var peopleFile: File = File(activity.filesDir.absolutePath, "people.json")

    init {
        if(!shopFile.exists()) {
            shopFile.createNewFile()
            shopFile.writeText("[]")
        }
        if(!peopleFile.exists()) {
            peopleFile.createNewFile()
            peopleFile.writeText("[]")
        }
    }

    fun writePeople(peopleData: String){
        peopleFile.writeText(peopleData)
    }

    fun writeShop(shopData: String){
        shopFile.writeText(shopData)
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

    fun getPeopleJson(): JSONArray {
        BufferedReader(peopleFile.reader()).use { reader ->
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
        BufferedReader(shopFile.reader()).use { reader ->
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