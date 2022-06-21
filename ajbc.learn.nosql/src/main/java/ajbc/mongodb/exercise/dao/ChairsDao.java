package ajbc.mongodb.exercise.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.print.Doc;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

import static com.mongodb.client.model.Updates.*;

import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.internal.operation.UpdateOperation;

import ajbc.learn.nosql.MyConnectionString;
import ajbc.mongodb.exercise.models.Chair;
import ajbc.mongodb.exercise.models.Measurment;
import ajbc.mongodb.exercise.models.Pillow;

public class ChairsDao {

	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> collection;
	private Gson gson;

	private static final String DATABASE = "Furnitures";
	private static final String COLLECTION = "Chairs";

	public ChairsDao() {
		ConnectionString connectionString = MyConnectionString.uri();
		MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString)
				.serverApi(ServerApi.builder().version(ServerApiVersion.V1).build()).build();

		mongoClient = MongoClients.create(settings);
		database = mongoClient.getDatabase(DATABASE);
		collection = database.getCollection(COLLECTION);

		gson = new Gson();
	}

	public void close() {
		mongoClient.close();
	}

	private List<Chair> parseDocuments(List<Document> documents) {
		return documents.stream().map(doc -> gson.fromJson(doc.toJson(), Chair.class)).collect(Collectors.toList());
	}

	private Chair parseDocument(Document document) {
		return gson.fromJson(document.toJson(), Chair.class);
	}

	private Bson getFilterEqId(int id) {
		return eq("chair_id", id);
	}

	// create:

	public void addChair(Chair chair) {
		String jsonChair = gson.toJson(chair);
		Document chairDoc = Document.parse(jsonChair);
		InsertOneResult oneResult = collection.insertOne(chairDoc);

		System.out.println("Chair was added to collection: " + oneResult.wasAcknowledged());
	}

	public void addChairs(List<Chair> chairs) {
		String jsonChair;
		List<Document> chairsDocuments = new ArrayList<Document>();

		for (Chair chair : chairs) {
			jsonChair = gson.toJson(chair);
			chairsDocuments.add(Document.parse(jsonChair));
		}

		InsertManyResult many = collection.insertMany(chairsDocuments);
		System.out.println("Chairs list was added to collection: " + many.wasAcknowledged());
	}

	// read:

	public Chair getChairById(int chairId) {
		Document chairDocument = collection.find(getFilterEqId(chairId)).first();
		
		if (chairDocument == null) {
			return null;
		}

		return gson.fromJson(chairDocument.toJson(), Chair.class);
	}

	public List<Chair> getStools() {
		List<Document> documents = collection.find(eq("is_stool", true)).into(new ArrayList<>());
		return parseDocuments(documents);
	}

	public List<Chair> getChairsByManufacurer(String manufacturer) {
		List<Document> documents = collection.find(eq("manufacturer", manufacturer)).into(new ArrayList<>());
		return parseDocuments(documents);
	}

	public List<Chair> getChairsInPriceRange(float min, float max) {
		List<Document> documents = collection.find(and(gte("price", min), lte("price", max))).into(new ArrayList<>());
		return parseDocuments(documents);
	}

	public List<Chair> getChairsByManufacurers(List<String> manufacturers) {
		List<Document> documents = collection.find(in("manufacturer", manufacturers)).into(new ArrayList<>());
		return parseDocuments(documents);
	}

	public List<Chair> getChairsShorterThen(double height) {
		List<Document> documents = collection.find(lt("measurment.height", height)).into(new ArrayList<>());
		return parseDocuments(documents);
	}

	// update:

	private Bson getUpdateOperations(Chair chair) {
		return combine(set("manufacturer", chair.getManufacturer()), set("model_name", chair.getModelName()),
				set("is_stool", chair.isStool()), set("price", chair.getPrice()),
				set("measurment.height", chair.getMeasurment().getHeight()),
				set("measurment.width", chair.getMeasurment().getWidth()),
				set("measurment.depth", chair.getMeasurment().getDepth()));
	}

	private Chair checkIfEqual(Chair chair) {
		Chair fromDB = getChairById(chair.getChairId());

		if (fromDB.equals(chair)) {
			System.out.println("Chair [" + chair.getChairId() + "] is already updated");
			return fromDB;
		}

		return null;
	}

	public Chair update(Chair chair) {
		Chair fromDB = checkIfEqual(chair);
		if (fromDB != null)
			return fromDB;

		Bson filter = getFilterEqId(chair.getChairId());
		Bson operations = getUpdateOperations(chair);
		UpdateResult updateResult = collection.updateOne(filter, operations);

		System.out.println(
				"Chair [" + chair.getChairId() + "] was updated in the collection: " + updateResult.wasAcknowledged());
		return getChairById(chair.getChairId());
	}

	public Chair updateAndGetBeforeUpdate(Chair chair) {
		Chair fromDB = checkIfEqual(chair);
		if (fromDB != null)
			return fromDB;

		Bson filter = getFilterEqId(chair.getChairId());
		Bson operations = getUpdateOperations(chair);
		Document oldVersion = collection.findOneAndUpdate(filter, operations);

		System.out.println("Chair [" + chair.getChairId() + "] was updated in the collection");
		return parseDocument(oldVersion);
	}

	public List<Chair> update(List<Chair> chairs) {
		List<Chair> fromDB = new ArrayList<>();

		for (Chair chair : chairs) {
			fromDB.add(update(chair));
		}

		return fromDB;
	}

	// using upsert
	public Chair updateWithPillow(Chair chair, Pillow pillow) {
		Bson filter = getFilterEqId(chair.getChairId());
		Bson operation = combine(set("pillow.shape", pillow.getShape()), set("pillow.color", pillow.getColor()));
		UpdateOptions option = new UpdateOptions().upsert(true);
		UpdateResult updateResult = collection.updateOne(filter, operation, option);
		System.out.println(
				"Chair [" + chair.getChairId() + "] was updated in the collection: " + updateResult.wasAcknowledged());
		return parseDocument(collection.find(filter).first());
	}

	// delete:

	public Chair deleteChairById(int id) {

		if (getChairById(id) == null) {
			System.out.println("Chair [" + id + "] does not exist in the collection");
			return null;
		}

		return parseDocument(collection.findOneAndDelete(getFilterEqId(id)));
	}

	public void deleteChairsByManufacurer(String manufacturer) {
		Bson filter = eq("manufacturer", manufacturer);

		if(getChairsByManufacurer(manufacturer).isEmpty()) {
			System.out.println("Manufacturer: [" + manufacturer + "] does not have chairs in the collection: ");
			return;
		}
		
		DeleteResult deleteResult = collection.deleteMany(filter);
		System.out.println("Chairs of manufacturer: [" + manufacturer + "] were deleted from the collection: "
				+ deleteResult.wasAcknowledged());
	}

	public void deleteChairsGteHeight(double height) {
		Bson filter = gte("measurment.height", height);
		
		if(collection.find(filter).into(new ArrayList<Document>()).isEmpty()) {
			System.out.println("There are no chairs with height greater or equal than " + height);
			return;
			
		}
		
		DeleteResult deleteResult = collection.deleteMany(filter);
		
		System.out.println("Chairs with height greater or equal than [" + height + "] were deleted from the collection: "
				+ deleteResult.wasAcknowledged());
	}

	public void deleteCollection() {
		if(collection.countDocuments() == 0) {
			System.out.println("The collection [" + COLLECTION + "] does not exist in database");
			return;
		}
		
		collection.drop();
		System.out.println("The collection [" + COLLECTION + "] was deleted from database.");
	}
}
