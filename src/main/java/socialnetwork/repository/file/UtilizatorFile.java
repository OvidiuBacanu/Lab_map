package socialnetwork.repository.file;

import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class UtilizatorFile extends AbstractFileRepository<Long, Utilizator>{

    public long id;

    public UtilizatorFile(String fileName, Validator<Utilizator> validator) {
        super(fileName, validator);
        id=get_max_id();
    }

    @Override
    public Utilizator extractEntity(List<String> attributes) {
        Utilizator user = new Utilizator(attributes.get(1),attributes.get(2));
        long aux=Long.parseLong(attributes.get(0));
        user.setId(aux);
        return user;
    }

    @Override
    protected String createEntityAsString(Utilizator entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }

    @Override
    public Utilizator save(Utilizator entity) {
        id=next_id(id);
        entity.setId(id);
        return super.save(entity);
    }

    public long get_max_id(){
        long max=0;
        for(long aux:getEntities().keySet())
            if(aux>max)
                max=aux;
        return max;
    }

    public long next_id(long id){
        return id+1;
    }
}
