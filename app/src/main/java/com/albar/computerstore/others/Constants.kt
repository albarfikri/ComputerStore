package com.albar.computerstore.others

import androidx.annotation.StringRes
import com.albar.computerstore.R

object Constants {

    const val REQUEST_CODE_LOCATION_PERMISSION = 0

    // for onBoarding screen Adapter
    const val NUM_PAGES = 3

    // Detail viewpager Tab Titles
    @StringRes
    val TAB_TITLES = intArrayOf(
        R.string.detail_tab_text_1,
        R.string.detail_tab_text_2
    )

    // Bundle key for detail
    const val KEY = "type"

    // Parcelable key
    const val PARCELABLE_KEY = "computerStore"

    // Data store key
    const val ON_BOARDING_DATA_STORE_KEY = "OnBoardingStatusWithDataStore"
    const val PREFERENCES_NAME = "onBoardingStatus"

    //App Bar Name
    const val COMPUTER_STORE_LIST = "Computer Store List"
    const val COMPUTER_STORE_MAPS = "Computer Store Maps"
    const val COMPUTER_STORE_NEAREST = "Computer Store Nearest"

    //Coordinate Request and Bundle Key
    const val REQUEST_KEY = "request_key"
    const val BUNDLE_KEY = "bundle_key"

    // Firebase
    const val FIRESTORE_TABLE = "computerstore" // firestore table name
    const val USERNAME_FIELD = "username"
    const val PASSWORD_FIELD = "password"

    // Handler to head up to Admin and Member
    const val DELAY_TO_MOVE_ANOTHER_ACTIVITY = 500L
    const val DELAY_TO_MOVE_ANOTHER_FRAGMENT = 2000L

    // Local Sharedpref
    const val LOCAL_SHARED_PREF = "local_shared_pref"
    const val COMPUTER_STORE_SESSION = "computer_store_session"
    const val VERIFIED_NUMBERS = "verified_numbers"
    const val UNVERIFIED_NUMBERS = "unverified_numbers"
}