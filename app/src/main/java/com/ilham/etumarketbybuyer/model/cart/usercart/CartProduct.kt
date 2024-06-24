package com.ilham.etumarketbybuyer.model.cart.usercart

data class CartProduct(
    val product: Product,
    val latitude: Double,
    val longitude: Double,
    val shippingCost : Int
)
