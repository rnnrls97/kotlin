package com.renanfran.transactionapp.android.base

sealed class NavigationEvent {
    object NavigateBack : NavigationEvent()
}

sealed class AddTransactionNavigationEvent : NavigationEvent() {
    object MenuOpenedClicked : AddTransactionNavigationEvent()
}

sealed class HomeNavigationEvent : NavigationEvent() {
    object NavigateToAddTransaction : HomeNavigationEvent()
    object NavigateToAddIncome : HomeNavigationEvent()
    object NavigateToSeeAll : HomeNavigationEvent()
}