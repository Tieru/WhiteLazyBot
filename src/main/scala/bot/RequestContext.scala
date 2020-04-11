package bot

import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.methods.Request

import scala.concurrent.{ExecutionContext, Future}

trait RequestContext {

  def request[R: Manifest](request: Request[R]): Future[R]

  def request[R: Manifest, A](args: Seq[A], requestFactory: A => Request[R]): Future[Seq[R]]

}

class BotRequestContext(private val handler: RequestHandler)(implicit ec: ExecutionContext) extends RequestContext {

  override def request[R: Manifest](request: Request[R]): Future[R] = {
    handler(request)
  }

  override def request[R: Manifest, A](args: Seq[A], requestFactory: A => Request[R]): Future[Seq[R]] = {
    Future.traverse(args)(arg => handler(requestFactory(arg)))
  }
}
