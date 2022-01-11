package io.standardizer.address.model

import io.standardizer.address.exception.InvalidModelException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*
import java.lang.Exception


class ModelFactory {
    companion object {
        /**
         * Creates a model that can be used by the standardizer
         * @param p The path of the data file
         * @param c The name of the country
         */
        @JvmStatic
        fun create (f : File, c: String) {
            val data = JSONObject(JSONTokener(FileReader(f))).toMap();
            var streetAbr = HashMap<String, String>();
            var secondaryUnits = HashMap<String, String>();
            var states = HashMap<String, String>();
            var directions = HashMap<String, String>();
            var miscellaneous = HashMap<String, String>();
            var csl = HashMap<String, String>();
            if (data.get("street_abbreviations") != null) {
                val m : Map<String, List<String>> = data.get("street_abbreviations") as Map<String, List<String>>;
                for (key in m.keys) {
                    streetAbr.put(key, key);
                    for (value in m.get(key)!!) {
                        streetAbr.put(value.uppercase(), key);
                    }
                }
            }

            if (data.get("secondary_units") != null) {
                val m : Map<String, String> = data.get("secondary_units") as Map<String, String>;
                for (key in m.keys) {
                    secondaryUnits.put(key, key);
                    m[key]?.let { secondaryUnits.put(it.uppercase(), key) };
                }
            }

            if (data.get("states") != null) {
                val m : Map<String, String> = data.get("states") as Map<String, String>;
                for (key in m.keys) {
                    states.put(key, key);
                    m[key]?.let { states.put(it.uppercase(), key) };
                }
            }

            if (data.get("directions") != null) {
                val m : Map<String, String> = data.get("directions") as Map<String, String>;
                for (key in m.keys) {
                    directions.put(key, key);
                    m[key]?.let { directions.put(it.uppercase(), key) };
                }
            }

            if (data.get("miscellaneous") != null) {
                val m : Map<String, List<String>> = data.get("miscellaneous") as Map<String, List<String>>;
                for (key in m.keys) {
                    miscellaneous.put(key, key);
                    for (value in m.get(key)!!) {
                        miscellaneous.put(value.uppercase(), key);
                    }
                }
            }

            if (data.get("county_state_localhwy") != null) {
                val m : Map<String, List<String>> = data.get("county_state_localhwy") as Map<String, List<String>>;
                for (key in m.keys) {
                    csl.put(key, key);
                    for (value in m.get(key)!!) {
                        csl.put(value.uppercase(), key);
                    }
                }
            }

            val mdl = Model(streetAbr, secondaryUnits, states, directions, miscellaneous, csl);
            File("./models").mkdir();
            ObjectOutputStream(FileOutputStream("./models/" + c + ".model")).use{ it -> it.writeObject(mdl)}
        }

        /**
         * Loads a model
         * @param m Model name
         * @return Model
         */
        @JvmStatic
        fun load(m: String): Model {
//            val f = File("./models/$m.model");
//            if (!f.exists()) throw ModelNotFoundException("Model \"$m\" could not be found");
            try {
                ObjectInputStream(this.javaClass.classLoader.getResourceAsStream("models/$m.model")).use { it ->
                    val model = it.readObject()

                    when (model) {
                        is Model -> return model
                        else -> throw InvalidModelException("Model is corrupted, please remake model!")
                    }
                }
            } catch (e : Exception) {
                e.printStackTrace()
                throw InvalidModelException("Model is corrupted, please remake model!")

            }

        }
    }
}