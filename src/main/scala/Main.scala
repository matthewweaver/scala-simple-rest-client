import com.google.gson.Gson
import com.typesafe.scalalogging.Logger
import org.apache.http.HttpHeaders
import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.params.HttpConnectionParams

case class Person(id: Int, name: String, age: Int)

object Main {

  def main(args: Array[String]) {

    val logger = Logger("Root")
    logger.info("Testing logging")

    if (args.length == 0) {
      logger.info("You need to provide the host address as a parameter")
    }
    val host = args(0)

    val url = s"http://$host:8080/persons"
    val client = new DefaultHttpClient

    logger.info("GET")
    val get = getRestContent(url, 1000, 1000)
    logger.info(get)

    val post = new HttpPost(url)
    post.addHeader(HttpHeaders.CONTENT_TYPE,"application/json")
    val person = new Person(55, "winston", 25)
    val personAsJson = new Gson().toJson(person)
    post.setEntity(new StringEntity(personAsJson))

    logger.info("POST")
    val response = client.execute(post)
    logger.info(response.toString)

    logger.info("GET")
    val get2 = getRestContent(url, 1000, 1000)
    logger.info(get2.toString)

  }

  def getRestContent(url: String,
                     connectionTimeout: Int,
                     socketTimeout: Int): String = {
    val httpClient = buildHttpClient(connectionTimeout, socketTimeout)
    val httpResponse = httpClient.execute(new HttpGet(url))
    val entity = httpResponse.getEntity
    var content = ""
    if (entity != null) {
      val inputStream = entity.getContent
      content = scala.io.Source.fromInputStream(inputStream).getLines.mkString
      inputStream.close
    }
    httpClient.getConnectionManager.shutdown
    content
  }

  private def buildHttpClient(connectionTimeout: Int, socketTimeout: Int):

  DefaultHttpClient = {
    val httpClient = new DefaultHttpClient
    val httpParams = httpClient.getParams
    HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout)
    HttpConnectionParams.setSoTimeout(httpParams, socketTimeout)
    httpClient.setParams(httpParams)
    httpClient
  }

}