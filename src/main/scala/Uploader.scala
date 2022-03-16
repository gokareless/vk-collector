import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.{BlobId, BlobInfo, StorageOptions}
import com.google.common.collect.Lists

import java.io.{File, FileInputStream}
import java.nio.file.Files
import java.nio.file.Paths

object Uploader {

  def apply(pathPrepared: String, bucketTargeted: String, projectId: String): Uploader = {
    new Uploader(pathPrepared, bucketTargeted, projectId)
  }
}


class Uploader(pathPrepared: String, bucketTargeted: String, projectId: String) {

  def upload(): Unit = {
    val credentials = GoogleCredentials
      .fromStream(new FileInputStream("/home/myan/.keys/vkollect-key.json"))
      .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
    for (file <- getListOfFiles(pathPrepared)) {
      val storage = StorageOptions.newBuilder.setProjectId(projectId).setCredentials(credentials).build.getService
      val blobId = BlobId.of(bucketTargeted, file.getName)
      val blobInfo = BlobInfo.newBuilder(blobId).build
      storage.create(blobInfo, Files.readAllBytes(Paths.get(file.getAbsolutePath)))
    }
  }

  def getListOfFiles(directory: String):List[File] = {
    val d = new File(directory)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }
}
