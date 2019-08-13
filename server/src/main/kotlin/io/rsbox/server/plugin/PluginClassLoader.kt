package io.rsbox.server.plugin

import com.google.common.io.ByteStreams
import com.uchuhimo.konf.Config
import io.rsbox.api.plugin.RSPlugin
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLClassLoader
import java.security.CodeSource
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile

/**
 * @author Kyle Escobar
 */

class PluginClassLoader(private val loader: PluginLoader?, parent: ClassLoader, private val config: Config, private val dataFolder: File, private val file: File) : URLClassLoader(arrayOf<URL>(file.toURI().toURL()), parent) {

    private var plugin: RSPlugin
    private val jar = JarFile(file)
    private val manifest = jar.manifest
    private val url = file.toURI().toURL()

    private val classes: ConcurrentHashMap<String, Class<*>> = ConcurrentHashMap()

    init {
        if(loader == null) { println("Loader cannot be null") }

        try {
            val jarClass: Class<*>
            try {
                jarClass = Class.forName(config[PluginSpec.main], true, this)
            } catch(e: ClassNotFoundException) {
                throw InvalidPluginException("Cannot find main class '${config[PluginSpec.main]}'", e)
            }

            val pluginClass: Class<out RSPlugin>

            try {
                pluginClass = jarClass.asSubclass(RSPlugin::class.java)
            } catch(e: ClassCastException) {
                throw InvalidPluginException("Main class '${config[PluginSpec.main]}' does not extend RSPlugin", e)
            }

            plugin = pluginClass.newInstance()
        } catch (e: IllegalAccessException) {
            throw InvalidPluginException("No public constructor.",e)
        } catch(e : InstantiationError) {
            throw InvalidPluginException("Abnormal plugin type.", e)
        }
    }

    override fun findClass(name: String): Class<*> {
        if(name.startsWith("io.rsbox.server") || name.startsWith("io.rsbox.api")) {
            throw ClassNotFoundException(name)
        }

        var result = classes.get(name)
        if(result == null) {
            val path = name.replace('.','/') + ".class"
            val entry = jar.getJarEntry(path)
            if(entry != null) {
                val classBytes: ByteArray
                try {
                    @Suppress("UnstableApiUsage")
                    classBytes = ByteStreams.toByteArray(jar.getInputStream(entry))
                } catch(e : IOException) {
                    throw ClassNotFoundException(name, e)
                }

                // process Class

                val dot = name.lastIndexOf('.')
                if(dot != -1) {
                    val pkgName = name.substring(0, dot)
                    if(getPackage(pkgName) == null) {
                        try {
                            if(manifest != null) {
                                definePackage(pkgName, manifest, url)
                            } else {
                                definePackage(pkgName, null, null, null, null, null, null, null)
                            }
                        } catch(e : IllegalArgumentException) {
                            if(getPackage(pkgName) == null) {
                                throw IllegalStateException("Cannot find package $pkgName")
                            }
                        }
                    }
                }

                val signers = entry.codeSigners
                val source = CodeSource(url, signers)

                result = defineClass(name, classBytes, 0, classBytes.size, source)
            }

            if(result == null) {
                result = super.findClass(name)
            }

            if(result != null) {
                // set loader class
            }

            classes.put(name, result!!)
        }
        return result
    }

    override fun close() {
        try {
            super.close()
        } finally {
            jar.close()
        }
    }

    fun getClasses(): Set<String> {
        return (classes as Map<String, Class<*>>).keys
    }

    fun initialize(rsboxPlugin: RSPlugin?) {
        if(rsboxPlugin == null) {
            throw Exception("Initializing plugin cannot be null.")
        }

        rsboxPlugin.dataFolder = dataFolder
        rsboxPlugin.isEnabled = true
        rsboxPlugin.name = config[PluginSpec.name]
        rsboxPlugin.main = config[PluginSpec.main]
        rsboxPlugin.version = config[PluginSpec.version]
        rsboxPlugin.authors = config[PluginSpec.authors]
        rsboxPlugin.author = config[PluginSpec.author]
        rsboxPlugin.onStart()
    }

    fun getPlugin(): RSPlugin {
        return plugin
    }
}