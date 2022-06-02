package mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos

import android.R
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos.databinding.ActivityEventDetailBinding
import java.io.File

class EventDetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityEventDetailBinding
    private var eventId: String? = ""
    private val refEvents = FirebaseFirestore.getInstance().collection("eventos")
    private var nombreEvento= ""
    private var fechaEvento= ""
    private var descripcionEvento= ""
    private var estadoEvento= ""
    private var keyEvento= ""
    private val listaArchivos = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Detalles del evento"
        eventId = this.intent.extras!!.getString("event_id")
        getEventData(eventId!!)
        binding.goToBack.setOnClickListener { finish() }
        binding.btnFotos.setOnClickListener {
            val uploadPhotos = Intent(this, PhotoUpload::class.java)
            uploadPhotos.putExtra("id", eventId)
            uploadPhotos.putExtra("eventKey", keyEvento)
            startActivity(uploadPhotos)
        }
    }
    private fun getEventData(eventId: String) {
        refEvents.document(eventId).get().addOnSuccessListener {
            if (it.exists()) {

                nombreEvento = it.getString("nombre").toString()
                fechaEvento = it.getString("fecha").toString()
                descripcionEvento = it.getString("fecha").toString()
                estadoEvento = it.getString("estado").toString()
                keyEvento = it.getString("claveEvent").toString()

                getEventPhotos(it.getString("claveEvent").toString())

                binding.textView2.setText(nombreEvento)
                binding.eventDate.setText(fechaEvento)
                binding.eventDescription.setText(nombreEvento)

                if(estadoEvento != "Abierto"){
                    binding.btnFotos.visibility = View.GONE
                }else{
                    binding.btnFotos.visibility = View.VISIBLE
                }


            } else {
                alerta("No existe ese documento")
            }
        }.addOnFailureListener {
            alerta("Atencion!!\n${it.message}")
        }
    }// fin del metodo del update
    private fun getEventPhotos(key: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("eventos/$key")
        storageRef.listAll().addOnSuccessListener { it ->
            listaArchivos.clear()
            it.items.forEach { listaArchivos.add(it.name) }
            binding.lvLista.adapter =
                ArrayAdapter(this, R.layout.simple_list_item_checked, listaArchivos)
            binding.lvLista.setOnItemClickListener { _, _, i, _ ->
                cargarImagen(listaArchivos[i],key)
            }
        }// fin del on success listener
    }
    private fun cargarImagen(s: String, key: String) {
        val stRef = FirebaseStorage.getInstance().reference.child("eventos/$key/$s")
        val tempFile = File.createTempFile("tempImage", "jpg")
        stRef.getFile(tempFile).addOnSuccessListener {
            val mapabits = BitmapFactory.decodeFile(tempFile.absolutePath)
            binding.IVimagen.setImageBitmap(mapabits)
        }
    }
    private fun alerta(cadena: String) {
        AlertDialog.Builder(this).setTitle("Atencion!!")
            .setMessage(cadena).show()
    }
}