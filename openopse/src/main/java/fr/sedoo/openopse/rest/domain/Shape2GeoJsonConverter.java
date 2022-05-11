package fr.sedoo.openopse.rest.domain;

import java.io.File;
import java.io.StringWriter;

import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.json.JSONArray;
import org.json.JSONObject;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import fr.sedoo.openopse.rest.dao.MtropicsJsonStyleDao;

public class Shape2GeoJsonConverter {

	public static String convertShapeFile(File shapeFile, MtropicsJsonStyleDao styleDao) throws Exception {
		FileDataStore dataStore = FileDataStoreFinder.getDataStore(shapeFile);
		SimpleFeatureSource featureSource = dataStore.getFeatureSource();
		String typeName = dataStore.getTypeNames()[0];
		SimpleFeatureType schema = featureSource.getSchema();
		CoordinateReferenceSystem dataCRS = schema.getCoordinateReferenceSystem();
		CoordinateReferenceSystem worldCRS = DefaultGeographicCRS.WGS84;
		boolean lenient = true; // allow for some error due to different datums
		MathTransform transform = CRS.findMathTransform(dataCRS, worldCRS, lenient);

		FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
		Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")

		FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);

		FeatureJSON fjson = new FeatureJSON();
		StringWriter writer = new StringWriter();
		writer.append("{ \"type\": \"FeatureCollection\",\n" + "    \"features\": [");
		FeatureIterator<SimpleFeature> features = collection.features();
		try {
			while (features.hasNext()) {
				SimpleFeature feature = features.next();

				Geometry geometry = (Geometry) feature.getDefaultGeometry();
				if (geometry != null) {
					Geometry geometry2 = JTS.transform(geometry, transform);

					feature.setDefaultGeometry(geometry2);
					fjson.writeFeature(feature, writer);
					if (features.hasNext()) {
						writer.append(",");
					}
				}
			}

			writer.append(" ]\n" + "     }");

			String json = writer.toString();
			return addStyle(json, styleDao);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				features.close();
			} catch (Exception e1) {
				// we do nothing
			}
		}
	}

	private static String addStyle(String content, MtropicsJsonStyleDao styleDao) throws Exception {
		JSONObject json = new JSONObject(content);
		JSONArray features = json.getJSONArray("features");
		for (int i = 0; i < features.length(); i++) {
			JSONObject feature = features.getJSONObject(i);
			styleDao.style(feature);
		}
		return json.toString();
	}

}
