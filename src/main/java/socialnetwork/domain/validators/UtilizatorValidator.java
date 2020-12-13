package socialnetwork.domain.validators;

import socialnetwork.domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        String exception_message="";
        if(entity.getFirstName()==null || "".equals(entity.getFirstName()))
            exception_message+="First name error!\n";
        if(entity.getLastName()==null || "".equals(entity.getLastName()))
            exception_message+="Last name error!\n";
        if(exception_message!="")
            throw new ValidationException(exception_message);
    }
}
