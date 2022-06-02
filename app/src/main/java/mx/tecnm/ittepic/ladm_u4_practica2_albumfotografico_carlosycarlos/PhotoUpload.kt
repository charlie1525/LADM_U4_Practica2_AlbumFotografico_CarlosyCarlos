package mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos.databinding.ActivityPhotoUploadBinding
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class PhotoUpload : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoUploadBinding
    private val listaArchivos = ArrayList<String>()
    private lateinit var imagen: Uri
    private var gallerySuccess = 2
    private var intentKey = ""
    private var eventId: String? = ""
    var refEventos = FirebaseFirestore.getInstance().collection("eventos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Subida de Fotos"

        intentKey =  this.intent.extras!!.getString("eventKey")!!

        // obtener el estado del evento

        getEventStatus(object :
            GetStatus { override fun onCallback(status: String) { binding.lbStatusEventUP.text = status  } }
            ,intentKey)

        // fin de la obtencio del estado del evento

        binding.lbKeyEventUP.text = intentKey



        binding.btnSeleccionar.setOnClickListener {
            val galeria = Intent(Intent.ACTION_GET_CONTENT)
            galeria.type = "image/*"
            startActivityForResult(galeria, gallerySuccess)
        }// fin de la selección de imagenes.

        binding.btnSubir.setOnClickListener {
            var nombreImagen = ""
            val cal = GregorianCalendar.getInstance()
            nombreImagen = cal.get(Calendar.YEAR).toString() + "-" +
                    cal.get(Calendar.MONTH).toString() + "-" +
                    cal.get(Calendar.DAY_OF_MONTH).toString() + ", " +
                    cal.get(Calendar.HOUR_OF_DAY).toString() + ":" +
                    cal.get(Calendar.MINUTE).toString() + ":" +
                    cal.get(Calendar.MILLISECOND).toString() + ".jpg"

            val stRef = FirebaseStorage.getInstance()
                .reference.child("eventos/$intentKey/$nombreImagen")

            val progressDi = ProgressDialog(this)
            progressDi.setMessage("Subiendo foto")
            progressDi.setCancelable(false)
            progressDi.show()

            stRef.putFile(imagen).addOnSuccessListener {
                progressDi.dismiss()
                mensaje("Imagen subida correctamente")
                binding.IVimagen.setImageBitmap(null)
            }// fin del success
                .addOnFailureListener {
                    progressDi.dismiss()
                    alerta("Error en la subida \n${it.message}")
                }
        }// fin de la subida de una foto

        binding.fabUP.setOnClickListener { finish() }

    }// fin del on create

    private fun getEventStatus(callBack: GetStatus,keyEvent: String){
        refEventos.whereEqualTo("claveEvent",keyEvent).get().addOnCompleteListener {
            if(it.isSuccessful) {
                var state = ""
                for (doc in it.result) {
                    state = doc.getString("estado")!!
                }// fin del for para ir por los resultados
                callBack.onCallback(state)
            }// fin del if si es que la consulta se completo
            else{
                alerta("Error... \n${it.exception!!.message}")
            }
        }// fin del onCompleteListener
    }// fin del método callBack

    private interface GetStatus{ fun onCallback(status: String) }

    //private fun getEventStatus(key: String) {
       // refEventos.whereEqualTo("claveEvent", key).get().addOnSuccessListener {
          //  for (doc in it!!) {
            //    intentStatus = doc.getString("estado")!!
           // }
       // }
   // }



    private fun cargarImagen(s: String, key: String) {
        val stRef = FirebaseStorage.getInstance().reference.child("eventos/$key/$s")
        val tempFile = File.createTempFile("tempImage", "jpg")
        stRef.getFile(tempFile).addOnSuccessListener {
            val mapabits = BitmapFactory.decodeFile(tempFile.absolutePath)
            binding.IVimagen.setImageBitmap(mapabits)
        }
    }

    private fun mensaje(cadena: String) {
        Toast.makeText(this, cadena, Toast.LENGTH_SHORT).show()
    }

    private fun alerta(cadena: String) {
        AlertDialog.Builder(this).setTitle("Atencion!!")
            .setMessage(cadena).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != RESULT_CANCELED){
            if (requestCode == gallerySuccess) {
                imagen = data!!.data!!
                binding.IVimagen.setImageURI(imagen)
            }
        }
    }
}