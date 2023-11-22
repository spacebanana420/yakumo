package yakumo
import yakumo.client.*
import yakumo.server.*

import java.io.File
import java.io.FileOutputStream
import scala.sys.exit

@main def main() = {
  val cyan = foreground("cyan")
  val green = foreground("green")
  val default = foreground("default")
  if File("config.txt").isFile() == false then
    createConfig()
  val configOk = isConfigFine()
  while true do {
    //clear()
    val mode = readUserInputSpawn(s"$cyan[Yakumo v0.9]\n$default--Choose an option--\n${green}0:${default} Exit            ${green}1:${default} Server            ${green}2:${default} Client\n${green}3:${default} Show config     ${green}4:${default} Show log\n")
    userChoice(mode)
  }
}

private def userChoice(mode: String) = {
  if "01234".contains(mode) == true && mode != "" then
    mode match
      case "0" =>
        exit()
      case "1" =>
        if isConfigFine() == true then
          server(getPort())
        else
          printStatus("You need to have a properly configured config.txt file!\nCancelling server launch", true)
          exit()
      case "2" =>
        // val file = getFile()
        val ip = getIP()
        val port = getPort()
        try
          client(ip, port)
        catch
          case e: Exception => readUserInput("Connection failed!\nMaybe the server isn't open?\n\nPress enter to continue")
      case "3" =>
        showConfig()
      case "4" =>
        showLog()
}

private def showConfig() = {
  val isok = isConfigFine()
  val config = getConfigFile()
  val maxperfile = getFileLimit(config, "perfile")
  val maxtotal = getFileLimit(config, "total")
  val password = getPassword(config)
  val dir = getStorageDirectory(config)
  val title = foreground("cyan")

  val default = foreground("default")
  val okColor =
    if isok == true then
      foreground("green")
    else
      foreground("red")

  readUserInput(s"$title//Main settings//$default\n\nPassword: $password\nStorage location: $dir\n\n$title//File settings//$default\n\nFile size limit: $maxperfile GB\nStorage size limit: $maxtotal GB\n\nIs the config ok?: $okColor$isok$default\n\nPress enter to continue")
}

private def getPort(): Int = {
  val yellow = foreground("yellow")
  val default = foreground("default")
  val portstr = readUserInput(s"Type the port to use\nDefault: ${yellow}42069${default}\n")
  try
    portstr.toInt
  catch
    case e: Exception =>
      42069
}

// def getFile(): Array[String] = {
//   val filepath = browse()
//   val relative = getRelativePath(filepath)
//   Array[String](relative, filepath)
// }

// def getDir(): String = {
//   val default = File("").getAbsolutePath()
//   val answer = readUserInput(s"Choose the server's storage directory\nDefault: $default")
//   if File(answer).isDirectory() == false then
//     println(s"The directory $answer does not exist! Using default directory")
//     default
//   else
//     answer
// }

private def getIP(): String = {
  val yellow = foreground("yellow")
  val default = foreground("default")
  val answer = readUserInput(s"Input the IP to connect to (default: ${yellow}localhost${default})")
  if answer != "" then
    answer
  else
    "localhost"
}
