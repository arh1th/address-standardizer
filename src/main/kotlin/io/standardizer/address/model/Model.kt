package io.standardizer.address.model

import java.io.Serializable

data class Model(val street_abbreviations: HashMap<String, String>, val secondary_units: HashMap<String, String>, val states: HashMap<String, String>, val directions: HashMap<String, String>, val miscellaneous: HashMap<String, String>, val csl: HashMap<String, String>) : Serializable
