package eu.seria.fana

import akka.actor.{Props, ActorSystem}

class FindANewApartment {

  val system = ActorSystem(getClass.getName.replace('.', '-'))

  val engine = system.actorOf(Props[FindANewApartmentEngine], "fana-engine")


  def userInput: Unit = {

    Console.print("> ")
    Console.readLine() match {
      case "start" => {
        engine ! Start()
        userInput
      }
      case "stop" => {
        engine ! Stop()
        userInput
      }
      case "exit" => system.shutdown()
      case command => {
        Console.println(s"unknown command $command")
        userInput
      }
    }

  }


  userInput


}
