package mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

        val spinner: Spinner = binding.spStatus
        ArrayAdapter.createFromResource(
            this,
            R.array.estadosEvento,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

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
                            .update("estado", binding.spStatus.selectedItem.toString())
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
    }

    private fun blankText(): Boolean {
        if (binding.nombreEvento.text!!.isEmpty() || binding.nombreEvento.text!!.isEmpty() ||
            binding.tipoEvento.text!!.isEmpty() || binding.tipoEvento.text!!.isEmpty() ||
            binding.fechaEvento.text!!.isEmpty() || binding.fechaEvento.text!!.isEmpty()
        ) {
            return false
        } else if (binding.nombreEvento.text!!.isBlank() || binding.nombreEvento.text!!.isBlank() ||
            binding.tipoEvento.text!!.isBlank() || binding.tipoEvento.text!!.isBlank() ||
            binding.fechaEvento.text!!.isBlank() || binding.fechaEvento.text!!.isBlank()
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

                if(estado == "Abierto"){
                    binding.spStatus.setSelection(0)
                }else if(estado == "Cerrado"){
                    binding.spStatus.setSelection(1)
                } else{
                    binding.spStatus.setSelection(2)
                }

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