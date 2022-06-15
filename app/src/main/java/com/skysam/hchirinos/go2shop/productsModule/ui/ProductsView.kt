package com.skysam.hchirinos.go2shop.productsModule.ui

import com.skysam.hchirinos.go2shop.common.models.Product

interface ProductsView {
    fun resultGetProducts(products: MutableList<Product>)
}
