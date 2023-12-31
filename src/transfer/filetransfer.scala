package yakumo.transfer
import yakumo.*

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

import java.net.Socket
import java.io.InputStream
import java.io.OutputStream

//File transfer operations, both used by the server and client

def download(is: InputStream, name: String, len: Long, dir: String = "") = {
  val fileout =
    if dir != "" && File(dir).isDirectory() == true then
      FileOutputStream(s"$dir${getDownloadName(name)}")
    else
      FileOutputStream(getDownloadName(name))
  var downloaded = 0
  val buffer = new Array[Byte](4096)
  while len - downloaded >= 4096 do
    while is.available() <= 4096 do Thread.sleep(100)
    is.read(buffer)
    fileout.write(buffer)
    downloaded += 4096
  if downloaded < len then
    val finalbytes = new Array[Byte]((len - downloaded).toInt)
    is.read(finalbytes)
    fileout.write(finalbytes)
  fileout.close()
}

def upload(os: OutputStream, filepath: String) =
  val filein = FileInputStream(filepath)
  val buffer = new Array[Byte](4096)
  while filein.available() >= 4096 do
    filein.read(buffer)
    os.write(buffer)
  if filein.available() > 0 then
    val finaldata = new Array[Byte](filein.available())
    filein.read(finaldata)
    os.write(finaldata)
  filein.close()

def getDownloadName(name: String, i: Int = 1): String =
  val finalname =
    if File(name).exists() == false then
      name
    else if File(s"new_$name").exists() == false then
      s"new_$name"
    else if File(s"new${i}_$name").exists() == false then
      s"new${i}_$name"
    else
      getDownloadName(name, i+1)
  finalname

def isUtf16(name: String, i: Int = 0, isutf: Boolean = false): Boolean =
  if i == name.length then
    isutf
  else if name(i).toShort == name(i).toByte && isutf == false then
    isUtf16(name, i+1, false)
  else
    isUtf16(name, i+1, true)
