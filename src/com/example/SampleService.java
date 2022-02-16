package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.GsonBuilder;

@Path("/sample-service")
public class SampleService {

	private static final Connection CONNECTION;

	private static final List<Person> PERSONS_DEFAULT;

	static {
		PERSONS_DEFAULT = new ArrayList<>();
		PERSONS_DEFAULT.add(new Person("Arnold", "Schwarzenegger"));
		PERSONS_DEFAULT.add(new Person("Bruce", "Wayne"));
		PERSONS_DEFAULT.add(new Person("Mickey", "Mouse"));
		PERSONS_DEFAULT.add(new Person("Darkwing", "Duck"));

		CONNECTION = createConnection();
		setupDatabase();
	}

	@GET
	@Path("/all-persons")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllPersons() {
		try (Statement stmt = CONNECTION.createStatement();) {
			List<Person> dbPersons = new ArrayList<>();

			ResultSet rs = stmt.executeQuery("SELECT firstname, lastname FROM person");
			while (rs.next()) {
				dbPersons.add(new Person(rs.getString("firstname"), rs.getString("lastname")));
			}

			return new GsonBuilder().setPrettyPrinting().create().toJson(dbPersons);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	private static Connection createConnection() {
		try {
			return DriverManager.getConnection(System.getenv("OPENSHIFT_DATABASE_URL"),
					System.getenv("OPENSHIFT_DATABASE_USER"), System.getenv("OPENSHIFT_DATABASE_PASSWORD"));

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private static void setupDatabase() {
		try (ResultSet tables = CONNECTION.getMetaData().getTables(null, null, "person", null);) {
			if (!tables.next()) {
				createPersonTable();
				createPersonData();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private static void createPersonTable() {
		try (Statement stmt = CONNECTION.createStatement();) {
			stmt.executeUpdate("CREATE TABLE person (" //
					+ "id INT(64) NOT NULL AUTO_INCREMENT," //
					+ "firstname VARCHAR(255)," //
					+ "lastname VARCHAR(255)," //
					+ "PRIMARY KEY(id))");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private static void createPersonData() {
		try (PreparedStatement ps = CONNECTION
				.prepareStatement("INSERT INTO person (firstname, lastname) VALUES(?,?)");) {

			for (Person person : PERSONS_DEFAULT) {
				ps.setString(1, person.getFirstname());
				ps.setString(2, person.getLastname());
				ps.addBatch();
			}

			ps.executeBatch();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
