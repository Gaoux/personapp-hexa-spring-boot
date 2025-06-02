package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation of Person operations.
 * Acts as a bridge between input ports and persistence layer (output ports).
 */
@Slf4j
@UseCase
public class PersonUseCase implements PersonInputPort {

	private PersonOutputPort personPersistance;

	/**
	 * Constructor to inject a specific implementation of PersonOutputPort.
	 * The @Qualifier indicates which bean to inject.
	 */
	public PersonUseCase(@Qualifier("personOutputAdapterMaria") PersonOutputPort personOutputPort) {
		this.personPersistance = personOutputPort;
	}

	/**
	 * Allows switching the persistence port implementation dynamically.
	 */
	@Override
	public void setPersistence(PersonOutputPort personOutputPort) {
		this.personPersistance = personOutputPort;
	}

	/**
	 * Creates and saves a new Person entity in the persistence layer.
	 */
	@Override
	public Person create(Person person) {
		log.debug("Into create Person UseCase");
		return personPersistance.save(person);
	}

	/**
	 * Updates an existing Person identified by their ID.
	 * Throws exception if the Person doesn't exist.
	 */
	@Override
	public Person edit(Long cc, Person person) throws NoExistException {
		log.debug("Into edit Person UseCase");
		Person oldPerson = personPersistance.findById(cc);
		if (oldPerson != null)
			return personPersistance.save(person);
		throw new NoExistException(
				"The person with id " + cc + " does not exist into db, cannot be edited");
	}

	/**
	 * Deletes a Person from persistence if they exist.
	 * Throws exception if the Person doesn't exist.
	 */
	@Override
	public Boolean drop(Long cc) throws NoExistException {
		log.debug("Into drop Person UseCase");
		Person oldPerson = personPersistance.findById(cc);
		if (oldPerson != null)
			return personPersistance.delete(cc);
		throw new NoExistException(
				"The person with id " + cc + " does not exist into db, cannot be dropped");
	}

	/**
	 * Retrieves all Person entities.
	 */
	@Override
	public List<Person> findAll() {
		log.debug("Into findAll Person UseCase");
		return personPersistance.findAll();
	}

	/**
	 * Retrieves a single Person by ID.
	 * Throws exception if the Person doesn't exist.
	 */
	@Override
	public Person findOne(Long cc) throws NoExistException {
		log.debug("Into findOne Person UseCase");
		Person oldPerson = personPersistance.findById(cc);
		if (oldPerson != null)
			return oldPerson;
		throw new NoExistException(
				"The person with id " + cc + " does not exist into db, cannot be found");
	}

	/**
	 * Returns the number of Person records in the system.
	 */
	@Override
	public Integer count() {
		return findAll().size();
	}

	/**
	 * Retrieves the phone numbers associated with a given Person.
	 * Throws exception if the Person doesn't exist.
	 */
	@Override
	public List<Phone> getPhones(Long cc) throws NoExistException {
		Person oldPerson = personPersistance.findById(cc);
		if (oldPerson != null)
			return oldPerson.getPhoneNumbers();
		throw new NoExistException(
				"The person with id " + cc + " does not exist into db, cannot get phones");
	}

	/**
	 * Retrieves the studies associated with a given Person.
	 * Throws exception if the Person doesn't exist.
	 */
	@Override
	public List<Study> getStudies(Long cc) throws NoExistException {
		Person oldPerson = personPersistance.findById(cc);
		if (oldPerson != null)
			return oldPerson.getStudies();
		throw new NoExistException(
				"The person with id " + cc + " does not exist into db, cannot get studies");
	}
}
