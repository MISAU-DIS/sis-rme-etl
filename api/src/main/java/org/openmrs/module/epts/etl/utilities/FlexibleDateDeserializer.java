package org.openmrs.module.epts.etl.utilities;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class FlexibleDateDeserializer extends JsonDeserializer<Date> {

	private static final String[] DATE_FORMATS = { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS",
			"yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd" };

	@Override
	public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException {

		JsonToken token = parser.getCurrentToken();

		if (token == JsonToken.VALUE_NULL) {
			return null;
		}

		// Numeric JSON timestamps (milliseconds)
		if (token == JsonToken.VALUE_NUMBER_INT) {
			return new Date(parser.getLongValue());
		}

		String value = parser.getValueAsString();

		if (value == null || value.trim().isEmpty()) {
			return null;
		}

		value = value.trim();

		// Numeric timestamps represented as strings
		if (value.matches("^-?\\d{11,17}$")) {

			try {
				return new Date(Long.parseLong(value));
			} catch (NumberFormatException e) {
				throw new IOException("Invalid Unix timestamp: " + value, e);
			}
		}

		// Try supported textual date formats
		for (String format : DATE_FORMATS) {

			SimpleDateFormat sdf = new SimpleDateFormat(format);

			sdf.setLenient(false);

			sdf.setTimeZone(TimeZone.getTimeZone("Africa/Maputo"));

			ParsePosition position = new ParsePosition(0);

			Date date = sdf.parse(value, position);

			if (date != null && position.getIndex() == value.length()) {
				return date;
			}
		}

		throw new IOException("Unsupported date format: " + value);
	}
}