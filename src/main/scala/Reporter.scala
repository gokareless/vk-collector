import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.language.postfixOps
import sys.process.*

object Reporter {

  def apply(reportPath: String, counter: Int, ids: LazyList[String]): Reporter = {
    new Reporter(reportPath, counter, ids)
  }
}


class Reporter(basePath: String, counter: Int, ids: LazyList[String]) {

  def report(): Unit = {
    val content = s"Current count: $counter, Current ids: ${ids.head} - ${ids.last}"
    val path = s"${basePath}/report.log"
    Files.write(Paths.get(path), content.getBytes(StandardCharsets.UTF_8))
  }
}

