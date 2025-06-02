package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Phone;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for managing Phone entities.
 * Acts as the service layer coordinating application logic and data persistence.
 */
@Slf4j
@UseCase
public class PhoneUseCase implements PhoneInputPort {

    private PhoneOutputPort phonePersistence;

    /**
     * Constructor to inject the specific PhoneOutputPort implementation.
     */
    public PhoneUseCase(@Qualifier("phoneOutputAdapterMaria") PhoneOutputPort phonePersistence) {
        this.phonePersistence = phonePersistence;
    }

    /**
     * Allows dynamic change of persistence adapter at runtime.
     */
    @Override
    public void setPersistence(PhoneOutputPort phonePersistence) {
        this.phonePersistence = phonePersistence;
    }

    /**
     * Creates a new Phone entry if it doesn't already exist.
     * @throws NoExistException if a phone with the same number already exists.
     */
    @Override
    public Phone create(Phone phone) throws NoExistException {
        if (phonePersistence.findById(phone.getNumber()) == null) {
            return phonePersistence.save(phone);
        }
        throw new NoExistException("Creation failed: phone with number " + phone.getNumber() + " already exists.");
    }

    /**
     * Updates an existing Phone record by number.
     * @throws NoExistException if the phone does not exist in persistence.
     */
    @Override
    public Phone edit(String number, Phone phone) throws NoExistException {
        if (phonePersistence.findById(number) != null) {
            phone.setNumber(number); // Ensure ID consistency between path and object
            return phonePersistence.save(phone);
        }
        throw new NoExistException("Update failed: no phone record found with number " + number + ".");
    }

    /**
     * Deletes a Phone record by number.
     * @throws NoExistException if the phone does not exist.
     */
    @Override
    public Boolean drop(String number) throws NoExistException {
        if (phonePersistence.findById(number) != null) {
            return phonePersistence.delete(number);
        }
        throw new NoExistException("Deletion failed: phone with number " + number + " does not exist.");
    }

    /**
     * Retrieves all Phone records from persistence.
     */
    @Override
    public List<Phone> findAll() {
        log.info("Listing all phones from persistence");
        return phonePersistence.find();
    }

    /**
     * Retrieves a single Phone by its number.
     * @throws NoExistException if the phone is not found.
     */
    @Override
    public Phone findOne(String number) throws NoExistException {
        Phone phone = phonePersistence.findById(number);
        if (phone != null) {
            return phone;
        }
        throw new NoExistException("Lookup failed: no phone found with number " + number + ".");
    }

    /**
     * Returns the total count of Phone entries.
     */
    @Override
    public Integer count() {
        return findAll().size();
    }

    /**
     * Retrieves all phones associated with a specific person ID.
     * Logs a warning if none are found.
     */
    @Override
    public List<Phone> getPhonesOfPerson(Integer personId) throws NoExistException {
        List<Phone> phones = phonePersistence.findByPersonId(personId);
        if (phones == null || phones.isEmpty()) {
            log.warn("No phone records found for person with ID {} or the person may not exist.", personId);
        }
        return phones;
    }
}
