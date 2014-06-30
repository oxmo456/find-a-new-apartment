package eu.seria.fana

import akka.actor.ActorSystem

object FindANewApartment {

  object Command {

    val Start = "start"
    val Stop = "stop"
    val Exit = "exit"


  }

}

class FindANewApartment(config: Config) {

  import FindANewApartment._

  val system = ActorSystem(getClass.getName.replace('.', '-'))

  val engine = system.actorOf(FindANewApartmentEngine.props(config))

  def handleUserInput: Unit = {
    Console.print("? ")
    Console.readLine() match {
      case Command.Start => {
        engine ! Start()
        handleUserInput
      }
      case Command.Stop => {
        engine ! Stop()
        handleUserInput
      }
      case Command.Exit => system.shutdown()
      case command => {
        Console.println(s"unknown command $command")
        handleUserInput
      }
    }
  }

  handleUserInput

}
