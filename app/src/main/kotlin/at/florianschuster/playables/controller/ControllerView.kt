package at.florianschuster.playables.controller

interface ControllerView<C : Controller<*, *, *>> {
    val controller: C?
}