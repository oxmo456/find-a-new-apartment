package eu.seria.fana

import akka.actor.{Props, Actor}
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import akka.event.Logging

case class SendNewApartmentsNotification(apartments: List[Apartment])

object NewApartmentsNotifier {

  def props(config: FanaConfig) = Props(new NewApartmentsNotifier(config))

}

class NewApartmentsNotifier(config: FanaConfig) extends Actor {

  val log = Logging(context.system, this)

  def twitterConf = new ConfigurationBuilder()
    .setDebugEnabled(false)
    .setOAuthConsumerKey(config.twitter.consumerKey)
    .setOAuthConsumerSecret(config.twitter.consumerSecret)
    .setOAuthAccessToken(config.twitter.accessToken)
    .setOAuthAccessTokenSecret(config.twitter.accessTokenSecret)
    .build()

  lazy val twitter = new TwitterFactory(twitterConf).getInstance()

  override def receive: Receive = {


    case SendNewApartmentsNotification(apartments) => {

      log.info(s"SendNewApartmentsNotification(${apartments.length})")

      apartments.foreach(apartment => {
        //twitter.sendDirectMessage("@Aunimi", apartment.sha1)
      })
    }
  }
}