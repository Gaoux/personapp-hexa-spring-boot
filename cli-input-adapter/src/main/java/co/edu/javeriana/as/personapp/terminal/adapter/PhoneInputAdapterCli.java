package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PhoneInputAdapterCli {

    @Autowired
    @Qualifier("phoneOutputAdapterMaria")
    private PhoneOutputPort phoneOutputPortMaria;

    @Autowired
    @Qualifier("phoneOutputAdapterMongo")
    private PhoneOutputPort phoneOutputPortMongo;

    @Autowired
    @Qualifier("personOutputAdapterMaria")
    private PersonOutputPort personOutputPortMaria;

    @Autowired
    @Qualifier("personOutputAdapterMongo")
    private PersonOutputPort personOutputPortMongo;

    @Autowired
    private TelefonoMapperCli personaMapperCli;

    PhoneInputPort personInputPort;

    public void setPersonOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            personInputPort = new PhoneUseCase(phoneOutputPortMaria);
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            personInputPort = new PhoneUseCase(phoneOutputPortMongo);
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }

    public void create(Scanner sc) {
        log.info("Creating PersonEntity in Input Adapter");
        try {
            sc = new Scanner(System.in);
            System.out.println("Enter new number: ");
            String identification = sc.nextLine();
            System.out.println("Enter new company: ");
            String companyName = sc.nextLine();
            System.out.println("Enter new owner ID: ");
            String ownerId = sc.nextLine();
            Person person = personInputPort.getPerson(String.valueOf(Integer.parseInt(ownerId)));
            Phone phone = new Phone(identification, companyName, person);
            phone = personInputPort.create(phone);
            System.out.println(personaMapperCli.fromDomainToAdapterCli(phone, new PersonaModelCli(person)));
        } catch (NoExistException e) {
            log.warn(e.getMessage());
        }
    }

    public void edit(Scanner sc) {
        log.info("Editing PersonEntity in Input Adapter");
        try {
            sc = new Scanner(System.in);
            System.out.println("Current number: ");
            String old_identification = sc.nextLine();
            System.out.println("New number: ");
            String identification = sc.nextLine();
            System.out.println("New company: ");
            String companyName = sc.nextLine();
            System.out.println("New owner ID: ");
            String ownerId = sc.nextLine();
            Person person = personInputPort.getPerson(String.valueOf(Integer.parseInt(ownerId)));
            Phone phone = new Phone(identification, companyName, person);
            phone = personInputPort.edit(old_identification, phone);
            System.out.println(personaMapperCli.fromDomainToAdapterCli(phone, new PersonaModelCli(person)));
        } catch (NoExistException e) {
            log.warn(e.getMessage());
        }
    }

    public void delete(Scanner sc) {
        log.info("Deleting PersonEntity in Input Adapter");
        try {
            sc = new Scanner(System.in);
            System.out.println("Current identification: ");
            String old_identification = sc.nextLine();
            System.out.println(personInputPort.drop(old_identification));
        } catch (NoExistException e) {
            log.warn(e.getMessage());
        }
    }

    public void history() {
        log.info("Into history of PersonEntity in Input Adapter");
        personInputPort.findAll().stream()
                .map(phone -> personaMapperCli.fromDomainToAdapterCli(phone,
                        new PersonaModelCli(phone.getOwner())))
                .forEach(System.out::println);
    }
}
