package com.example.chitchatkt

import android.app.Dialog
import android.content.Intent
import android.os.Bundle

import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.chitchatkt.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.view.*

class LoginActivity : AppCompatActivity() {
    lateinit var view: ActivityLoginBinding
    var context = this@LoginActivity
    private lateinit var auth: FirebaseAuth
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var login: Button
    lateinit var newuser: TextView
    var toggle: Boolean = false
    lateinit var dialog: Dialog
    private val buttonClick = AlphaAnimation(1f, 0.8f)
//    lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = DataBindingUtil.setContentView(this,R.layout.activity_login)
            auth = FirebaseAuth.getInstance()
//        supportActionBar(toolbar)
//        supportActionBar!!.setTitle("Register")
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        findbyid()
        setupdialog()
//        setSupportActionBar(view.toolbarinc.toolbar)
//        val actionbar = supportActionBar
//        actionbar!!.title = "Login"
//        actionbar.setDisplayHomeAsUpEnabled(true)
        setonclicklistners()

    }

    private fun setupdialog() {
        dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_progress_dialog)
        val text = dialog.findViewById<TextView>(R.id.progresstext)
        text.text = getString(R.string.authenticating)
        val window = dialog.window
        window!!.setGravity(Gravity.CENTER)
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun setonclicklistners() {

        login.setOnClickListener { it ->
            val textEmail: String = email.text.toString().trim()
            val textPassword: String = password.text.toString().trim()
            if (TextUtils.isEmpty(textEmail) or TextUtils.isEmpty(textPassword)){

                Snackbar.make(it,"Fill All Credentials", Snackbar.LENGTH_LONG).show()

            }

            else{
//                    view.ll1.progressdialog.visibility = View.VISIBLE
                dialog.show()
                auth.signInWithEmailAndPassword(textEmail,textPassword).addOnCompleteListener {

                    if (!it.isSuccessful)return@addOnCompleteListener
                        intents(Intent(context,MessagingActivity::class.java ))
//                        val intent: Intent = Intent(context,MessagingActivity::class.java )
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                        startActivity(intent)
                    Log.e("LoginResponse","login Successfull")
//                    view.ll1.progressdialog.visibility = View.GONE
                    dialog.dismiss()
                        finish()

                }
                    .addOnFailureListener {
                        Log.e("LoginResponse","login failed")
                        dialog.dismiss()
                        Toast.makeText(this,"You can't login with this email or password",Toast.LENGTH_SHORT).show()
                    }
            }
        }

        newuser.setOnClickListener {
            it.startAnimation(buttonClick)
            intents(Intent(context,RegisterationActivity::class.java) )
//            val intent: Intent = Intent(context,RegisterationActivity::class.java)
//         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//           startActivity(intent)
            finish()
        }

        view.ll1.ll2.passwordtoggle.setOnClickListener {
            it.startAnimation(buttonClick)

            if(!toggle)
            {
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                toggle = true
            }

            else
            {
                password.transformationMethod = PasswordTransformationMethod.getInstance()
                toggle = false
            }
        }
    }

    private fun intents(intent: Intent){

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }

    private fun findbyid() {
        newuser = findViewById(R.id.newuser)
        email  = findViewById(R.id.email)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
/* toolbar = findViewById(R.id.toolbar) as Toolbar */
    }
}

//private operator fun ActionBar?.invoke(toolbar: Toolbar) {
//
//}