package com.skysam.hchirinos.go2shop.listsShared

import com.skysam.hchirinos.go2shop.common.models.ListShared

/**
 * Created by Hector Chirinos (Home) on 3/11/2021.
 */
interface OnClick {
    fun addUsers(list: ListShared)
    fun viewList(list: ListShared)
    fun deleteList(list: ListShared)
}