import java.util.*;

import com.google.gson.*;

import java.lang.reflect.Type;

import com.sun.istack.internal.Nullable;

public class RatesDeserializer implements JsonDeserializer<RateObject> {

    @Nullable
    public RateObject deserialize(JsonElement json, Type typeOfT,
                                  JsonDeserializationContext context) throws JsonParseException {
        RateObject rate = null;

        if (json.isJsonObject()) {
            Set<Map.Entry<String, JsonElement>> entries = json.getAsJsonObject().entrySet();
            if (entries.size() > 0) {
                Map.Entry<String, JsonElement> entry = entries.iterator().next();
                rate = new RateObject(entry.getKey(), entry.getValue().getAsDouble());
            }
        }
        return rate;
    }
}