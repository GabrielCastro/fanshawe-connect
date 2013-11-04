package ca.GabrielCastro.betterpreferences.demo;

import java.util.HashSet;
import java.util.Set;

import ca.GabrielCastro.betterpreferences.BetterPreference;
import ca.GabrielCastro.betterpreferences.preftypes.BooleanPreference;
import ca.GabrielCastro.betterpreferences.preftypes.FloatPreference;
import ca.GabrielCastro.betterpreferences.preftypes.IntegerPreference;
import ca.GabrielCastro.betterpreferences.preftypes.LongPreference;
import ca.GabrielCastro.betterpreferences.preftypes.StringPreference;
import ca.GabrielCastro.betterpreferences.preftypes.StringSetPreference;

public interface KnownPrefs {

    BetterPreference<String> STRING = new StringPreference("string", "no text");
    BetterPreference<Set<String>> STRING_SET = new StringSetPreference("string_set", new HashSet<String>());
    BetterPreference<Integer> INTEGER = new IntegerPreference("integer", 0);
    BetterPreference<Long> LONG = new LongPreference("long", 0l);
    BetterPreference<Float> FLOAT = new FloatPreference("float", 0f);
    BetterPreference<Boolean> BOOLEAN = new BooleanPreference("is_funny", false);

}
