package sk.marcel.rtvt

object Constants {
    const val precolored = "precolored"
    const val displayColor = "display_color"
    const val needColor = "need_color"
    const val pictureNumber = "picture-number"
    const val team = "team"
    const val datetime = "datetime"
    const val alpha = "#22"
    val colorsMap = mapOf(
        Pair("red", "#ff0000"),
        Pair("green", "#00ff00"),
        Pair("blue", "#0000ff"),
        Pair("yellow", "#f6ff00"),
        Pair("cyan", "#00ddff"),
        Pair("magenta", "#ff00a2"),
        Pair("orange", "#ff7b00"),
        Pair("white", "#ffffff"),
        Pair("brown", "#853100"),
        Pair("light green", "#a9ff91"),
        Pair("dark green", "#0e4200"),
        Pair("azure", "#628bc4"),
        Pair("purple", "#8d0aff")
    )

    val colorsMixing = mapOf(
        Pair(arrayListOf("red").sorted(), "red"),
        Pair(arrayListOf("red", "red").sorted(), "red"),
        Pair(arrayListOf("red", "red", "red").sorted(), "red"),

        Pair(arrayListOf("green").sorted(), "green"),
        Pair(arrayListOf("green", "green").sorted(), "green"),
        Pair(arrayListOf("green", "green", "green").sorted(), "green"),

        Pair(arrayListOf("blue").sorted(), "blue"),
        Pair(arrayListOf("blue", "blue").sorted(), "blue"),
        Pair(arrayListOf("blue", "blue", "blue").sorted(), "blue"),

        Pair(arrayListOf("red", "green").sorted(), "yellow"),
        Pair(arrayListOf("green", "blue").sorted(), "cyan"),
        Pair(arrayListOf("blue", "red").sorted(), "magenta"),

        Pair(arrayListOf("red", "red", "green").sorted(), "orange"),
        Pair(arrayListOf("red", "green", "blue").sorted(), "white"),
        Pair(arrayListOf("red", "red", "blue").sorted(), "brown"),

        Pair(arrayListOf("green", "red", "green").sorted(), "light green"),
        Pair(arrayListOf("green", "green", "blue").sorted(), "dark green"),
        Pair(arrayListOf("blue", "green", "blue").sorted(), "azure"),

        Pair(arrayListOf("blue", "red", "blue").sorted(), "purple")

    )
}