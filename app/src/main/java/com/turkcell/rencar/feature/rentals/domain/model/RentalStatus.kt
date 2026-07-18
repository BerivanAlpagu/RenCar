package com.turkcell.rencar.feature.rentals.domain.model

enum class RentalStatus {
    PREPARING,
    ACTIVE,
    COMPLETED,
    CANCELLED
}

enum class RentalPlan {
    PER_MINUTE,
    HOURLY,
    DAILY
}

enum class RentalPaymentStatus {
    UNPAID,
    PAID
}

enum class RentalPaymentMethod {
    WALLET,
    CARD,
    IYZICO
}

enum class RentalPhotoSide {
    FRONT,
    BACK,
    LEFT,
    RIGHT
}
