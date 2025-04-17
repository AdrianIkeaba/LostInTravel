package com.ghostdev.location.lostintravel.data.models

import com.ghostdev.location.RecommendedPlacesQuery
import kotlinx.serialization.Serializable

@Serializable
data class SerializablePlace(
    val leadingDestinationTitle: String,
    val subDestinationTitle: String?,
    val price: String?,
    val imageUrl: String?
)

fun RecommendedPlacesQuery.RecommendedPlace.toSerializable(): SerializablePlace {
    return SerializablePlace(
        leadingDestinationTitle = this.leadingDestinationTitle ?: "",
        subDestinationTitle = this.subDestinationTitle,
        price = this.price.toString(),
        imageUrl = this.imageUrl
    )
}

fun SerializablePlace.toApolloModel(): RecommendedPlacesQuery.RecommendedPlace {
    return RecommendedPlacesQuery.RecommendedPlace(
        leadingDestinationTitle = this.leadingDestinationTitle,
        subDestinationTitle = this.subDestinationTitle,
        price = this.price?.toDouble(),
        imageUrl = this.imageUrl
    )
}

