package com.example.trelloc

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.trelloc.firebase.Firestore
import com.example.trelloc.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp :Baseactivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
setupab()
        btn_sign_up.setOnClickListener {
            registerUser()
        }




        }
    fun useregisteredSuccess(){
        Toast.makeText(this,"yph have Successfully Registered",Toast.LENGTH_LONG).show()
    hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()


    }
    private fun setupab(){
        setSupportActionBar(toolbar_sign_up_activity)
        val actionbar=supportActionBar
        if (actionbar!=null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }
        toolbar_sign_up_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun registerUser(){
        val name: String = et_name.text.toString().trim { it <= ' ' }
        val email: String = et_email.text.toString().trim { it <= ' ' }
        val password: String = et_password.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {

            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                {
                    task->hideProgressDialog()
                    if (task.isSuccessful){
                        val use:FirebaseUser=task!!.result!!.user!!
                        val regEmail=use.email!!
                       val user=User(use.uid,regEmail,name)
                        Firestore().registerUser(this,user)

                    }else{
                        Toast.makeText(this,"Request Failed",Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
    // END

    // TODO (Step 10: A function to validate the entries of a new user.)
    // START
    /**
     * A function to validate the entries of a new user.
     */
    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }

}
