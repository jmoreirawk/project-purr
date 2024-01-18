package pro.moreira.projectpurr.common.factories

import com.github.javafaker.Faker
import pro.moreira.projectpurr.data.entities.Breed

class BreedFactory {

    private val faker = Faker()
    private val imageFactory = ImageFactory()

    fun build(
        withImage: Boolean = true,
        isFavorite: Boolean = faker.bool().bool(),
    ) = Breed(
        id = faker.idNumber().valid(),
        name = faker.cat().name(),
        origin = faker.cat().breed(),
        temperament = faker.cat().registry(),
        description = faker.cat().registry(),
        image = if (withImage) imageFactory.build() else null,
        lifeSpan = faker.cat().registry(),
        isFavorite = isFavorite,
    )

    fun buildList(
        size: Int = 3,
        withImage: Boolean = true,
        isFavorite: Boolean = faker.bool().bool(),
    ) = List(size) { build(withImage, isFavorite) }
}