package mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos.databinding.ActivityEditEventBinding

class EditEventActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditEventBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Editar evento"

        binding.regresar.setOnClickListener { finish() }
    }

}