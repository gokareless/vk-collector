import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveOutputStream}
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream

import java.io.{BufferedOutputStream, File, IOException}
import java.nio.file.{FileVisitResult, Files, Path, Paths, SimpleFileVisitor}
import java.nio.file.attribute.BasicFileAttributes
import scala.util.Using

object Preparer {

  def apply(pathDownloaded: String, pathPrepared: String, name: String): Preparer = {
    new Preparer(pathDownloaded, pathPrepared, name)
  }
}

class Preparer(pathDownloaded: String, pathPrepared: String, name: String) {

  def prepare(): Unit = {
    try {
      val source = Paths.get(pathDownloaded)
      createTarGzipFolder(source)
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  @throws(classOf[IOException])
  def createTarGzipFolder(source: Path): Unit = {
    if (!Files.isDirectory(source))  throw new IOException("Please provide a directory.")
    val directory = new File(pathPrepared)
    if (!directory.exists) directory.mkdir
    val fileName = s"${directory.getAbsolutePath}/${name}.tar.gz"
    val tarFileName = new File(fileName).getAbsolutePath
    Using.Manager { use =>
      val fOut = use(Files.newOutputStream(Paths.get(tarFileName)))
      val buffOut = use(new BufferedOutputStream(fOut))
      val gzOut = use(new GzipCompressorOutputStream(buffOut))
      val tOut = use(new TarArchiveOutputStream(gzOut))
      tOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX)
      val visitor = new CustomFileVisitor(tOut, source)
      Files.walkFileTree(source, visitor)
      tOut.finish();
    }
  }
}

class CustomFileVisitor(tOut: TarArchiveOutputStream, source: Path) extends SimpleFileVisitor[Path] {
  override def visitFile(file: Path, attributes: BasicFileAttributes): FileVisitResult = {

    if (attributes.isSymbolicLink) return FileVisitResult.CONTINUE
    val targetFile = source.relativize(file)
    try {
      val tarEntry = new TarArchiveEntry(file.toFile, targetFile.toString)
      tOut.putArchiveEntry(tarEntry)
      Files.copy(file, tOut)
      tOut.closeArchiveEntry()
    } catch {
      case _: IOException => println(s"Unable to tar.gz : $file")
      case _: IllegalArgumentException => println(s"Unable to tar.gz : $file")
    }
    FileVisitResult.CONTINUE
  }

  override def visitFileFailed(file: Path, exc: IOException): FileVisitResult = {
    println(s"Unable to tar.gz : $file")
    FileVisitResult.CONTINUE
  }
}
