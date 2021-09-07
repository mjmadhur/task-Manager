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
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.et_password


class SignIn : Baseactivity(){

  //  private lateinit var googlesignInclient: GoogleSignInClient
   // lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        setSupportActionBar(toolbar_sign_in_activity)
        val actionbar=supportActionBar
        if (actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }
        toolbar_sign_in_activity.setNavigationOnClickListener {
            onBackPressed()
        }
        ee_sign_in.setOnClickListener {
            SigninRegisteredUser()
        }
        /*val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_id)).requestEmail().build()
        googlesignInclient= GoogleSignIn.getClient(this,gso)
        auth = FirebaseAuth.getInstance()
        g_sin.setOnClickListener {
            signIn()
        }*/


    }
    fun signedInSuccess(user:User){
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        this.finish()
    }

   /*override fun onStart() {
        super.onStart()
        val currentuseer=auth.currentUser


    }*/

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode== RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Sign In", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Sign In", "Google sign in failed", e)
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(this,"SIGN IN SUCCESS",Toast.LENGTH_SHORT).show()
                    val Intent=Intent(this,MainActivity::class.java)
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)

                }
            }
    }
    // [END auth_with_google]

    // [START signin]
    private fun signIn() {
        val signInIntent = googlesignInclient.signInIntent
        @Suppress("DEPRECATION")
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }*/



    private fun SigninRegisteredUser(){
       val email: String = et_emailsin.text.toString().trim { it <= ' ' }
       val password: String = et_password.text.toString().trim { it <= ' ' }
       if (validateForm(email,password)){
           showProgressDialog(resources.getString(R.string.please_wait))

           FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
               .addOnCompleteListener(this)
               { task ->
                   hideProgressDialog()

                   if (task.isSuccessful) {

                       // Sign in success, update UI with the signed-in user's information
                       Firestore().LoadInUser(this)


                   } else {
                       // If sign in fails, display a message to the user.

                       Toast.makeText(baseContext, "Authentication failed.",
                           Toast.LENGTH_SHORT).show()

                   }
               }
       }

    }
    private fun validateForm( email: String, password: String): Boolean {
        return when {

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