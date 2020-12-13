package socialnetwork.service;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;

public class UtilizatorService {
    private Repository<Long, Utilizator>repo;

    public UtilizatorService(Repository<Long, Utilizator> repo) {
        this.repo = repo;
    }

    public Utilizator addUtilizator(Utilizator messageTask) {
        Utilizator task = repo.save(messageTask);
        return task;
    }

    public Utilizator removeUtilizator(Long id){
        Utilizator task=repo.delete(id);
        return task;
    }

    public Iterable<Utilizator> getAll(){
        return repo.findAll();
    }
}
