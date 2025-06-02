package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Profession;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for handling Profession-related operations.
 * Coordinates between the application's input layer and the persistence port.
 */
@Slf4j
@UseCase
public class ProfessionUseCase implements ProfessionInputPort {

    private ProfessionOutputPort professionPersistence;

    /**
     * Constructor with dependency injection of the persistence layer.
     * The @Qualifier selects the default adapter implementation.
     */
    public ProfessionUseCase(@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionOutputPort) {
        this.professionPersistence = professionOutputPort;
    }

    /**
     * Allows switching the persistence port dynamically at runtime.
     */
    @Override
    public void setPersistence(ProfessionOutputPort professionOutputPort) {
        this.professionPersistence = professionOutputPort;
    }

    /**
     * Creates and stores a new Profession entity in the persistence layer.
     */
    @Override
    public Profession create(Profession profession) {
        log.debug("Into create Profession UseCase");
        return professionPersistence.save(profession);
    }

    /**
     * Updates an existing Profession identified by ID.
     * Ensures the ID in the path is set in the object to persist.
     * @throws NoExistException if the profession doesn't exist in the DB.
     */
    @Override
    public Profession edit(Integer identification, Profession profession) throws NoExistException {
        log.debug("Into edit Profession UseCase");
        Profession oldProfession = professionPersistence.findById(identification);
        if (oldProfession != null) {
            profession.setIdentification(identification); // Ensure consistency
            return professionPersistence.save(profession);
        }
        throw new NoExistException("Update failed: profession with ID " + identification + " not found.");
    }

    /**
     * Deletes a Profession record by its ID.
     * @throws NoExistException if the profession does not exist.
     */
    @Override
    public Boolean drop(Integer identification) throws NoExistException {
        log.debug("Into drop Profession UseCase");
        Profession oldProfession = professionPersistence.findById(identification);
        if (oldProfession != null) {
            return professionPersistence.delete(identification);
        }
        throw new NoExistException("Deletion failed: no profession found with ID " + identification + ".");
    }

    /**
     * Retrieves all Profession records.
     */
    @Override
    public List<Profession> findAll() {
        log.debug("Into findAll Profession UseCase");
        return professionPersistence.findAll();
    }

    /**
     * Fetches a specific Profession by ID.
     * @throws NoExistException if the profession is not found.
     */
    @Override
    public Profession findOne(Integer identification) throws NoExistException {
        log.debug("Into findOne Profession UseCase");
        Profession profession = professionPersistence.findById(identification);
        if (profession != null) {
            return profession;
        }
        throw new NoExistException("Lookup failed: profession with ID " + identification + " was not found.");
    }

    /**
     * Returns the total number of professions stored.
     */
    @Override
    public Integer count() {
        log.debug("Into count Profession UseCase");
        return findAll().size();
    }
}
