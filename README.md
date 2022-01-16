
# Address Standardizer
<p align="center">
<a href="https://github.com/arh1th/address-standardizer/blob/main/LICENSE" target="blank">
<img src="https://img.shields.io/github/license/arh1th/address-standardizer?style=for-the-badge" alt="license" />
</a>
<a href="https://github.com/arh1th/address-standardizer/fork" target="blank">
<img src="https://img.shields.io/github/forks/arh1th/address-standardizer?style=for-the-badge" alt="forks"/>
</a>
<a href="https://github.com/arh1th/address-standardizer/stargazers" target="blank">
<img src="https://img.shields.io/github/stars/arh1th/address-standardizer?style=for-the-badge" alt="stars"/>
</a>
<a href="https://github.com/arh1th/address-standardizer/issues" target="blank">
<img src="https://img.shields.io/github/issues/arh1th/address-standardizer?style=for-the-badge" alt="issues"/>
</a>
<a href="https://github.com/arh1th/address-standardizer/pulls" target="blank">
<img src="https://img.shields.io/github/issues-pr/arh1th/address-standardizer?style=for-the-badge" alt="pull-requests"/>
</a>
<img src="https://img.shields.io/tokei/lines/github/arh1th/address-standardizer?style=for-the-badge"/>

</p>
An address standardizer made in Kotlin based on USPS Publication 28

## What it does do
- Parse your address into parts
- Replace some parts (eg. direction) with its standard.


## What it does not do
- Appending ZIP+4
- Address Validation
- DPV Verification
- Address (Typo) Correction

## Download
- <a href="https://github.com/arh1th/address-standardizer/releases/tag/1.0-SNAPSHOT"> Download link </a>

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
    public static void main(String[] args) { 
        AddressStandardizer adr = AddressStandardizerFactory.createStandardizer("us"); 
        Map<String, String> res = adr.standardize("your address here"); 
        System.out.println(res); 
    }
}  
```  

## TODO
- Support for bypass and frontage roads
- Refactoring to make more Object-Oriented
- Proper exception handling