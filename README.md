
# address-standardizer
An address standardizer made in Kotlin based on USPS Publication 28

## What it does not do
- Appending ZIP+4
- Address Validation
- DPV Verification
- Address (Typo) Correction

## How to use

**Make sure to use a valid address, otherwise it may not work!**
### In Kotlin

```kotlin  
import io.standardizer.address.AddressStandardizerFactory  
  
val adr = AddressStandardizerFactory.createStandardizer("us").standardize("your address here")  
```  
or
```kotlin  
import io.standardizer.address.AddressStandardizerFactory  
  
val standardizer = AddressStandardizerFactory.createStandardizer("us")  
val adr = standardizer.standardize("your address here")  
```  
### In Java
**Use Standalone Jar (includes Kotlin Runtime), otherwise you will have to manually include the kotlin runtime in the classpath!**
```java  
package packageName;  
  
import io.standardizer.address.AddressStandardizer;  
import io.standardizer.address.AddressStandardizerFactory;  
  
import java.util.Map;  
  
public class Main {  
 public static void main(String[] args) { AddressStandardizer adr = AddressStandardizerFactory.createStandardizer("us"); Map<String, String> res = adr.standardize("your address here"); System.out.println(res); }}  
```  

## TODO
- Support for bypass and frontage roads
- Refactoring to make more Object-Oriented
- Proper exception handling