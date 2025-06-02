package co.edu.javeriana.as.personapp.application.port.out;

import java.util.List;

import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.domain.Phone;

@Port
public interface PhoneOutputPort {

    Phone save(Phone phone);

    Boolean delete(String number);
    
    Phone findById(String number);
    
    List<Phone> findByPersonId(Integer personId);

    List<Phone> find();
} 