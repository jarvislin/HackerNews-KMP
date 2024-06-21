package domain.models

sealed class Category(val path: String, val index: Int, val title: String) {
    companion object {
        fun from(spinnerIndex: Int): Category = when (spinnerIndex) {
            0 -> TopStories
            1 -> NewStories
            2 -> BestStories
            3 -> AskStories
            4 -> ShowStories
            5 -> JobStories
            else -> throw IllegalArgumentException("Invalid index")
        }

        fun getAll(): List<Category> = listOf(
            TopStories, NewStories, BestStories, AskStories, ShowStories, JobStories
        )
    }
}

data object TopStories : Category("topstories.json", 0, "Top")
data object NewStories : Category("newstories.json", 1, "New")
data object BestStories : Category("beststories.json", 2, "Best")
data object AskStories : Category("askstories.json", 3, "Ask")
data object ShowStories : Category("showstories.json", 4, "Show")
data object JobStories : Category("jobstories.json", 5, "Job")
