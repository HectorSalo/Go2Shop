package com.skysam.hchirinos.go2shop.common.classView

import com.skysam.hchirinos.go2shop.database.room.entities.Product

/**
 * Created by Hector Chirinos (Home) on 9/3/2021.
 */
interface ProductSaveFromList {
    fun productSave(product: Product)
}