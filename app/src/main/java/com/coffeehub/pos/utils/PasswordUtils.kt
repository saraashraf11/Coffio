package com.coffeehub.pos.utils

import java.security.MessageDigest

object PasswordUtils {
    fun hash(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(password.toByteArray())
        return digest.fold("") { str, byte -> str + "%02x".format(byte) }
    }

    fun verify(password: String, hash: String): Boolean {
        return hash(password) == hash
    }
}
