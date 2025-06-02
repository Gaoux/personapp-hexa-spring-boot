package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for managing Study relationships between Persons and Professions.
 * Implements business logic for creating, editing, deleting, and retrieving study records.
 */
@Slf4j
@UseCase
public class StudyUseCase implements StudyInputPort {

    private StudyOutputPort studyPersintence;

    /**
     * Constructor injecting the selected StudyOutputPort (e.g., MariaDB adapter).
     */
    public StudyUseCase(@Qualifier("studyOutputAdapterMaria") StudyOutputPort studyPersintence) {
        this.studyPersintence = studyPersintence;
    }

    /**
     * Allows dynamic reassignment of the persistence layer for studies.
     */
    @Override
    public void setPersintence(StudyOutputPort studyPersintence) {
        this.studyPersintence = studyPersintence;
    }

    /**
     * Creates a new study association if it doesn't already exist.
     * @throws NoExistException if the person-profession pair already has an associated study.
     */
    @Override
    public Study create(Study study) throws NoExistException {
        if (studyPersintence.findById(study.getPerson().getIdentification(), study.getProfession().getIdentification()) == null) {
            return studyPersintence.save(study);
        }
        throw new NoExistException(
                "Creation failed: study already registered for personId=" + study.getPerson().getIdentification()
                        + " and professionId=" + study.getProfession().getIdentification());
    }

    /**
     * Updates an existing study entry based on person and profession IDs.
     * @throws NoExistException if no such study relationship exists.
     */
    @Override
    public Study edit(Integer personId, Integer professionId, Study study) throws NoExistException {
        if (studyPersintence.findById(personId, professionId) != null) {
            // Ensure the correct composite key values are set before saving
            study.getPerson().setIdentification(personId);
            study.getProfession().setIdentification(professionId);
            return studyPersintence.save(study);
        }
        throw new NoExistException(
                "Update failed: no study found for personId=" + personId + " and professionId=" + professionId);
    }

    /**
     * Deletes an existing study relationship from persistence.
     * @throws NoExistException if the specified study does not exist.
     */
    @Override
    public Boolean drop(Integer personId, Integer professionId) throws NoExistException {
        if (studyPersintence.findById(personId, professionId) != null) {
            return studyPersintence.delete(personId, professionId);
        }
        throw new NoExistException(
                "Deletion failed: study entry not found for personId=" + personId + " and professionId=" + professionId);
    }

    /**
     * Retrieves all study records.
     */
    @Override
    public List<Study> findAll() {
        log.info("Listing all studies from persistence");
        return studyPersintence.find();
    }

    /**
     * Fetches a single study record by composite key (person ID + profession ID).
     * @throws NoExistException if no such record is found.
     */
    @Override
    public Study findOne(Integer personId, Integer professionId) throws NoExistException {
        Study study = studyPersintence.findById(personId, professionId);
        if (study != null) {
            return study;
        }
        throw new NoExistException(
                "Query failed: no study exists with personId=" + personId + " and professionId=" + professionId);
    }

    /**
     * Returns the total number of study entries.
     */
    @Override
    public Integer count() {
        return findAll().size();
    }
}
