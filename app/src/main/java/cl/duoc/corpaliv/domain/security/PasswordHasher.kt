package cl.duoc.corpaliv.domain.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// Hash de la clave con PBKDF2-HMAC-SHA256.
// - La sal es aleatoria por usuario: dos claves iguales generan hashes distintos.
// - Las iteraciones hacen lento cada intento, lo que dificulta la fuerza bruta.
// - Nunca se guarda la clave en texto plano: solo el hash y la sal (Base64).
object PasswordHasher {

    private const val ALGORITMO = "PBKDF2WithHmacSHA256"
    private const val ITERACIONES = 120_000
    private const val LARGO_LLAVE_BITS = 256
    private const val LARGO_SAL_BYTES = 16

    data class Resultado(val hash: String, val sal: String)

    fun hash(clave: String): Resultado {
        val sal = ByteArray(LARGO_SAL_BYTES).also { SecureRandom().nextBytes(it) }
        return Resultado(hash = codificar(derivar(clave, sal)), sal = codificar(sal))
    }

    fun verificar(clave: String, hashGuardado: String, salGuardada: String): Boolean {
        val calculado = derivar(clave, decodificar(salGuardada))
        // Comparación en tiempo constante: no filtra pistas por el tiempo de respuesta
        return MessageDigest.isEqual(calculado, decodificar(hashGuardado))
    }

    private fun derivar(clave: String, sal: ByteArray): ByteArray {
        val spec = PBEKeySpec(clave.toCharArray(), sal, ITERACIONES, LARGO_LLAVE_BITS)
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun codificar(bytes: ByteArray) = Base64.getEncoder().encodeToString(bytes)
    private fun decodificar(texto: String) = Base64.getDecoder().decode(texto)
}
