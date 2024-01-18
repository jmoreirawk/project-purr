package pro.moreira.projectpurr.common.factories

import com.github.javafaker.Faker
import pro.moreira.projectpurr.data.entities.Image

class ImageFactory {

    private val faker = Faker()

    fun build() = Image(
        id = faker.idNumber().valid(),
        url = faker.internet().url(),
    )

    fun buildList(size: Int = 3) = List(size) { build() }
}