package mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.storage.FirebaseStorage
import mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos.databinding.ActivityPhotoUploadBinding

class PhotoUpload : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoUploadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }


    private fun cargarLista(){
        val storageRef = FirebaseStorage.getInstance().reference.child("evento")
    }

    private fun mensaje(cadena: String){
        Toast.makeText(this, cadena, Toast.LENGTH_SHORT).show()
    }
    private fun alerta(cadena:String){
        AlertDialog.Builder(this).setTitle("Atencion!!")
            .setMessage(cadena).show()
    }

}