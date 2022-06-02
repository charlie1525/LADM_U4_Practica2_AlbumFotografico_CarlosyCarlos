package mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos.databinding.ActivityEditEventBinding

class EditEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditEventBinding
    private var eventId: String? = ""
    private val refEvents = FirebaseFirestore.getInstance().collection("eventos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Editar evento"
        eventId = this.intent.extras!!.getString("event_id")
        getEventData(eventId!!)

        binding.regresar.setOnClickListener { finish() }
        binding.updateEvent.setOnClickListener {
            if (blankText()) {
                refEvents.document(eventId!!).get().addOnSuccessListener { it ->
                    if (it.exists()) {
                        refEvents.document(eventId!!)
                            .update("nombre", binding.nombreEvento.text.toString())
                            .addOnFailureListener { alerta("Error\n${it.message}") }
                        refEvents.document(eventId!!)
                            .update("tipo", binding.tipoEvento.text.toString())
                            .addOnFailureListener { alerta("Error\n${it.message}") }
                        refEvents.document(eventId!!)
                            .update("fecha", binding.fechaEvento.text.toString())
                            .addOnFailureListener { alerta("Error\n${it.message}") }
                        refEvents.document(eventId!!)
                            .update("estado", binding.estadoEvento.text.toString())
                            .addOnSuccessListener { cleanTexts() }
                            .addOnFailureListener { alerta("Error\n${it.message}") }
                    } else {
                        mensaje("No existe el docuemnto")
                    }
                }//Fin del OnSuccess
                    .addOnFailureListener { alerta("Error\n${it.message}") }
                mensaje("Evento actualizado")
            } else {
                alerta("No debe tener espacios en blanco o estar vacio ningún campo")
            }
        }
    } // fin del onCreate

    private fun cleanTexts() {
        binding.nombreEvento.text!!.clear()
        binding.tipoEvento.text!!.clear()
        binding.fechaEvento.text!!.clear()
        binding.estadoEvento.text!!.clear()
    }

    private fun blankText(): Boolean {
        if (binding.nombreEvento.text!!.isEmpty() || binding.nombreEvento.text!!.isEmpty() ||
            binding.tipoEvento.text!!.isEmpty() || binding.tipoEvento.text!!.isEmpty() ||
            binding.fechaEvento.text!!.isEmpty() || binding.fechaEvento.text!!.isEmpty() ||
            binding.estadoEvento.text!!.isEmpty() || binding.estadoEvento.text!!.isEmpty()
        ) {
            return false
        } else if (binding.nombreEvento.text!!.isBlank() || binding.nombreEvento.text!!.isBlank() ||
            binding.tipoEvento.text!!.isBlank() || binding.tipoEvento.text!!.isBlank() ||
            binding.fechaEvento.text!!.isBlank() || binding.fechaEvento.text!!.isBlank() ||
            binding.estadoEvento.text!!.isBlank() || binding.estadoEvento.text!!.isBlank()
        ) {
            return false
        }
        return true
    }

    private fun getEventData(eventId: String) {
        refEvents.document(eventId).get().addOnSuccessListener {
            if (it.exists()) {

                val nombre = it.getString("nombre")
                val tipo = it.getString("tipo")
                val fecha = it.getString("fecha")
                val estado = it.getString("estado")
                binding.nombreEvento.setText(nombre)
                binding.tipoEvento.setText(tipo)
                binding.fechaEvento.setText(fecha)
                binding.estadoEvento.setText(estado)

            } else {
                alerta("No existe ese documento")
            }
        }.addOnFailureListener {
            alerta("Atencion!!\n${it.message}")
        }
    }// fin del metodo del update

    private fun alerta(cadena: String) {
        AlertDialog.Builder(this).setTitle("Atencion!!")
            .setMessage(cadena).show()
    }

    private fun mensaje(cadena: String) {
        Toast.makeText(this, cadena, Toast.LENGTH_SHORT).show()
    }

}// fin de la clase