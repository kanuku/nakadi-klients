package component

sealed trait MenuType

case object Link extends MenuType

case object Route extends MenuType

case class Menu(
                 `type`: MenuType = Route,
                 text: String = "",
                 path: String = "")

