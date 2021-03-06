package ma.mohamed.codingchallenge.data

import ma.mohamed.codingchallenge.data.model.PagedResponseModel
import ma.mohamed.codingchallenge.data.model.RepoModel
import ma.mohamed.codingchallenge.data.model.UserModel
import kotlin.random.Random

object DataTestUtils {

    val REPOS_RESPONSE_MODEL = PagedResponseModel(
        (1..10).map {
            RepoModel(
                it.toLong(),
                "repository $it",
                "description of repo $it",
                "url $it",
                UserModel("user $it", "url", "avatar"),
                it * Random.nextInt(100),
                "Kotlin"
            )
        }
    )

    val USER_MODEL = UserModel("user", "url", "avatar")

    val REPO_MODEL = RepoModel(
        1,
        "repository",
        "description of repo",
        "url",
        USER_MODEL,
        100,
        "Kotlin"
    )
}