package socialnetwork.repository.file;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class PrietenieFile extends AbstractFileRepository<Tuple<Long,Long>,Prietenie>{

    public PrietenieFile(String fileName, Validator<Prietenie> validator) {
        super(fileName, validator);
    }

    @Override
    public Prietenie extractEntity(List<String> attributes) {
        long e1=Long.parseLong(attributes.get(0));
        long e2=Long.parseLong(attributes.get(1));
        Prietenie p=new Prietenie();
        Tuple x=new Tuple(e1,e2);
        p.setId(x);
        return p;
    }

    @Override
    protected String createEntityAsString(Prietenie entity) {
        return entity.getId().getLeft().toString()+";"+entity.getId().getRight().toString();
    }
}
