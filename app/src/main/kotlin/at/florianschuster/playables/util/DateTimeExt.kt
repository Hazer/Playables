package at.florianschuster.playables.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

val LocalDate.displayString: String get() = dateFormatter.format(this)