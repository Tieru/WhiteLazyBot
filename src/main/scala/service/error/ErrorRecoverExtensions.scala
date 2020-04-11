package service.error

import service.error.ErrorCode.ErrorCode
import slogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object ErrorRecoverExtensions extends LazyLogging {

  implicit class FutureRecover[T](val future: Future[T])(implicit ec: ExecutionContext) {

    def recoverWithDefaultError(): Future[T] = {
      future.recoverWith {
        case e: AppException => Future.failed(e)
        case e: Throwable => Future.failed(AppException(ErrorInfo(ErrorCode.Unknown)))
      }
    }

    def recoverFromAppError(pf: PartialFunction[ErrorCode, Future[T]]): Future[T] =
      future.transformWith {
        case Failure(t) =>
          logger.error(s"Default app error handling for error $t")
          pf.applyOrElse(t.asInstanceOf[AppException].data.errorCode, (_: ErrorCode) => future)
        case Success(_) => future
      }
  }

}
