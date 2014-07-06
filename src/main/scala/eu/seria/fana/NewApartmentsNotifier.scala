package eu.seria.fana

import akka.actor.{ActorLogging, Props, Actor}
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

case class SendNewApartmentsNotification(apartments: List[Apartment])

object NewApartmentsNotifier {

  def props(config: FanaConfig) = Props(new NewApartmentsNotifier(config))

}

class NewApartmentsNotifier(config: FanaConfig) extends Actor with ActorLogging {

  lazy val recipients = config.notificationsRecipients

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
      for {
        recipient <- recipients
        apartment <- apartments
        if (config.notificationsEnabled)
      } yield {
        log.info(s"send notification: $recipient ${apartment.sha1}")
        twitter.sendDirectMessage(recipient, apartment.sha1)
      }
    }
  }
}