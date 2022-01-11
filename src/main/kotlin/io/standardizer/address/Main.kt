import io.standardizer.address.AddressStandardizerFactory
import io.standardizer.address.model.ModelFactory
import java.io.File

fun main(args: Array<String>) {
    val r = AddressStandardizerFactory.createStandardizer("us").standardize("1234 W Main Street Apartment 101 San Fransisco CA 12345-1234")
    println(r);
    println("${r["primaryAddressNumber"]} ${r["preDirection"]} ${r["streetName"]} ${r["suffix"]} ${r["postDirection"]} ${r["secondaryAddressIdentifier"]} ${r["secondaryAddress"]} ${r["city"]} ${r["state"]} ${r["zipcode"]}".replace("\\s+".toRegex(), " "))
}