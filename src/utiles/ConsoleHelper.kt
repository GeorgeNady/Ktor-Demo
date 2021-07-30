package com.george.utiles

object ConsoleHelper {

    private val ANSI_RESET = "\u001B[0m"
    private val ANSI_BLACK = "\u001B[30m"
    private val ANSI_RED = "\u001B[31m"
    private val ANSI_GREEN = "\u001B[32m"
    private val ANSI_YELLOW = "\u001B[33m"
    private val ANSI_BLUE = "\u001B[34m"
    private val ANSI_PURPLE = "\u001B[35m"
    private val ANSI_CYAN = "\u001B[36m"
    private val ANSI_WHITE = "\u001B[37m"

    fun printlnBlack(text:String) = println("$ANSI_BLACK $text $ANSI_RESET")
    fun printlnRed(text:String) = println("$ANSI_RED $text $ANSI_RESET")
    fun printlnGreen(text:String) = println("$ANSI_GREEN $text $ANSI_RESET")
    fun printlnYellow(text:String) = println("$ANSI_YELLOW $text $ANSI_RESET")
    fun printlnBlue(text:String) = println("$ANSI_BLUE $text $ANSI_RESET")
    fun printlnPurple(text:String) = println("$ANSI_PURPLE $text $ANSI_RESET")
    fun printlnCyan(text:String) = println("$ANSI_CYAN $text $ANSI_RESET")
    fun printlnWhite(text:String) = println("$ANSI_WHITE $text $ANSI_RESET")

}