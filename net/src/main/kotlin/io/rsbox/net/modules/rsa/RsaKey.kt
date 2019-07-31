package io.rsbox.net.modules.rsa

import io.rsbox.api.RSBox
import mu.KotlinLogging
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemReader
import org.bouncycastle.util.io.pem.PemWriter
import java.io.IOException
import java.lang.RuntimeException
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.Security
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

/**
 * @author Kyle Escobar
 */

class RsaKey {

    private val server = RSBox.server

    private var privateKeyPath: Path = Paths.get(server.config.getOrNull<String>("server.rsa.privatekey") ?: "rsbox/data/rsa/private.pem")
    private var publicKeyPath: Path = Paths.get(server.config.getOrNull<String>("server.rsa.publickey") ?: "rsbox/data/rsa/public.pem")
    private var rsaExponentPath: Path = Paths.get(server.config.getOrNull<String>("server.rsa.exponent") ?: "rsbox/data/rsa/rsa.exponent")
    private var rsaModulusPath: Path = Paths.get(server.config.getOrNull<String>("server.rsa.modulus") ?: "rsbox/data/rsa/rsa.modulus")

    private lateinit var exponent: BigInteger

    private lateinit var modulus: BigInteger

    private var radix = server.config.getOrNull<Int>("server.rsa.radix") ?: 16


    init {
        init()
    }

    fun init() {
        if(!Files.exists(privateKeyPath)) {
            val scanner = Scanner(System.`in`)
            println("Private RSA key was not found at: $privateKeyPath")
            println("Would you like to generate one? (y/n)")

            val create = if(scanner.hasNext()) scanner.nextLine() in arrayOf("yes", "y", "true") else true
            if(create) {
                logger.info { "Generating RSA key pair..." }
                createPair(bitCount = (server.config.getOrNull<Int>("server.rsa.bits") ?: 2048))
                scanner.next()
                init()
            } else {
                throw RuntimeException("Private RSA key was not found! Follow the instructions in the documentation / WiKi.")
            }
        }

        try {
            PemReader(Files.newBufferedReader(privateKeyPath)).use { reader ->
                val pem = reader.readPemObject()
                val keySpec = PKCS8EncodedKeySpec(pem.content)

                Security.addProvider(BouncyCastleProvider())
                val factory = KeyFactory.getInstance("RSA", "BC")

                val privateKey = factory.generatePrivate(keySpec) as RSAPrivateKey
                exponent = privateKey.privateExponent
                modulus = privateKey.modulus
            }
        } catch (e : Exception) {
            throw ExceptionInInitializerError(IOException("Error reading RSA key pair.", e))
        }
    }

    fun getExponent(): BigInteger {
        return this.exponent
    }

    fun getModulus(): BigInteger {
        return this.modulus
    }


    /**
     * Credits: Apollo
     *
     * @author Graham
     * @author Major
     * @author Cube
     */
    private fun createPair(bitCount: Int) {
        Security.addProvider(BouncyCastleProvider())

        val keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC")
        keyPairGenerator.initialize(bitCount)
        val keyPair = keyPairGenerator.generateKeyPair()

        val privateKey = keyPair.private as RSAPrivateKey
        val publicKey = keyPair.public as RSAPublicKey

        println("")
        println("Place these keys in the client (find BigInteger(\"10001\" in client code):")
        println("--------------------")
        println("public key: " + publicKey.publicExponent.toString(radix))
        println("modulus: " + publicKey.modulus.toString(radix))
        println("")

        try {
            PemWriter(Files.newBufferedWriter(privateKeyPath)).use { writer ->
                writer.writeObject(PemObject("RSA PRIVATE KEY", privateKey.encoded))
            }

            Files.newBufferedWriter(rsaExponentPath).use { writer ->
                writer.write(publicKey.publicExponent.toString(radix))
            }

            Files.newBufferedWriter(rsaModulusPath).use { writer ->
                writer.write(publicKey.modulus.toString(radix))
            }

        } catch (e: Exception) {
            logger.error(e) { "Failed to write private key to ${privateKeyPath.toAbsolutePath()}" }
        }

        try {
            PemWriter(Files.newBufferedWriter(publicKeyPath)).use { writer ->
                writer.writeObject(PemObject("RSA PUBLIC KEY", publicKey.encoded))
            }
        } catch (e : Exception) {
            logger.error(e) { "Failed to write public key to ${publicKeyPath.toAbsolutePath()}" }
        }

        System.exit(1)
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}