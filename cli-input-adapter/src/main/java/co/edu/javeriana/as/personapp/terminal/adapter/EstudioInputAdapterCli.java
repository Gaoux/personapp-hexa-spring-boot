package co.edu.javeriana.as.personapp.terminal.adapter;

import java.time.LocalDate;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class StudyInputAdapterCli {

    @Autowired
    @Qualifier("studyOutputAdapterMaria")
    private StudyOutputPort studyOutputPortMaria;

    @Autowired
    @Qualifier("studyOutputAdapterMongo")
    private StudyOutputPort studyOutputPortMongo;

    @Autowired
    @Qualifier("personOutputAdapterMaria")
    private PersonOutputPort personOutputPortMaria;

    @Autowired
    @Qualifier("personOutputAdapterMongo")
    private PersonOutputPort personOutputPortMongo;

    @Autowired
    @Qualifier("professionOutputAdapterMaria")
    private ProfessionOutputPort professionOutputPortMaria;

    @Autowired
    @Qualifier("professionOutputAdapterMongo")
    private ProfessionOutputPort professionOutputPortMongo;

    @Autowired
    private EstudioMapperCli studyMapperCli;

    StudyInputPort studyInputPort;

    public void setStudyOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            studyInputPort = new StudyUseCase(studyOutputPortMaria);
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            studyInputPort = new StudyUseCase(studyOutputPortMongo);
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }

    public void history() {
        log.info("Retrieving StudyEntity history in Input Adapter");
        studyInputPort.findAll().stream()
                .map(study -> studyMapperCli.fromDomainToAdapterCli(study, new PersonaModelCli(study.getPerson()), new ProfesionModelCli(study.getProfession())))
                .forEach(System.out::println);
    }

    public void create(Scanner sc) {
        log.info("Creating StudyEntity in Input Adapter");
        System.out.println("Enter the new university name: ");
        String universityName = sc.nextLine();
        System.out.println("Enter the new graduation date: ");
        String graduationDate = sc.nextLine();
        System.out.println("Enter the new person ID: ");
        String personId = sc.nextLine();
        System.out.println("Enter the new profession ID: ");
        String professionId = sc.nextLine();

        Person person = new Person(); // Dummy initialization
        Profession profession = new Profession(); // Dummy initialization
        LocalDate date = LocalDate.parse(graduationDate);
        Study study = new Study(person, profession, date, universityName);
        study = studyInputPort.create(study);
        System.out.println(studyMapperCli.fromDomainToAdapterCli(study, new PersonaModelCli(person), new ProfesionModelCli(profession)));
    }

    public void edit(Scanner sc) {
        log.info("Editing StudyEntity in Input Adapter");
        try {
            System.out.println("Current profession ID: ");
            String oldProfessionId = sc.nextLine();
            System.out.println("Current person ID: ");
            String oldPersonId = sc.nextLine();
            System.out.println("New university name: ");
            String universityName = sc.nextLine();
            System.out.println("New graduation date: ");
            String graduationDate = sc.nextLine();
            System.out.println("New person ID: ");
            String personId = sc.nextLine();
            System.out.println("New profession ID: ");
            String professionId = sc.nextLine();

            Person person = new Person(); // Dummy initialization
            Profession profession = new Profession(); // Dummy initialization
            LocalDate date = LocalDate.parse(graduationDate);
            Study study = new Study(person, profession, date, universityName);
            study = studyInputPort.edit(study, Integer.parseInt(oldPersonId), Integer.parseInt(oldProfessionId));
            System.out.println(studyMapperCli.fromDomainToAdapterCli(study, new PersonaModelCli(person), new ProfesionModelCli(profession)));
        } catch (NoExistException e) {
            log.warn(e.getMessage());
        }
    }

    public void delete(Scanner sc) {
        log.info("Deleting StudyEntity in Input Adapter");
        try {
            System.out.println("Current profession ID: ");
            String professionId = sc.nextLine();
            System.out.println("Current person ID: ");
            String personId = sc.nextLine();
            System.out.println(studyInputPort.drop(Integer.parseInt(professionId), Integer.parseInt(personId)));
        } catch (NoExistException e) {
            log.warn(e.getMessage());
        }
    }
}
