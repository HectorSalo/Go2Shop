package com.skysam.hchirinos.go2shop.homeModule.presenter

import com.skysam.hchirinos.go2shop.homeModule.interactor.InicioInteractor
import com.skysam.hchirinos.go2shop.homeModule.interactor.InicioInteractorClass
import com.skysam.hchirinos.go2shop.homeModule.ui.InicioView

/**
 * Created by Hector Chirinos on 10/03/2021.
 */
class InicioPresenterClass(private val inicioView: InicioView): InicioPresenter {
    private val inicioInteractor: InicioInteractor = InicioInteractorClass(this)
    override fun getValueWeb() {
        inicioInteractor.getValueWeb()
    }

    override fun getProductsFromFirestore() {
        inicioInteractor.getProductsFromFirestore()
    }

    override fun resultSync(statusOk: Boolean) {
        inicioView.resultSync(statusOk)
    }
}