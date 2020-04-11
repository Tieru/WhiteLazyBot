package injection

import com.google.inject.Injector
import javax.inject.Inject
import net.codingwell.scalaguice.InjectorExtensions._

import scala.reflect.runtime.universe._

class HandlerProvider @Inject()(private val injector: Injector) {

  def provide[T: TypeTag](): T = injector.instance[T]

}
