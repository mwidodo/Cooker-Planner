package project.cookerpro

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException


class SignUpActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var signUpBtn: Button
    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var confirmPass: EditText
    private lateinit var AlreadySignedUp:TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        firebaseAuth = FirebaseAuth.getInstance()
        signUpBtn = findViewById(R.id.SignUp)

        AlreadySignedUp = findViewById(R.id.AlreadySignedUp)

        AlreadySignedUp.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        signUpBtn.setOnClickListener {
            email = findViewById(R.id.signUpEmail)
            val email = email.text.toString()

            pass = findViewById(R.id.signUpPass)
            val pass = pass.text.toString()
            confirmPass = findViewById(R.id.signUpPass)
            val confirmPass = confirmPass.text.toString()
//            pass = binding.passET.text.toString()
//            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            try {
                                if (task.isSuccessful) {
                                    val intent = Intent(this, SignInActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    // If user creation fails, throw a custom exception
                                    throw FirebaseAuthException("User creation failed", task.exception)
                                }
                            } catch (e: FirebaseAuthException) {
                                // Handle your custom exception here
                                Log.e("FirebaseAuthException", e.toString())

                                // You can check the specific type of Firebase exception
                                if (e.cause is FirebaseAuthWeakPasswordException) {
                                    // Handle weak password exception
                                    Toast.makeText(this, "Weak password", Toast.LENGTH_SHORT).show()
                                } else if (e.cause is FirebaseAuthInvalidCredentialsException) {
                                    // Handle invalid email exception
                                    Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
                                } else if (e.cause is FirebaseAuthUserCollisionException) {
                                    // Handle user already exists exception
                                    Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Handle other Firebase authentication exceptions
                                    Toast.makeText(this, "User creation failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                // Handle other exceptions if needed
                                Log.e("Exception", e.toString())
                                Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}