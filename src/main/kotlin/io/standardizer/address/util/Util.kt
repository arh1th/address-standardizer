package io.standardizer.address.util

import io.standardizer.address.model.Model

class Util {
    companion object {
        fun containsNumber(s: String) : Boolean {
            return s.matches(Regex(".*\\d.*"))
        }

        fun extractNumber(s: String) : String {
            return s.filter { it.isDigit() }
        }

        fun removeAndReturn(start: Int, end: Int, l: List<Any>) : List<Any> {
            var l2 = l.toMutableList();
            for (i in end - 1 downTo start step 1) {
                l2.removeAt(i)
            }
            return l2.toList();
        }

        fun findCSL(splitted: List<String>, model: Model) : Map<Any?, Any?> {
            var found = false
            var foundCSL = ""
            var isInterstateWithI = false;
            var isIh = false
            val sortedMap: MutableMap<String, String> = LinkedHashMap()
            model.csl.keys.sortedBy { it.length }.forEach { sortedMap[it] = model.csl[it]!! }
            for (i in sortedMap.keys) {
                var f = false
                if (i == "I") {
                    for (j in splitted) {
                        if (j.startsWith("I") && containsNumber(j) && !j.startsWith("IH")) {
                            f = true;
                            isInterstateWithI = true;
                            break;
                        }
                        else if (j.startsWith("I") && j.length == 1) {
                            f = true;
                            break;
                        }
                    }
                } else if (i == "IH") {
                    for (j in splitted) {
                        if (j.startsWith("IH") && containsNumber(j)) {
                            f = true;
                            isIh = true;
                            break;
                        }
                        else if (j.startsWith("IH") && j.length == 2) {
                            f = true;
                            break;
                        }
                    }
                }
                else {
                    for (j in i.split(" ")) {
                        if (splitted.contains(j)) {
                            f = true;
                        } else {
                            f = false;
                            break;
                        }
                    }
                }

                if (f) {
                    found = true;
                    foundCSL = i;
                }
            }
            if (!found) {
                return hashMapOf(
                    "found" to false,
                    "start" to -1,
                    "end" to -1,
                    "isInterstateWithI" to false,
                    "isIh" to false,
                    "csl" to ""
                )
            }

            if (isInterstateWithI) {
                for ((i, e) in splitted.withIndex()) {
                    if (e.startsWith("I") && containsNumber(e)) {
                        return hashMapOf(
                            "found" to true,
                            "start" to i,
                            "end" to i,
                            "isInterstateWithI" to true,
                            "isIh" to false,
                            "csl" to "I"
                        )
                    }
                }
            }
            else if (isIh) {
                for ((i, e) in splitted.withIndex()) {
                    if (e.startsWith("IH") && containsNumber(e)) {
                        return hashMapOf(
                            "found" to true,
                            "start" to i,
                            "end" to i,
                            "isInterstateWithI" to false,
                            "isIh" to true,
                            "csl" to "IH"
                        )
                    }
                }
            }

            var cslSplitted : List<String> = foundCSL.split(" ")

            if (cslSplitted.size == 1) {
                var cind = splitted.indexOf(foundCSL)
                return hashMapOf(
                    "found" to true,
                    "start" to cind,
                    "end" to cind,
                    "isInterstateWithI" to false,
                    "isIh" to false,
                    "csl" to foundCSL
                )
            }

            var cind = splitted.indexOf(cslSplitted[0])
            var rind = splitted.indexOf(cslSplitted[cslSplitted.size - 1])
            return hashMapOf(
                "found" to true,
                "start" to cind,
                "end" to rind,
                "isInterstateWithI" to false,
                "isIh" to false,
                "csl" to foundCSL
            )
        }

        fun isOppositeDir(dir1: String, dir2: String) : Boolean {
            return (dir1 == "S" && dir2 == "N") || (dir1 == "N" && dir2 == "S") || (dir1 == "E" && dir2 == "W") || (dir1 == "W" && dir2 == "E") || dir1 == dir2 || ((dir1 == "E" || dir1 == "W") && (dir2 == "N" || dir2 == "S"))
        }
    }
}