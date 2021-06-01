package me.vinachiong.androidlib.lrc.okhttpCache

import okio.BufferedSink
import okio.Source
import java.io.*
import kotlin.jvm.Throws

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class DiskLruCache internal constructor(
    internal val fileSystem: FileSystem,
    val directory: File,
    private val appVersion: Int,
    internal val valueCount: Int,
    maxSize: Long,
    taskRunner: TaskRunner
): Closeable, Flushable {
    
    @get:Synchronized @set:Synchronized
    var maxSize: Long = maxSize
        set(value) {
            field = value
            if (initialized) {
                // 调度cleanupTask // Trim the existing store if necessary.
            }
        }
    
    
    private val journalFile: File
    private val journalFileTmp: File
    private val journalFileBackup: File
    private var size: Long = 0L
    private var journalWriter: BufferedSink? = null
    internal val lruEntries = LinkedHashMap<String, Entry>(0, 0.75f, true)
    private var redundantOpCount: Int = 0
    private var hasJournalError: Boolean = false
    private var civilizedFileSystem: Boolean = false
    
    // 以下属性的读写必须当 synchronized(this)
    private var initialized: Boolean = false
    internal var closed: Boolean = false
    private var mostRecentTrimFailed: Boolean = false
    private var mostRecentRebuildFailed: Boolean = false
    
    // 区分旧snapshot和当前snapshot，每个entry，在编辑修改被提交时候会被赋予sequenceNumber
    // snapshot被认为是过期的，如果sequenceNumber与entry的不相等
    private var nextSequenceNumber: Long = 0
    private val cleanupQueue = taskRunner.newQueue()
    private val cleanTask = object: Task("") {
        override fun runOnce(): Long {
            TODO("Not yet implemented")
        }
    }
    
    init {
        require(maxSize > 0L) { "maxSize <= 0" }
        require(valueCount > 0) { "valueCount <= 0" }
        
        this.journalFile = File(directory, JOURNAL_FILE)
        this.journalFileTmp = File(directory, JOURNAL_FILE_TEMP)
        this.journalFileBackup = File(directory, JOURNAL_FILE_BACKUP)
    }
    
    @Synchronized @Throws(IOException::class)
    fun initialize() {
    
    }
    
    @Throws(IOException::class)
    private fun readJournal() {}
    
//    @Throws(FileNotFoundException::class)
//    private fun newJournalWriter(): BufferedSink {return }
    
    private fun readJournalLine(line: String) {}
    
    private fun processJournal() {}
    private fun rebuildJournal() {}
    
    operator fun get(key: String): Snapshot? {
        initialize()
        return null
    }
    
    fun edit(key: String, expectedSequenceNumber: Long = ANY_SEQUENCE_NUMBER): Editor? {
        return null
    }
    
    fun size(): Long {
        initialize()
        return size
    }
    
    internal fun completeEdit(editor: Editor, success: Boolean) {
    
    }
    
    private fun journalRebuildRequired(): Boolean {
        val redundantOpCompactThreshold = 2000
        return redundantOpCount >= redundantOpCompactThreshold
                && redundantOpCount >= lruEntries.size
    }
    
//    fun remove(key: String): Boolean {
//        initialize()
//    }
    
//    @Throws(IOException::class)
//    internal fun removeEntry(entry: Entry): Boolean {
//        if (!civilizedFileSystem) {
//
//        }
//    }
    
    @Synchronized
    private fun checkNotClosed() {check(!closed) {"cache is closed"}}
    
    @Synchronized @Throws(IOException::class)
    override fun flush() {
        if (!initialized) return
        checkNotClosed()
        trimToSize()
        journalWriter!!.flush()
    }
    
    internal inner class Entry internal constructor(
        internal val key: String
    ) {
        internal val lengths: LongArray = LongArray(valueCount)
        internal val cleanFiles = mutableListOf<File>()
        internal val dirtyFiles = mutableListOf<File>()
        
        // 可读，表示已发布
        internal var readable: Boolean = false
        // entry需要被删除且 读写都已经完成后
        internal var zombie: Boolean = false
        
    }
    
    @Synchronized fun isClosed() :Boolean = closed
    
    @Synchronized @Throws(IOException::class)
    override fun close() {
        if (!initialized || closed) {
            closed = true
            return
        }
    }
    
    @Throws(IOException::class)
    fun trimToSize() {
        while(size> maxSize) {
            if (!removeOldestEntry()) return
        }
        mostRecentTrimFailed = false
    }
    private fun removeOldestEntry(): Boolean {
        for (toEvict in lruEntries.values) {
            if (!toEvict.zombie) {
//                removeEntry(toEvict)
                return true
            }
        }
        return false
    }
    
    inner class Snapshot internal constructor(
        private val key: String,
        private val sequenceNumber: Long,
        private val sources: List<Source>,
        private val lengths: LongArray
    ) : Closeable {
        
        fun edit(): Editor? = this@DiskLruCache.edit(key, sequenceNumber)
        fun getSource(index: Int): Source = sources[index]
        fun getLength(index: Int): Long = lengths[index]
        override fun close() {
            for (source in sources) {
                try {
                    source.close()
                } catch (rethrown: RuntimeException) {
                    throw rethrown
                } catch (_: Exception) {
                }
            }
        }
    }
    
    inner class Editor internal constructor(internal val entry: Entry) {
    
    }
    
    companion object {
        @JvmField val JOURNAL_FILE = "journal"
        @JvmField val JOURNAL_FILE_TEMP = "journal.tmp"
        @JvmField val JOURNAL_FILE_BACKUP = "journal.bkp"
        @JvmField val MAGIC = "libcore.io.DiskLruCache"
        @JvmField val VERSION_1 = "1"
        @JvmField val ANY_SEQUENCE_NUMBER: Long = -1
        @JvmField val LEGAL_KEY_PATTERN = "[a-z0-9_-]{1,120}".toRegex()
        @JvmField val CLEAN = "CLEAN"
        @JvmField val DIRTY = "DIRTY"
        @JvmField val REMOVE = "REMOVE"
        @JvmField val READ = "READ"
    }
}