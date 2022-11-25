package com.example.assessment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.assessment.Constants.Companion.messageError
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MessageActivity: AppCompatActivity() {

    var messages: HashMap<String,Array<Array<String>>> = hashMapOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_page)

//        Toast.makeText(this,"Heyy",Toast.LENGTH_SHORT).show()
        println("Ayush 1")
        initfetchMessages()
    }

    fun moveToLoginActivity(){

        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
    fun displayError(errorMessage:String){
        Toast.makeText(this,errorMessage, Toast.LENGTH_SHORT).show()
    }

    fun fetchMessages(authToken:String){

        println("Ayush 2")
        Thread(
            Runnable {

                val okHttpClient = OkHttpClient()
                val request = Request.Builder()
                    .method("GET",null)
                    .url("https://android-messaging.branch.co/api/messages")
                    .header("X-Branch-Auth-Token",authToken)
                    .build()
                println("Ayush 3")
                okHttpClient.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        // Handle this
                    }

                    override fun onResponse(call: Call, response: okhttp3.Response) {
                        // Handle this
                        if(response.code!=200){
                            runOnUiThread {
                                displayError(messageError)
//                                moveToLoginActivity()
                                println("Ayush response code!=200")
                            }
                        }
                        else {

                            var jsonData: String = response.body?.string() ?: ""
//                            jsonData = "{\"data\": $jsonData}"

                            val jsonArray = JSONArray(jsonData)
                            saveData(jsonArray)
                        }
                    }
                })

            } ).start()



    }

    fun saveData(jsonArray: JSONArray){

        for(i in 0 until jsonArray.length()){

            val jsonObject = jsonArray.getJSONObject(i)
            val id = jsonObject.getInt("id").toString()
            val thread_id = jsonObject.getInt("thread_id").toString()
            val user_id = jsonObject.getString("user_id")
            val body = jsonObject.getString("body")
            val timestamp = jsonObject.getString("timestamp")
            val agent_id = jsonObject.getString("agent_id")

            val message = arrayOf(timestamp,id,user_id,body,agent_id)

            messages.getOrDefault(thread_id,{})

//            println("test" + id + " " + thread_id + " "+ user_id + " "+ user_id + " "+ body + " "+ timestamp+ " "+agent_id);

        }


    }


    fun initfetchMessages(){

        val sharedPreferences = getSharedPreferences("DefaultSharedPref", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token","")

        println("Ayush $authToken")

        if(authToken.isNullOrEmpty()){
            moveToLoginActivity()
            return
        }

        println("Ayush fetching messages")
        fetchMessages(authToken)
    }

}