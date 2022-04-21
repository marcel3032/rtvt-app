package sk.marcel.rtvtAttendance

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Downloaders {
    class PeopleDownloadTask : AsyncTask<String?, Int, String?>() {
        override fun doInBackground(vararg params: String?): String? {
            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null
            try {
                val url = URL(params[0])
                connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val stream: InputStream = connection.inputStream
                reader = BufferedReader(InputStreamReader(stream))
                val buffer = StringBuffer()
                var line = reader.readLine()
                while (line != null) {
                    buffer.append(""" $line """.trimIndent())
                    Log.e("Response: ", "> $line")
                    line = reader.readLine()
                }
                MainActivity.jsonsHelpers.writePeople(buffer.toString())
                return buffer.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
                try {
                    reader?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return null
        }
    }
}