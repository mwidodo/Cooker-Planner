package project.cookerpro

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class FirebaseAuthException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

class SignInActivity : AppCompatActivity() {
    private lateinit var email:TextView
    private lateinit var password:TextView
    private lateinit var register:TextView
    private lateinit var signIn: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        register = findViewById(R.id.RegisterText)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        firebaseAuth = FirebaseAuth.getInstance()
        signIn = findViewById(R.id.SignInButton)


        signIn.setOnClickListener {
            Log.d("Check if works","works!!")

            val email = email.text.toString()
            val pass = password.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                Log.d("Email==", email.toString())
                firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        try {
                            if (task.isSuccessful) {
                                Log.d("Successfull True", task.isSuccessful.toString())
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                // If sign-in fails, throw a custom exception
                                throw FirebaseAuthException("Authentication failed", task.exception)
                            }
                        } catch (e: FirebaseAuthException) {
                            // Handle your custom exception here
                            Log.e("FirebaseAuthException", e.toString())

                            // You can check the specific type of Firebase exception
                            if (e.cause is FirebaseAuthInvalidCredentialsException) {
                                // Handle invalid credentials exception
                                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                            } else {
                                // Handle other Firebase authentication exceptions
                                Toast.makeText(this, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            // Handle other exceptions if needed
                            Log.e("Exception", e.toString())
                            Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }

        }

        register.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
    @SuppressLint("LongLogTag")
    override fun onStart() {
        super.onStart()
        Log.d("firebaseAuth.currentUser",firebaseAuth.currentUser.toString())
        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}