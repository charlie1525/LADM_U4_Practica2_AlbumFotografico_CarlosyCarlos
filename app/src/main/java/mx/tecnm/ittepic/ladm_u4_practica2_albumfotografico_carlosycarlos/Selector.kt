package mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos.databinding.ActivitySelectorBinding
import kotlin.random.Random
import kotlin.system.exitProcess

class Selector : AppCompatActivity() {
    private lateinit var binding : ActivitySelectorBinding
    private val colRef = FirebaseFirestore.getInstance().collection("eventos")
    private val arrEventos = ArrayList<String>()
    var listaID = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val user = FirebaseAuth.getInstance().currentUser!!.email!!
        val key = generateEventId()

        fillList(user)

        binding.btnCreateSl.setOnClickListener {
            createPopUp(key,user)
        }// fin del boton para la creacion de un evento

        binding.btnFotos.setOnClickListener {
            val uploadPhotos = Intent(this, PhotoUpload::class.java)
            uploadPhotos.putExtra("id", key)
            startActivity(uploadPhotos)
        }
        binding.checkEvent.setOnClickListener {
        }

    }

    private fun fillList(user: String){
        colRef.whereEqualTo("correo",user).get().addOnCompleteListener {
            if(it.isSuccessful){
                arrEventos.clear()
                listaID.clear()
                for(docs  in it.result){
                    val evento = "Evento: ${docs.getString("tipo")}\nEstado: ${docs.getString("estado")}"
                    var eventKey = docs.id.toString();
                    listaID.add(eventKey)
                    arrEventos.add(evento)
                }
                showEventList(arrEventos)

            } else{ alerta("Error.. \n${it.exception!!.message}") }
        }// fin del onComplete
    }// fin de método

    private fun showEventList(datos: ArrayList<String>) {
        binding.listaEventosPersonal.adapter = ArrayAdapter<String>(this, androidx.appcompat.R.layout.select_dialog_item_material,datos)
        binding.listaEventosPersonal.setOnItemClickListener { adapterView, view, index, l ->
            actionsDialog(index)
        }
    }

    private fun actionsDialog(index: Int) {
        var idElegido = listaID.get(index)
        AlertDialog.Builder(this).setTitle("ATENCION!").
        setMessage("¿Que deseas hacer con \n ${arrEventos.get(index)}?")
            .setPositiveButton("VER DETALLES"){d,i ->openDetail(idElegido) }
            .setNeutralButton("CANCELAR") {d,i -> }
            .setNegativeButton("EDITAR") {d,i -> openEdit(idElegido)}
            .show()
    }

    private fun openEdit(idElegido: String) {
        val editEvent = Intent(this, EditEventActivity::class.java)
        editEvent.putExtra("event_id", idElegido)
        startActivity(editEvent)
    }
    private fun openDetail(idElegido: String) {
        val eventDetail = Intent(this, EventDetailActivity::class.java)
        eventDetail.putExtra("event_id", idElegido)
        startActivity(eventDetail)
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
        val txtNombre = createPopUp.findViewById<TextInputEditText>(R.id.nombrePopUp)
        val txtFecha = createPopUp.findViewById<TextInputEditText>(R.id.fechaPopUp)
        val txtDescripcion = createPopUp.findViewById<TextInputEditText>(R.id.descripcionPopUp)
        val progDialog = ProgressDialog(this)
        progDialog.setMessage("Creando el evento")
        progDialog.show()

        lbCorreo.text = correo
        lbClave.text = clave
        val remoteFS = FirebaseFirestore.getInstance().collection("eventos")

        with(builder){
            setPositiveButton("Hecho")
            {_,_ ->
                if(txtTipo.text!!.isBlank() || txtTipo.text!!.isEmpty()){
                    alerta("No se pudo crear el evento\nHace falta el tipo de evento")
                    return@setPositiveButton
                } else if(txtNombre.text!!.isBlank() || txtNombre.text!!.isEmpty()) {
                    alerta("No se pudo crear el evento\nHace falta el nombre del evento")
                    return@setPositiveButton
                } else if(txtFecha.text!!.isBlank() || txtFecha.text!!.isEmpty()) {
                    alerta("No se pudo crear el evento\nHace falta la fecha del evento")
                    return@setPositiveButton
                }
                else{
                    val data = hashMapOf(
                        "claveEvent" to lbClave.text.toString(),
                        "correo" to lbCorreo.text.toString(),
                        "descripcion" to txtDescripcion.text.toString(),
                        "estado" to "abierto",
                        "fecha" to txtFecha.text.toString(),
                        "nombre" to txtNombre.text.toString(),
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
                startActivity(Intent(this,Login::class.java))
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