package io.standardizer.address

import io.standardizer.address.us.USAddressStandardizer

class AddressStandardizerFactory {
    companion object {
        @JvmStatic
        fun createStandardizer(countryCode: String) : AddressStandardizer {
            return when (countryCode.lowercase()) {
                "us" -> USAddressStandardizer.getInstance()
                else -> USAddressStandardizer.getInstance()
            }
        }
    }
}