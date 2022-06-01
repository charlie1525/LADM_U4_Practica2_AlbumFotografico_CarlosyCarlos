package mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos.databinding.ActivitySelectorBinding
import kotlin.random.Random
import kotlin.system.exitProcess

class Selector : AppCompatActivity() {
    private lateinit var binding : ActivitySelectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val user = FirebaseAuth.getInstance().currentUser!!.email
        val key = generateEventId()
        binding.btnCreateSl.setOnClickListener {
            createPopUp(key,user!!)
        }// fin del boton para la creacion de un evento

    }

    private fun generateEventId(): String {
        val clave = Hasher().createHash()
        val ceil = Random.nextInt(10, 17)
        val floor = Random.nextInt(18, 30)
        return clave.subSequence(ceil, floor).toString()
    }

    private fun createPopUp(clave:String, correo:String){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val createPopUp =inflater.inflate(R.layout.createpopup,null)
        val lbClave = createPopUp.findViewById<TextView>(R.id.lbClave)
        val lbCorreo = createPopUp.findViewById<TextView>(R.id.lbCorreo)
        val txtTipo = createPopUp.findViewById<TextInputEditText>(R.id.tipoPopUp)
        val progDialog = ProgressDialog(this)
        progDialog.setMessage("Creando el evento")
        progDialog.show()

        lbCorreo.text = correo
        lbClave.text = clave
        val remoteFS = FirebaseFirestore.getInstance().collection("eventos")

        with(builder){
            setPositiveButton("Hecho")
            {_,_ ->
                val data = hashMapOf(
                "claveEvent" to lbClave.text.toString(),
                "correo" to lbCorreo.text.toString(),
                "estado" to "abierto",
                "tipo" to txtTipo.text.toString()
                    )
                remoteFS.add(data).addOnSuccessListener {
                    txtTipo.text!!.clear()
                    progDialog.dismiss()
                    mensaje("Evento creado con exito")
                }.addOnFailureListener{
                    progDialog.dismiss()
                    alerta("No se pudo crear el evento \n${it.message}")
                }
            }
                .setNeutralButton("Cancelar"){_,_ -> progDialog.dismiss()}
                .setView(createPopUp)
                .show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }// match del activity desde el XML

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mnAboutM -> {
                alerta("17401063 CARLOS EDUARDO ROBLES LOPEZ\n" +
                        "17400978 CARLOS URIEL FREGOSO ESPERICUETA\n")
            }
            R.id.mnSessionM -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
            R.id.mnExitM -> {
                exitProcess(0)
            }
        }
        return true
    }

    private fun alerta(cadena:String){
        AlertDialog.Builder(this).setTitle("Atencion!!")
            .setMessage(cadena).show()
    }

    private fun mensaje(cadena:String){
        Toast.makeText(this, cadena, Toast.LENGTH_SHORT).show()
    }
}