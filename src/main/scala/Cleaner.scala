import java.io.File
import java.nio.file.Path

object Cleaner {

  def apply(paths: List[String]): Cleaner = {
    new Cleaner(paths)
  }
}


class Cleaner(paths: List[String]) {

  def clean(): Unit = {
    for (path <- paths) {
      deleteRecursively(new File(path))
    }
  }
  
  def deleteRecursively(file: File): Unit = {
    if (file.isDirectory) {
      file.listFiles.foreach(deleteRecursively)
    }
    if (file.exists && !file.delete) {
      throw new Exception(s"Unable to delete ${file.getAbsolutePath}")
    }
  }
}
