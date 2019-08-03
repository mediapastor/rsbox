package io.rsbox.engine

import java.io.*
import java.lang.RuntimeException
import java.net.URL
import java.util.*
import java.util.zip.ZipFile

/**
 * @author Kyle Escobar
 */

object Setup {

    val revision = 181
    val cache_zip = "https://github.com/rsbox/cache/raw/rev_181/cache.zip"
    val xteas = "https://github.com/rsbox/cache/raw/rev_181/xteas.json"

    fun downloader() {
        val input = Scanner(System.`in`)
        println("It appears you are missing the OSRS cache files.")
        println("Would you like to download revision $revision cache from the RSBox repo? (y/n)")
        val download = if(input.hasNext()) input.nextLine() in arrayOf("y", "yes", "true") else false
        if(download) {
            println("Downloading revision $revision cache.")
            this.downloadCache()
            println("Downloading revision $revision xteas.")
            this.downloadXteas()
        } else {
            throw RuntimeException("Please put the cache files in 'rsbox/data/cache/' and the corresponding xteas in 'rsbox/data/xteas/'.")
        }
    }

    private fun downloadCache() {
        val cache_url = URL(cache_zip)
        val cache_con = cache_url.openConnection()

        val cache_stream = DataInputStream(cache_url.openStream())

        val buffer = ByteArray(cache_con.contentLength)
        cache_stream.readFully(buffer)
        cache_stream.close()

        val output = DataOutputStream(FileOutputStream(File("rsbox/data/cache/cache.zip")))
        output.write(buffer)
        output.flush()
        output.close()
        println("Cache download completed. Extracting files.")


        ZipFile("rsbox/data/cache/cache.zip").use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { stream ->
                    File("rsbox/data/cache/${entry.name}").outputStream().use { output ->
                        stream.copyTo(output)
                    }
                }
            }
        }

        File("rsbox/data/cache/cache.zip").delete()
        println("Cache extraction completed successfully.")
    }

    private fun downloadXteas() {
        val url = URL(xteas)
        val con = url.openConnection()
        val stream = DataInputStream(url.openStream())

        val buffer = ByteArray(con.contentLength)
        stream.readFully(buffer)
        stream.close()

        if(File("rsbox/data/xteas/xteas.json").exists()) {
            File("rsbox/data/xteas/xteas.json").delete()
            println("Deleted existing xteas file.")
        }

        val output = DataOutputStream(FileOutputStream(File("rsbox/data/xteas/xteas.json")))
        output.write(buffer)
        output.flush()
        output.close()

        println("Xteas download completed successfully.")
    }
}