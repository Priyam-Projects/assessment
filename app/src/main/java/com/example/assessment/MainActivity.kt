package com.example.assessment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.assessment.Constants.Companion.incorrectCreds
import com.example.assessment.Constants.Companion.invalidEmail
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginReqeust()
    }

    fun moveToMessageActivity(){
        println("Ayush intent create")
        val intent = Intent(this@MainActivity,MessageActivity::class.java)
        startActivity(intent)
    }

    fun verifyEmail(email: Editable):Boolean{
        return !TextUtils.isEmpty(email) // check if it is not empty
                    && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() // matches regex
    }

    fun displayError(errorMessage:String){
        Toast.makeText(this,errorMessage,Toast.LENGTH_SHORT).show()
    }

    fun saveUserInfo(authToken:String){

        println("Ayush in saveUserInfo")
        val sharedPreferences = getSharedPreferences("DefaultSharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()

        myEdit.putString("auth_token", authToken)
        myEdit.commit()

        println("Ayush in calling funciton")
        moveToMessageActivity()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun makeLoginRequest(userEmail: Editable, userPassword: Editable){

        Thread(
            Runnable {

                val payload = "username=$userEmail&password=$userPassword"

                val okHttpClient = OkHttpClient()
                val requestBody = payload.toRequestBody()
                val request = Request.Builder()
                    .method("POST", requestBody)
                    .url("https://android-messaging.branch.co/api/login")
                    .build()
                okHttpClient.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        // Handle this
                    }

                    override fun onResponse(call: Call, response: okhttp3.Response) {
                        // Handle this
                        if(response.code!=200){
                            runOnUiThread {
                                displayError(incorrectCreds)
                            }
                        }
                        else {

                            val jsonData: String = response.body?.string() ?: ""
                            val jsonObject = JSONObject(jsonData)

                            println(jsonObject.toString())
                            runOnUiThread {
                                saveUserInfo(jsonObject.getString("auth_token"))
                            }
                        }
                    }
                })

            } ).start()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun loginReqeust(){

        val sharedPreferences = getSharedPreferences("DefaultSharedPref", MODE_PRIVATE)
        val auth  = sharedPreferences.getString("auth_token","")

        println("ayush69 "+auth)

        if(!auth.isNullOrBlank()){
            moveToMessageActivity()
            return
        }

        findViewById<Button>(R.id.login_button).setOnClickListener {

            var userEmail = findViewById<EditText>(R.id.inputUsername).text
            var userPassword = findViewById<EditText>(R.id.inputPassword).text

            if(!verifyEmail(userEmail)){
                displayError(invalidEmail)
            }
            else{
                makeLoginRequest(userEmail,userPassword)
            }
        }

    }




}


