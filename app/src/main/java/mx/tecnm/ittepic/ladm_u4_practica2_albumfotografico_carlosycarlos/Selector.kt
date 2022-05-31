package mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth

class Selector : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)
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
                finish()
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