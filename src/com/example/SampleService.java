package com.example;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.GsonBuilder;

@Path("/sample-service")
public class SampleService {

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
