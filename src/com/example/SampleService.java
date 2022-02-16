package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.GsonBuilder;

@Path("/sample-service")
public class SampleService {

	private static final Connection CONN;

	static {
		try {
			CONN = DriverManager.getConnection(System.getenv("OPENSHIFT_DATABASE_URL"),
					System.getenv("OPENSHIFT_DATABASE_USER"), System.getenv("OPENSHIFT_DATABASE_PASSWORD"));

			System.out.println(CONN.toString());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@GET
	@Path("/all-persons")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllPersons() {
		List<Person> persons = new ArrayList<>();

		persons.add(new Person("Arnold", "Schwarzenegger"));
		persons.add(new Person("Bruce", "Wayne"));
		persons.add(new Person("Mickey", "Mouse"));
		persons.add(new Person("Darkwing", "Duck"));

		return new GsonBuilder().setPrettyPrinting().create().toJson(persons);
	}

}
