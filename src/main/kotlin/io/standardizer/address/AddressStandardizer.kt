package io.standardizer.address

interface AddressStandardizer {

    /**
     * Standardizes an address
     *
     * Infers the city and street
     *
     * @param adr The full address
     * @return A HashMap with `primaryAddressNumber`, `preDirection`, `streetName`, `suffix`, `postDirection`, `secondaryAddressIdentifier`, `secondaryAddress`, `city`, `state`, and `postalCode` all in respective order
     */
    fun standardize(adr: String) : Map<String, String>
    
    /**
     * Standardizes an address without heavy inferring
     *
     * More light work done than regular standardization
     *
     * @param adr Address without city, state, and postal code
     * @param city City
     * @param state State
     * @param postalCode Postal Code
     * @return A HashMap with `primaryAddressNumber`, `preDirection`, `streetName`, `suffix`, `postDirection`, `secondaryAddressIdentifier`, `secondaryAddress`, `city`, `state`, and `postalCode` all in respective order
     */
    fun standardize(adr: String, city: String, state: String, postalCode: String) : Map<String, String>


}