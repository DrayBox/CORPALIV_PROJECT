package cl.duoc.corpaliv.domain

import cl.duoc.corpaliv.domain.security.PasswordHasher
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    @Test
    fun claveCorrecta_seVerifica() {
        val r = PasswordHasher.hash("prueba123")
        assertTrue(PasswordHasher.verificar("prueba123", r.hash, r.sal))
    }

    @Test
    fun claveIncorrecta_noSeVerifica() {
        val r = PasswordHasher.hash("prueba123")
        assertFalse(PasswordHasher.verificar("prueba124", r.hash, r.sal))
    }

    @Test
    fun mismaClave_generaHashesDistintosPorLaSal() {
        val a = PasswordHasher.hash("prueba123")
        val b = PasswordHasher.hash("prueba123")
        assertNotEquals(a.sal, b.sal)
        assertNotEquals(a.hash, b.hash)
    }

    @Test
    fun elHash_noContieneLaClave() {
        val r = PasswordHasher.hash("prueba123")
        assertFalse(r.hash.contains("prueba123"))
    }
}
