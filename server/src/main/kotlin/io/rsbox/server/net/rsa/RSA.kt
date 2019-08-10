package io.rsbox.server.net.rsa

import io.rsbox.server.ServerConstants
import mu.KLogging
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemReader
import org.bouncycastle.util.io.pem.PemWriter
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

object RSA : KLogging() {
    lateinit var keyPath: Path

    lateinit var exponent: BigInteger

    lateinit var modulus: BigInteger

    private var radix = 16

    fun init() {
        keyPath = Paths.get("${ServerConstants.RSA_PATH}private.pem")

        if(!Files.exists(keyPath)) {
            generatePair()
        } else {
            load()
        }
    }

    private fun generatePair() {
        val scanner = Scanner(System.`in`)
        println("RSA private key not found in path: ${ServerConstants.RSA_PATH}.")
        println("Would you like to generate one? (y/n)")
        if(scanner.hasNext() && scanner.nextLine() in arrayOf("y","yes","true","si","")) {
            logger.info { "Generating RSA key pair..." }

            Security.addProvider(BouncyCastleProvider())

            val keypairGen = KeyPairGenerator.getInstance("RSA","BC")
            keypairGen.initialize(2048)
            val keyPair = keypairGen.generateKeyPair()

            val privateKey = keyPair.private as RSAPrivateKey
            val publicKey = keyPair.public as RSAPublicKey
            exponent = publicKey.publicExponent
            modulus = publicKey.modulus

            var bar = "--------------------------------------"
            for(i in 0..modulus.toString(radix).length) {
                bar += "-"
            }

            println(bar)
            println("- Public Key: ${exponent.toString(radix)}")
            println("- Modulus: ${modulus.toString(radix)}")
            println(bar)

            PemWriter(Files.newBufferedWriter(keyPath)).use { writer ->
                writer.writeObject(PemObject("RSA PRIVATE KEY", privateKey.encoded))
            }

            PemWriter(Files.newBufferedWriter(Paths.get("${ServerConstants.RSA_PATH}public.pem"))).use { writer ->
                writer.writeObject(PemObject("RSA PUBLIC KEY", publicKey.encoded))
            }

            PemWriter(Files.newBufferedWriter(Paths.get("${ServerConstants.RSA_PATH}modulus.txt"))).use { writer ->
                writer.write(modulus.toString(radix))
            }

            logger.info { "RSA key files have been created. Continuing server initialization." }
            init()
        }
    }

    private fun load() {
        PemReader(Files.newBufferedReader(keyPath)).use { reader ->
            val pem = reader.readPemObject()
            val keySpec = PKCS8EncodedKeySpec(pem.content)

            Security.addProvider(BouncyCastleProvider())
            val factory = KeyFactory.getInstance("RSA","BC")

            val privateKey = factory.generatePrivate(keySpec) as RSAPrivateKey
            exponent = privateKey.privateExponent
            modulus = privateKey.modulus
        }
    }
}