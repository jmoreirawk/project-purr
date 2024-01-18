package pro.moreira.projectpurr.common

import com.github.javafaker.Faker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
open class BaseTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    protected val faker = Faker()
}