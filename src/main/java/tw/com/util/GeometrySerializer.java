package tw.com.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.vividsolutions.jts.geom.Geometry;

public class GeometrySerializer extends JsonSerializer<Geometry> 
{
	// model > request
				
		@Override
		public void serialize( Geometry inputGeometry, JsonGenerator generator, SerializerProvider provider ) throws IOException {
			
			generator.writeStartObject();
			generator.writeStringField("longitude", String.valueOf(inputGeometry.getCoordinate().x));
			generator.writeStringField("latitude", String.valueOf(inputGeometry.getCoordinate().y));
			generator.writeEndObject();
			
		} // serialize()
		
	}

