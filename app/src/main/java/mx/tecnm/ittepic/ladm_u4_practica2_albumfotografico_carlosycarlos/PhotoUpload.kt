package mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ProgressBar
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
    private var gallerySuccess = 1
    private var intentKey = ""
    private var status = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Subida de Fotos"

        intentKey = "asiudhaih"// this.intent.extras!!.getString("id")!!
        cargarLista(intentKey)


        val query = FirebaseFirestore.getInstance()
            .collection("eventos")
        query.whereEqualTo("claveEvent", intentKey).get().addOnSuccessListener {
            for (doc in it) {
                status = doc!!.id
            }
            alerta(status)
        }


        binding.lbKeyEventUP.text = intentKey
        binding.lbTypeEventUP.text = status


        binding.btnSeleccionar.setOnClickListener {
            val galeria = Intent(Intent.ACTION_GET_CONTENT)
            galeria.type = "image/*"
            startActivityForResult(galeria, gallerySuccess)
        }// fin de la selección de imagenes.

        binding.btnSubir.setOnClickListener {
            var nombreImagen = ""
            val cal = GregorianCalendar.getInstance()
            nombreImagen = cal.get(Calendar.YEAR).toString() + "/" +
                    cal.get(Calendar.MONTH).toString() + "/" +
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
                cargarLista(intentKey)
            }// fin del success
                .addOnFailureListener {
                    progressDi.dismiss()
                    alerta("Error en la subida \n${it.message}")
                }
        }// fin de la subida de una foto

        binding.fabUP.setOnClickListener { finish() }


    }


    private fun cargarLista(key: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("eventos/$key")
        storageRef.listAll().addOnSuccessListener { it ->
            listaArchivos.clear()
            it.items.forEach { listaArchivos.add(it.name) }
            binding.lvLista.adapter =
                ArrayAdapter(this, android.R.layout.simple_selectable_list_item, listaArchivos)
            binding.lvLista.setOnItemClickListener { _, _, i, _ ->
                cargarImagen(
                    listaArchivos[i],
                    key
                )
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
        if (requestCode == 2) {
            imagen = data!!.data!!
            binding.IVimagen.setImageURI(imagen)
        }

    }
}