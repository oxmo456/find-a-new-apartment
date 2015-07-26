package eu.seria

import java.net.NetworkInterface
import scala.collection.JavaConversions._

package object utils {

  def noop[A, B]: PartialFunction[A, B] = new PartialFunction[A, B] {

    override def isDefinedAt(x: A): Boolean = false

    override def apply(v1: A): B = throw new IllegalStateException()
  }

  lazy val sha1MessageDigest = java.security.MessageDigest.getInstance("SHA-1")

  def sha1(args: CharSequence*): String = {
    sha1MessageDigest.digest(args.mkString.getBytes("UTF-8")).map("%02x".format(_)).mkString
  }

  lazy val hostname: String = System.getenv("HOSTNAME")

  lazy val hostIPs: List[String] = NetworkInterface.getNetworkInterfaces().toList.map(networkInterface => {
    s"${networkInterface.getName}:${networkInterface.getInetAddresses().map(_.getHostAddress).mkString(",")}"
  })

}
