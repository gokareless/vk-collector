import scala.io.Source

object Collector {

    val DIR_BASE = "/home/myan/data"
    val DIR_SOURCED = s"$DIR_BASE/sourced"
    val DIR_DOWNLOADED = s"$DIR_BASE/downloaded"
    val DIR_PREPARED = s"$DIR_BASE/prepared"
    val BUCKET_TARGETED = "vk-collect-bucket"
    val PROJECT_ID = "vkollect"
    val FILE_NAME = "userpic0s.txt"
    val STEP = 3

    def main(args: Array[String]): Unit = {
        println("Collecting started")
        var counter = 0
        val source = Source.fromFile(s"$DIR_SOURCED/$FILE_NAME")
        val list = source.getLines().to(LazyList)
        while (counter < list.size) {
            counter += STEP
            val chunk = list.slice(counter - STEP, counter)
            val urls = chunk.map(s => s.split("\\t")).map(a => a(1).trim)
            val ids = chunk.map(s => s.split("\\t")).map(a => a(0).trim)
            val name = Namer(counter, "file-").name()
            println(s"Downloading $counter elements")
            Downloader(urls, DIR_DOWNLOADED).download()
            println(s"Preparing $counter elements")
            Preparer(DIR_DOWNLOADED, DIR_PREPARED, name).prepare()
            println(s"Uploading $counter elements")
            Uploader(DIR_PREPARED, BUCKET_TARGETED, PROJECT_ID).upload()
            println(s"Cleaning $counter elements")
            Cleaner(List(DIR_DOWNLOADED, DIR_PREPARED)).clean()
            println(s"Reporting $counter elements")
            Reporter(DIR_BASE, counter, ids).report()
        }
        source.close()
        println("Collecting finished")

    }
}
