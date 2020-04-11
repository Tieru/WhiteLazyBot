package entity.user

case class UserEntity
(
  id: Int,
  isRegistered: Boolean,
  isBanned: Boolean,
  username: String,
  isAdmin: Boolean,
)
