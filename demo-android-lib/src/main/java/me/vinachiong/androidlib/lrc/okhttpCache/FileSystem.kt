package me.vinachiong.androidlib.lrc.okhttpCache

import okio.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

/**
 * 抽象一个文件系统，具有文件操作的能力
 *
 * @author vina.chiong
 * @version v1.0.0
 */
interface FileSystem {
    
    companion object {
        
        @JvmField
        val SYSTEM: FileSystem = object : FileSystem {
    
            @Throws(FileNotFoundException::class)
            override fun source(file: File): Source = file.source()
    
            @Throws(FileNotFoundException::class)
            override fun sink(file: File): Sink {
                return try {
                    file.sink()
                } catch (_: FileNotFoundException) {
                    file.parentFile.mkdirs()
                    file.sink()
                }
            }
    
            @Throws(FileNotFoundException::class)
            override fun appendingSink(file: File): Sink {
                return try {
                    file.appendingSink()
                } catch (_: FileNotFoundException) {
                    file.parentFile.mkdirs()
                    file.appendingSink()
                }
            }
    
            @Throws(IOException::class)
            override fun delete(file: File) {
                if (!file.delete() && file.exists()) {
                    throw IOException("failed to delete $file")
                }
            }
            
            override fun exists(file: File): Boolean = file.exists()
            
            override fun size(file: File): Long = file.length()
    
            @Throws(IOException::class)
            override fun rename(from: File, to: File) {
                delete(to)
                if (!from.renameTo(to)) {
                    throw IOException("failed to rename $from to $to")
                }
            }
            
            override fun deleteContents(directory: File) {
                val files = directory.listFiles() ?: throw IOException("not a readable directory: $directory")
                files.forEach { file ->
                    if (file.isDirectory) {
                        deleteContents(file)
                    }
                    if (!file.delete()) {
                        throw IOException("failed to delete $file")
                    }
                }
            }
    
            override fun toString(): String = "FileSystem.SYSTEM"
        }
    }
    
    /** 读取[file]，转换成Source，相当于InputStream */
    @Throws(FileNotFoundException::class)
    fun source(file: File): Source
    
    @Throws(FileNotFoundException::class)
    fun sink(file: File): Sink
    
    @Throws(FileNotFoundException::class)
    fun appendingSink(file: File): Sink
    
    @Throws(IOException::class)
    fun delete(file: File)
    
    @Throws(IOException::class)
    fun exists(file: File): Boolean
    
    fun size(file: File): Long
    
    @Throws(IOException::class)
    fun rename(from: File, to: File)
    
    @Throws(IOException::class)
    fun deleteContents(directory: File)
}