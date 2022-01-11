package io.standardizer.address.us

import io.standardizer.address.AddressStandardizer
import io.standardizer.address.SingletonHolder
import io.standardizer.address.model.Model
import io.standardizer.address.model.ModelFactory
import io.standardizer.address.util.Util

class USAddressStandardizer private constructor() : AddressStandardizer {
    private var model: Model = ModelFactory.load("us");


    override fun standardize(adr: String): Map<String, String> {
        var (zipcode, state, city, secondaryAddressIdentifier, secondaryAddress) = listOf("", "", "", "", "")

        var newAdr = adr.replace(".", "")
        newAdr = newAdr.uppercase()
        var splitted = newAdr.split(' ')

        var data = findZipCode(splitted);

        if (data["returned"] != "") {
            zipcode = data["returned"].toString();
            splitted = Util.removeAndReturn(data["start"] as Int, data["end"] as Int, splitted) as List<String>;
        }

        data = findState(splitted, model);

        if (data["returned"] != "") {
            state = data["returned"].toString();
            splitted = Util.removeAndReturn(data["start"] as Int, data["end"] as Int, splitted) as List<String>;
        }

        data = findCity(splitted, model);

        if (data["returned"] != "") {
            city = data["returned"].toString();
            splitted = Util.removeAndReturn(data["start"] as Int, data["end"] as Int, splitted) as List<String>;
        }


        var ndata = findSecondaryUnit(splitted, model);

        if (ndata["returned"]?.get(0) != "") {
            if (ndata["returned"]?.size == 2) {
                secondaryAddressIdentifier = ndata["returned"]?.get(0).toString();
                secondaryAddress = ndata["returned"]?.get(1).toString();
            }
            else {
                secondaryAddressIdentifier = ndata["returned"]?.get(0).toString();
            }
            splitted = Util.removeAndReturn(ndata["start"]?.get(0) as Int, ndata["end"]?.get(0) as Int, splitted) as List<String>;
        }

        var (primaryAddressNumber, preDirection, streetName, suffix, postDirection) = findStreet(
            splitted,
            model
        );

        if (preDirection != "") {
            preDirection = model.directions[preDirection].toString()
        }

        if (postDirection != "") {
            postDirection = model.directions[postDirection].toString()
        }

        return hashMapOf(
            "postalCode" to zipcode,
            "state" to state,
            "city" to city,
            "primaryAddressNumber" to primaryAddressNumber,
            "secondaryAddressIdentifier" to secondaryAddressIdentifier,
            "secondaryAddress" to secondaryAddress,
            "preDirection" to preDirection,
            "streetName" to streetName,
            "suffix" to suffix,
            "postDirection" to postDirection
        )
    }

    override fun standardize(adr: String, city: String, state: String, postalCode: String): Map<String, String> {
        var newAdr = adr.replace(".", "")
        newAdr = newAdr.uppercase()
        var splitted = newAdr.split(' ')

        var ndata = findSecondaryUnit(splitted, model);

        var (secondaryAddressIdentifier, secondaryAddress) = listOf("","")

        if (ndata["returned"]?.get(0) != "") {
            if (ndata["returned"]?.size == 2) {
                secondaryAddressIdentifier = ndata["returned"]?.get(0).toString();
                secondaryAddress = ndata["returned"]?.get(1).toString();
            }
            else {
                secondaryAddressIdentifier = ndata["returned"]?.get(0).toString();
            }
            splitted = Util.removeAndReturn(ndata["start"]?.get(0) as Int, ndata["end"]?.get(0) as Int, splitted) as List<String>;
        }

        var (primaryAddressNumber, preDirection, streetName, suffix, postDirection) = findStreet(
            splitted,
            model
        );

        if (preDirection != "") {
            preDirection = model.directions[preDirection].toString()
        }

        if (postDirection != "") {
            postDirection = model.directions[postDirection].toString()
        }

        return hashMapOf(
            "postalCode" to findZipCode(listOf(postalCode.uppercase()))["returned"].toString(),
            "state" to findState(listOf(state.uppercase()), model)["returned"].toString(),
            "city" to city.uppercase(),
            "primaryAddressNumber" to primaryAddressNumber,
            "secondaryAddressIdentifier" to secondaryAddressIdentifier,
            "secondaryAddress" to secondaryAddress,
            "preDirection" to preDirection,
            "streetName" to streetName,
            "suffix" to suffix,
            "postDirection" to postDirection
        )
    }

    private fun findZipCode(splitted : List<String>) : HashMap<String, Any?> {
        var reverse = splitted.reversed()
        var section = reverse[0]

        if (section.length == 4 && reverse[1].length == 5) {
            return hashMapOf(
                "returned" to reverse[1] + '-' + section,
                "start" to (splitted.size - 2),
                "end" to (splitted.size)
            )
        } else if (section.length == 10 || section.length == 5 || section.length == 4) {
            return hashMapOf(
                "returned" to section,
                "start" to (splitted.size - 1),
                "end" to (splitted.size)
            )
        } else if (section.length == 9) {
            return hashMapOf(
                "returned" to section.substring(0,5) + '-' + section.substring(5, 9),
                "start" to (splitted.size - 1),
                "end" to (splitted.size)
            )
        }

        return hashMapOf(
            "returned" to "",
            "start" to -1,
            "end" to -1
        )
    }

    private fun findState(splitted: List<String>, model: Model) : HashMap<String, Any?> {
        var index = splitted.size;
        var string = "";

        for (i in index - 1 downTo 0 step 1) {
            string = splitted[i] + ' ' + string;
            string = string.trim();
            if (model.states.contains(string)) {
                return hashMapOf(
                    "returned" to model.states.get(string),
                    "start" to i,
                    "end" to index
                )
            }
        }

        return hashMapOf(
            "returned" to "",
            "start" to -1,
            "end" to -1
        )
    }

    private fun findCity(splitted: List<String>, model: Model) : HashMap<String, Any?> {
        if (splitted.size < 4) {
            if (splitted.size < 3 && !model.street_abbreviations.contains(splitted[splitted.size - 1])) {
                return hashMapOf(
                    "returned" to "",
                    "start" to -1,
                    "end" to -1
                )
            }
            else {
                return hashMapOf(
                    "returned" to splitted[splitted.size - 1],
                    "start" to splitted.size - 1,
                    "end" to splitted.size
                )
            }
        }

        var reverse : List<String> = splitted.reversed();
        var cutInd = -1;

        for ((i, e) in reverse.withIndex()) {
            if (splitted.size - 1 - i <= 3) {
                break
            }

            if (model.secondary_units.contains(e)) {
                for (j in i - 1 downTo 0 step 1) {
                    if (!reverse[j].contains('#') && !Util.containsNumber(reverse[j]) && reverse[j].length > 1) {
                        cutInd = j
                        break;
                    }
                }
            }
        }

        if (cutInd != -1) {
            return hashMapOf(
                "returned" to Util.removeAndReturn(0, splitted.size - 1 - cutInd, splitted).joinToString(" "),
                "start" to splitted.size - 1 - cutInd,
                "end" to splitted.size
            )
        }

        var foundStreetAbr = false;
        var foundDir = false;
        var conDex = 0
        var tick = 0
        var csl : Map<Any?, Any?> = Util.findCSL(splitted, model)

        if (csl["found"] as Boolean) {
            if ((csl["isInterstateWithI"] as Boolean) || (csl["isIh"] as Boolean)) {
                cutInd = csl["end"] as Int + 1;
            }
            else {
                cutInd = (csl["end"] as Int) + 2;
            }
        }

        for ((i, e) in splitted.withIndex()) {
            if (i == 0) {
                continue
            }

            if (conDex > 0) {
                conDex -= 1;
                continue;
            }

            if (model.street_abbreviations.contains(e) && !foundStreetAbr) {
                if (i + 1 < splitted.size && model.street_abbreviations.contains(splitted[i + 1]) && (i + 2 != splitted.size - 1 || splitted.size == 4)) {
                    if (cutInd != -1) {
                        foundStreetAbr = true;
                        conDex = 1;
                        tick = 1;
                        continue;
                    }
                    cutInd = i + 2;
                    foundStreetAbr = true;
                    conDex = 1;
                    tick = 1;
                    continue;
                }
                else {
                    if (cutInd != -1) {
                        tick = 1;
                        foundStreetAbr = true;
                        continue;
                    }
                    tick = 1;
                    foundStreetAbr = true;
                    cutInd = i + 1;
                    continue;
                }
            }

            if (model.directions.contains(e)) {
                if (foundStreetAbr) {
                    if (foundDir) {
                        break;
                    }
                    else {
                        if (model.directions.contains(splitted[i + 1])) {
                            if (Util.isOppositeDir(model.directions[e].toString(), model.directions[splitted[i + 1]].toString())) {
                                cutInd = i;
                            }
                            else {
                                cutInd = i + 1;
                            }
                        }
                        else {
                            cutInd = i + 1
                        }
                    }
                }
                else {
                    foundDir = true;
                }
            }
            if (tick == 1) {
                break;
            }
        }

        if (cutInd != -1) {
            return hashMapOf(
                "returned" to Util.removeAndReturn(0, cutInd, splitted).joinToString(" "),
                "start" to cutInd,
                "end" to splitted.size
            )
        }
        else {
            return hashMapOf(
                "returned" to splitted.subList(3, splitted.size).toList().joinToString(" "),
                "start" to 3,
                "end" to splitted.size
            )
        }
    }

    private fun findSecondaryUnit(splitted: List<String>, model: Model) : HashMap<String, List<Any?>> {
        var reverse = splitted.reversed();
        var index = -1;
        for ((i, e) in reverse.withIndex()) {
            if (splitted.size - 1 - i <= 3) {
                break;
            }
            if (model.secondary_units.contains(e)) {
                index = i;
            }
        }

        if (index == -1) {
            var i = 0;
            for ((ind, el) in reverse.withIndex()) {
                if (el.contains("#")) {
                    if (el.length > 1) {
                        var returned = el.split("#").toMutableList();
                        returned.add(0, "#");
                        return hashMapOf(
                            "returned" to listOf(returned.joinToString(" ")),
                            "start" to listOf(splitted.size - 1 - ind),
                            "end" to listOf(splitted.size)
                        )
                    }
                    else {
                        if (ind - 1 < 0) {
                            break;
                        }
                        else {
                            return hashMapOf(
                                "returned" to listOf(reverse.subList(0,ind+1).toList().joinToString(" ")),
                                "start" to listOf(splitted.size - 1 - ind),
                                "end" to listOf(splitted.size)
                            )
                        }
                    }
                }
            }

            return hashMapOf(
                "returned" to listOf(""),
                "start" to listOf(-1),
                "end" to listOf(-1)
            )
        }
        var copied = splitted.toList().toMutableList();
        copied = (Util.removeAndReturn(0,(splitted.size - 1 - index), copied) as List<String>).toMutableList();
        for ((ind, el) in copied.withIndex()) {
            if (el.contains("#")) {
                if (el.length > 1) {
                    copied[ind] = copied[ind].replace("#", "").trim();
                }
                else {
                    copied.removeAt(ind);
                }
            }
        }
        return hashMapOf(
            "returned" to listOf(model.secondary_units[copied[0]], copied.subList(1, copied.size).joinToString(" ")),
            "start" to listOf(splitted.size - 1 - index),
            "end" to listOf(splitted.size)
        )
    }

    private fun findStreet(s: List<String>, model: Model) : List<String> {
        var splitted = s.toList().toMutableList();
        var foundDir = false;
        var foundStreetAbr = false;
        var conDex = 0;
        var tick = 0;
        var streetInd = -1
        var preDirInd = -1
        var postDirInd = -1
        var csl : Map<Any?, Any?> = Util.findCSL(splitted, model)
        if (splitted.size < 3 && !(csl["found"] as Boolean)) {
            return listOf(splitted[0], "", splitted[1], "", "");
        }

        for ((ind, el) in splitted.withIndex()) {
            if (ind == 0) continue;
            if (conDex > 0) {
                conDex -= 1;
                continue;
            }
            if (model.street_abbreviations.contains(el) and !foundStreetAbr) {
                if (ind + 1 < splitted.size && model.street_abbreviations.contains(splitted[ind + 1])) {
                    foundStreetAbr = true;
                    conDex = 1;
                    tick = 1;
                    streetInd = ind + 1;
                    continue;
                }
                else {
                    streetInd = ind;
                    tick = 1;
                    foundStreetAbr = true;
                    continue;
                }
            }
            if (model.directions.contains(el)) {
                if (foundStreetAbr) {
                    if (foundDir) {
                        postDirInd = ind;
                        break;
                    }
                    else {
                        foundDir = true;
                        postDirInd = ind;
                    }
                } else {
                    if (ind + 1 < splitted.size && model.street_abbreviations.contains(splitted[ind + 1])) {
                        if (splitted.size <= ind + 2 || !model.street_abbreviations.contains(splitted[ind + 2])) {
                            continue;
                        }
                    }
                    foundDir = true;
                    preDirInd = ind;
                }
            }

            if (tick == 1) {
                break;
            }
        }

        var (streetName, preDirection, postDirection, primaryAddressNumber, suffix) = listOf("","","","","");
        var special = false;
        primaryAddressNumber = splitted[0];
        if (csl["found"] as Boolean) {
            special = true;
            var cInd = csl["start"] as Int;
            var rInd = csl["end"] as Int;
            if (csl["isInterstateWithI"] as Boolean) {
                streetName = model.csl.get(csl["csl"] as String).toString() + " " + splitted[cInd].drop(1)
                var pd = -1;
                if (cInd + 1 < splitted.size && model.directions.contains(splitted[rInd + 2])) {
                    postDirection = splitted[cInd + 1]
                    splitted.removeAt(cInd + 1)
                    postDirInd = -1;
                }
                if (cInd - 1 > 0 && model.directions.contains(splitted[cInd - 1])) {
                    pd = cInd - 1;
                    preDirInd = -1
                    preDirection = splitted[cInd - 1]
                }
                splitted.removeAt(cInd)
                if (pd != -1) {
                    splitted.removeAt(pd);
                }
            } else if (csl["isIh"] as Boolean) {
                streetName = model.csl.get(csl["csl"] as String).toString() + " " + splitted[cInd].drop(2)
                var pd = -1;
                if (cInd + 1 < splitted.size && model.directions.contains(splitted[rInd + 2])) {
                    postDirection = splitted[cInd + 1]
                    splitted.removeAt(cInd + 1)
                    postDirInd = -1;
                }
                if (cInd - 1 > 0 && model.directions.contains(splitted[cInd - 1])) {
                    pd = cInd - 1;
                    preDirInd = -1
                    preDirection = splitted[cInd - 1]
                }
                splitted.removeAt(cInd)
                if (pd != -1) {
                    splitted.removeAt(pd);
                }
            } else {
                streetName = model.csl.get(csl["csl"] as String).toString() + " " + splitted[rInd + 1];
                var pd = -1;
                if (rInd + 2 < splitted.size && model.directions.contains(splitted[rInd + 2])) {
                    postDirection = splitted[rInd + 2]
                    splitted.removeAt(rInd + 2)
                    postDirInd = -1;
                }
                if (cInd - 1 > 0 && model.directions.contains(splitted[cInd - 1])) {
                    pd = cInd - 1;
                    preDirInd = -1
                    preDirection = splitted[cInd - 1]
                }
                splitted.removeAt(rInd + 1)
                for (ind in rInd downTo cInd step 1) {
                    splitted.removeAt(ind)
                }
                if (pd != -1) {
                    splitted.removeAt(pd);
                }
            }

        } else if (streetInd != -1) {
            splitted[streetInd] = model.street_abbreviations.get(splitted[streetInd]).toString();
            suffix = splitted[streetInd];
        }

        if (preDirInd != -1) {
            splitted[preDirInd] = model.directions.get(splitted[preDirInd]).toString()
            preDirection = splitted[preDirInd];
        }

        if (postDirInd != -1) {
            splitted[postDirInd] = model.directions.get(splitted[postDirInd]).toString();
            postDirection = splitted[postDirInd]
            splitted.removeAt(postDirInd)
        }

        if (!special) {
            splitted.removeAt(streetInd)
        }

        if (preDirInd != -1) {
            splitted.removeAt(preDirInd)
        }

        splitted.removeAt(0)
        if (streetName == "") {
            streetName = splitted.joinToString(" ");
        }

        return listOf(primaryAddressNumber, preDirection, streetName, suffix, postDirection)
    }

    companion object : SingletonHolder<USAddressStandardizer>(::USAddressStandardizer)
}