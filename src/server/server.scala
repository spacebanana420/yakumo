package yakumo.server
import yakumo.*
import yakumo.transfer.*
import yakumo.config.*

import java.net.ServerSocket
import java.net.Socket
import java.io.InputStream
import java.io.OutputStream
import java.io.File

//This file includes the code exclusive to the server

private def getClientIP(sock: Socket): String = sock.getRemoteSocketAddress().toString()

def server(port: Int = 42069) = {
  println(s"Opened server with port ${foreground("green")}$port${foreground("default")}\nWaiting for incoming requests...")
  writeLog(s"///Opened server with port $port///\n")
  val ss = ServerSocket(port)
  while true do {
    try
      println("---Opening new connection---\n")
      writeLog("Server is awating connection")
      serverSession(ss)
      println("Closing connection")
    catch
      case e: Exception =>
        printStatus("The server has crashed or the client disconnected unexpectedly!", true)
        writeLog("Server crashed or connection was interrupted!")
  }
  ss.close()
  writeLog(s"///Server with port $port closed///\n")
}

def serverSession(ss: ServerSocket) = {
  val sock = ss.accept()
  val ip = getClientIP(sock)
  val is = sock.getInputStream()
  val os = sock.getOutputStream()

  val clog = s"Client of IP ${foreground("green")}$ip${foreground("default")} connected to the server"
  println(clog); writeLog(clog)

  val config = getConfigFile()
  val usepass = passwordEnabled(config)
  val password = getPassword(config)
  val dir = getStorageDirectory(config)

  def server_listen(): Unit = {
    val msg = receiveMessage(is)
    if msg != "close" then
      msg match
        case "getfiles" =>
          val log = "Client requested storage information"
          println(log); writeLog(log)
          sendServerFileInfo(os, dir)
        case "upload" =>
          val log = "Client requested file upload"
          println(log); writeLog(log)
          serverDownload(is, os, dir)
        case "download" =>
          val log = "Client requested file download"
          println(log); writeLog(log)
          serverUpload(is, os, dir)
        case "delete" => //implement!!!
      server_listen()
  }
  println("Connection established with client")

  if usepass == false then
    sendByte(1, os)
    server_listen()
  else
    println("Waiting for client to send the password")
    sendByte(0, os)
    while is.available() == 0 do
      Thread.sleep(250) //add a timeout
    val inpass = readString(is.available(), is)
    if inpass == password then
      val log = "Password is correct, proceeding"
      println(log); writeLog(log)
      sendByte(1, os)
      server_listen()
    else
      val log = "Incorrect password received"
      printStatus(log, false); writeLog(log)
      sendByte(0, os)
  sock.close()
}

def serverDownload(is: InputStream, os: OutputStream, dir: String) = {
  val nameLen = readInt(is)
  val name = readString(nameLen, is)
  val fileLen = readLong(is)

  if isFileValid(fileLen, nameLen) == true then
    println(s"\n--Downloading File--\n  * Name: $name\n  * Length: $fileLen bytes\n")
    writeLog(s"///Downloading $name\nLength: $fileLen bytes///")
    sendByte(1, os)
    download(is, name, fileLen, dir)
    println(s"Finished downloading $name!")
  else
    printStatus(s"Requested file transfer exceeds configured limit or file name is empty", true)
    sendByte(0, os)
}

def serverUpload(is: InputStream, os: OutputStream, dir: String) = {
  val files = getServerFiles(dir)
  if files.length == 0 then
    printStatus("The server storage is empty, there is nothing to send to the client", false)
  else
    val chosen = readInt(is)
    val len = File(s"$dir${files(chosen)}").length() //can crash the connection!
    sendLong(len, os)

    println(s"\n--Uploading File--\n  * Name: ${files(chosen)}\n  * Length: $len bytes")
    writeLog(s"Uploading ${files(chosen)}\nLength: $len bytes")
    upload(os, s"$dir${files(chosen)}")
    println(s"Finished uploading ${files(chosen)}!")
}
