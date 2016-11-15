package tw.com.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class GeometryDeserializer extends JsonDeserializer<Point> 
{

	@Override
	public Point deserialize(JsonParser jsonParser,
			DeserializationContext context) throws JsonProcessingException, IOException {

		JsonNode node = jsonParser.getCodec().readTree(jsonParser);

		GeometryFactory gf = new GeometryFactory();
		Coordinate coordinate = new Coordinate(
				node.get("longitude").asDouble(), node.get("latitude")
						.asDouble());

		return gf.createPoint(coordinate);

	} // deserialize()

}