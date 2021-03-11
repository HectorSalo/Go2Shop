package com.skysam.hchirinos.go2shop.homeModule.presenter

import com.skysam.hchirinos.go2shop.homeModule.interactor.InicioInteractor
import com.skysam.hchirinos.go2shop.homeModule.interactor.InicioInteractorClass

/**
 * Created by Hector Chirinos on 10/03/2021.
 */
class InicioPresenterClass: InicioPresenter {
    private val inicioInteractor: InicioInteractor = InicioInteractorClass()
    override fun getValueWeb() {
        inicioInteractor.getValueWeb()
    }

    override fun getDataFromFirestore() {
        inicioInteractor.getDataFromFirestore()
    }
}