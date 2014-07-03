package eu.seria.fana

import akka.actor.{Props, Actor}
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

case class SendNewApartmentsNotification(apartments: List[Apartment])

object NewApartmentsNotifier {

  def props(config: FanaConfig) = Props(new NewApartmentsNotifier(config))

}

class NewApartmentsNotifier(config: FanaConfig) extends Actor {

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
      apartments.foreach(apartment => {
        twitter.sendDirectMessage("@Aunimi", apartment.sha1)
      })
    }
  }
}
