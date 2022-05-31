package mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val autenticacion = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null) invocaSeleccion()// fin de la checada si hay un usuario ya logeado

        binding.inscribir.setOnClickListener {
            autenticacion.createUserWithEmailAndPassword(binding.correo.text.toString(), binding.txtPass.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        binding.correo.setText("")
                        binding.txtPass.setText("")
                        mensaje("Usuario Creado")
                        autenticacion.signOut()
                    }else{ alerta("Error de creacion de usuario") }
                }
        }

        binding.autenticar.setOnClickListener {
            val dialogo = ProgressDialog(this)
            dialogo.setMessage("Se esta autenticando el correo")
            dialogo.setCancelable(false)
            dialogo.show()

            autenticacion.signInWithEmailAndPassword(
                binding.correo.text.toString(),
                binding.txtPass.text.toString()
            ).addOnCompleteListener {
                dialogo.dismiss()
                if(it.isSuccessful){
                    invocaSeleccion()
                    return@addOnCompleteListener
                }else{
                   alerta("Correo o contraseña erroneos")
                }
            }
        }

        binding.recuperar.setOnClickListener {
            val dialogo = ProgressDialog(this)
            dialogo.setCancelable(false)
            dialogo.setMessage("Mandando correo de recuperación")
            dialogo.show()

            autenticacion.sendPasswordResetEmail(
                binding.correo.text.toString()
            ).addOnSuccessListener {
                dialogo.dismiss()
                alerta("Correo enviado, favor de revisar su bandeja de entrada")
            }
        }

    }// fin del on create

    private fun invocaSeleccion() {
        val selectionWindow = Intent(this,Selector::class.java)
        startActivity(selectionWindow)
        finish()
    }

    private fun alerta(cadena:String){
        AlertDialog.Builder(this).setTitle("Atencion!!")
            .setMessage(cadena).show()
    }

    private fun mensaje(cadena:String){
        Toast.makeText(this, cadena, Toast.LENGTH_SHORT).show()
    }
}// fin del main activity