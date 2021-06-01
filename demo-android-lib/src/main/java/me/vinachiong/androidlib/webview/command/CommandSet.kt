package me.vinachiong.androidlib.webview.command

import me.vinachiong.androidlib.webview.interfaces.Command

/**
 *
 * 命令集合
 * @author vina.chiong
 * @version v1.0.0
 */
internal abstract class CommandSet {
    /** 命令集合 */
    private val commands: HashMap<String, Command> = HashMap()
    
    fun getCommand(name: String): Command? = commands[name]
    
    /** 本命令集合的登记，或者标记 */
    abstract val level: Int
    fun registerCommand(command: Command) {
        commands[command.name] = command
    }
    
}