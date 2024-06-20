package domain.models

sealed class Category(val path: String)
data object TopStories : Category("topstories.json")
data object NewStories : Category("newstories.json")
data object BestStories : Category("beststories.json")
data object AskStories : Category("askstories.json")
data object ShowStories : Category("showstories.json")
data object JobStories : Category("jobstories.json")
