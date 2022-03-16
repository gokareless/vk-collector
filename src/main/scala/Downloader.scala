import sys.process.*
import java.net.URL
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import scala.language.postfixOps

object Downloader {

  def apply(urls: LazyList[String], pathDownloaded: String): Downloader = {
    new Downloader(urls, pathDownloaded)
  }
}

class Downloader(urls: LazyList[String], pathDownloaded: String) {

  def download(): Unit = {
    for (url <- urls) {
      if (!url.equals("https://vk.com/images/deactivated_200.png") &&
          !url.equals("https://vk.com/images/camera_200.png")) {
        val filename =
          if (url.contains("?"))
            url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"))
          else
            url.substring(url.lastIndexOf("/") + 1)
        Files.createDirectories(Paths.get(pathDownloaded))
        val path = s"$pathDownloaded/$filename"
        val res = s"wget -O $path $url" !!
      }
    }
  }
}
