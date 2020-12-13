package socialnetwork.domain.validators;

import socialnetwork.domain.Prietenie;

public class PrietenieValidator implements Validator<Prietenie>{
    @Override
    public void validate(Prietenie entity) throws ValidationException {
        if(entity.getId().getLeft()==entity.getId().getRight())
            throw new ValidationException("Nu poate fi acelasi id");
    }
}
