package mx.tecnm.ittepic.ladm_u4_practica2_albumfotografico_carlosycarlos

import java.security.MessageDigest


class Hasher {
    fun createHash(): String {
        val bytes = this.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}