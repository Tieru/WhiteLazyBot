package service.auth

import com.bot4s.telegram.models.User

object UserExtensions {

  implicit class UserExt(val user: User) {
    def makeUsername(): String = {
      user.username match {
        case Some(username) => "@" + username
        case None => user.firstName + " " + user.lastName.getOrElse("")
      }
    }
  }

}
