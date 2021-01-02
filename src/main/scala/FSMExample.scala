
import akka.actor.{ActorSystem, FSM, Props, Stash}

object UserStorageFSM {

  // define user data
  case class User(name: String, email: String)

  // define operations
  case object Connect
  case object Disconnect

  trait DBOperation
  object DBOperation {
    case object Create extends DBOperation
    case object Update extends DBOperation
    case object Read extends DBOperation
    case object Delete extends DBOperation
  }
  case class Operation(op: DBOperation, user: User)

  //define actor states
  sealed trait State
  case object Connected extends State
  case object Disconnected extends State

  // Define initial data for the state
  sealed trait Data
  case object EmptyData extends Data
}

class UserStorageFSM extends FSM[UserStorageFSM.State, UserStorageFSM.Data] with Stash{

  import UserStorageFSM._

  // define start with
  startWith(Disconnected, EmptyData)

  // 2. define states
  when(Disconnected){
    case Event(Connect, _) =>
      println("UserStorage Connected to DB")
      unstashAll()
      goto(Connected) using(EmptyData)
    case Event(_, _) =>
      stash()
      stay using(EmptyData)
  }

  when(Connected) {
    case Event(Disconnect, _) =>
      println("UserStorage disconnected from DB")
      goto(Disconnected) using EmptyData

    case Event(Operation(op, user), _) =>
      println(s"UserStorage receive ${op} operation to do in user: ${user}")
      stay using EmptyData
  }

  // initialise
  initialize()
}

object FSMExample extends App {
  import UserStorageFSM._

  val system = ActorSystem("Hotswap-FSM")

  val userStorage = system.actorOf(Props[UserStorageFSM], "userStorage-fsm")

  userStorage ! Connect

  userStorage ! Operation(DBOperation.Create, User("nila", "nila@nila.com"))

  userStorage ! Disconnect

  Thread.sleep(100)

  system.terminate()
}
