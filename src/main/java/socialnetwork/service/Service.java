package socialnetwork.service;

import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.grafuri.Graf;
import socialnetwork.observer.Observable;
import socialnetwork.observer.Observer;
import socialnetwork.repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class Service implements Observable {
    private Repository<Long, Utilizator> user_repo;
    private Repository<Tuple<Long,Long>, Prietenie> friendship_repo;
    private Repository<Long,Message> message_repo;
    public Graf graf;

    public Service(Repository<Long, Utilizator> user_repo, Repository<Tuple<Long, Long>, Prietenie> friendship_repo,Graf graf) {
        this.user_repo = user_repo;
        this.friendship_repo = friendship_repo;
        this.graf=graf;

        adauga_prietenii_fisier();
        populare_graf();
    }

    public Service(Repository<Long, Utilizator> user_repo, Repository<Tuple<Long, Long>, Prietenie> friendship_repo, Repository<Long, Message> message_repo, Graf graf) {
        this.user_repo = user_repo;
        this.friendship_repo = friendship_repo;
        this.message_repo = message_repo;
        this.graf = graf;
        adauga_prietenii_fisier();
        populare_graf();
    }

    /*adauga prieteniile din fisier in lista friends a utilizatorilor*/
    public void adauga_prietenii_fisier(){
        for(Prietenie p:friendship_repo.findAll()){
            Tuple<Long,Long> tuple=p.getId();
            Utilizator u1=user_repo.findOne(p.getId().getLeft());
            Utilizator u2=user_repo.findOne(p.getId().getRight());
            u1.addFriend(u2);
            u2.addFriend(u1);
        }
    }

    //creeaza graful cu datele din fisier
    public void populare_graf(){
        for(Utilizator u:user_repo.findAll())
            graf.addNod(u.getId());
        for(Prietenie p:friendship_repo.findAll())
            graf.addMuchie(p.getId().getLeft(),p.getId().getRight());
    }

    //service utilizator

    //adauga utilizator daca exista, daca nu arunca exceptie
    public void addUtilizator(Utilizator utilizator) {
        if(user_repo.save(utilizator)!=null)
            throw new ServiceException("Utilizator existent!");
        graf.addNod(utilizator.getId());
    }

    //sterge utilizator daca nu exista, daca nu arunca exceptie
    public void removeUtilizator(Long id){
        Utilizator task=user_repo.delete(id);
        if(task==null)
            throw new ServiceException("Id inexistent!");
        if(task!=null)
            for(Utilizator u:task.getFriends()){
                u.removeFriend(task);
                Long id_prieten=u.getId();
                Tuple<Long,Long> tuple1=new Tuple(id,id_prieten);
                Tuple<Long,Long> tuple2=new Tuple(id_prieten,id);
                if(friendship_repo.delete(tuple1)==null)
                    friendship_repo.delete(tuple2);
            }
        graf.removeNod(id);
    }

    //service prietenie

    /*adauga prietenie
    * daca nu exista utilizatorii arunca exceptie
    * daca exista prietenie arunca exceptie*/
    public void addPrietenie(Prietenie p){
        Tuple<Long,Long> tuple=p.getId();
        Long id1=tuple.getLeft();
        Long id2=tuple.getRight();

        Utilizator u1=user_repo.findOne(id1);
        Utilizator u2=user_repo.findOne(id2);

        if(u1==null)
            throw new ServiceException("Utilizator inexistent");
        if(u2==null)
            throw new ServiceException("Utilizator inexistent");

        Tuple<Long,Long> inverse=new Tuple(tuple.getRight(),tuple.getLeft());

        if(friendship_repo.findOne(tuple)!=null || friendship_repo.findOne(inverse)!=null)
           throw new ServiceException("Prietenie existenta");

        friendship_repo.save(p);
        u1.addFriend(u2);
        u2.addFriend(u1);
        graf.addMuchie(id1,id2);
    }

    /*sterge prietenie
     * daca nu exista prietenie arunca exceptie*/
    public void removePrietenie(Prietenie p){
        Tuple<Long,Long> tuple=p.getId();
        Tuple<Long,Long> inverse=new Tuple(tuple.getRight(),tuple.getLeft());

        if(friendship_repo.findOne(tuple)==null && friendship_repo.findOne(inverse)==null)
            throw new ServiceException("Prietenie inexistenta");

        Long id1=tuple.getLeft();
        Long id2=tuple.getRight();

        Utilizator u1=user_repo.findOne(id1);
        Utilizator u2=user_repo.findOne(id2);

        if(friendship_repo.delete(tuple)==null)
            friendship_repo.delete(inverse);
        u1.removeFriend(u2);
        u2.removeFriend(u1);
        graf.removeMuchie(id1,id2);
    }

    public Iterable<Utilizator> getAllUsers(){ return user_repo.findAll(); }

    public Iterable<Prietenie> getAllFriendships(){ return friendship_repo.findAll(); }

    public Iterable<Message> getAllMessages(){return  message_repo.findAll();}

    public Graf getGraf() {
        return graf;
    }

    public String afisareComunitati(){
        graf.getcommunities();
        return graf.comunitatiToString();
    }

    public Integer nrComunitati(){
        Integer x= graf.getcommunities();
        if(x==0)
            throw new ServiceException("Nu exista comunitati");
        return x;
    }

    public Utilizator findUtilizator(Long id) {
        Utilizator utilizator=user_repo.findOne(id);
        for(Prietenie p:getAllFriendships()) {
            if (p.getStatus() != null) {
                if (p.getStatus().equals("accepted")) {
                    if (p.getId().getRight().equals(id)) {
                        utilizator.addFriend(user_repo.findOne(p.getId().getLeft()));
                    } else {
                        if ((p.getId().getLeft().equals(id))) {
                            utilizator.addFriend(user_repo.findOne(p.getId().getRight()));
                        }
                    }
                }
            }
        }
        return utilizator;
    }

    public Utilizator findUtilizatorVersion2(Long id){
        return user_repo.findOne(id);
    }

    public Prietenie findPrietenie(Long id1,Long id2){
        Tuple<Long,Long> id=new Tuple<>(id1,id2);
        Prietenie p=friendship_repo.findOne(id);
        if(p!=null)
            return p;
        else{
            Tuple<Long,Long> tuple=new Tuple<>(id.getRight(),id.getLeft());
            return friendship_repo.findOne(tuple);
        }
    }

    public Message findMessage(Long id){return message_repo.findOne(id);}

    public void sendFriendRequest(Long from,Long to){
        Utilizator u1=user_repo.findOne(from);
        Utilizator u2=user_repo.findOne(to);

        if(u1==null)
            throw new ServiceException("Utilizator inexistent");
        if(u2==null)
            throw new ServiceException("Utilizator inexistent");

        Tuple<Long,Long> tuple=new Tuple<>(from,to);
        Tuple<Long,Long> inverse=new Tuple(tuple.getRight(),tuple.getLeft());

        Prietenie p1=friendship_repo.findOne(tuple);
        Prietenie p2=friendship_repo.findOne(inverse);
        if(p1!=null)
            throw new ServiceException("Prietenie existenta: "+p1.getStatus());
        if(p2!=null)
            throw new ServiceException("Prietenie existenta: "+p2.getStatus());

        Prietenie p=new Prietenie();
        p.setId(tuple);
        p.setStatus("pending");
        friendship_repo.save(p);
    }

    public void accept_rejectFriendRequest(Long from,Long to, String decizie){
        Prietenie p=findPrietenie(from,to);
        if(!p.getStatus().equals("pending")){
            throw new ServiceException("Prietenie existenta. Status: "+p.getStatus());
        }
        Prietenie noua=new Prietenie();
        noua.setId(p.getId());
        noua.setStatus(decizie);
        friendship_repo.update(noua);
        notifyObservers();
    }

    public void addNewMessage(Message message){
        message_repo.save(message);
    }

    public void removeFriend(Long id1, Long id2){
        Prietenie p1=findPrietenie(id1,id2);
        if(p1!=null){
            accept_rejectFriendRequest(id1,id2,"rejected");
        }
        else {
            Prietenie p2 = findPrietenie(id2, id1);
            accept_rejectFriendRequest(id2,id1,"rejected");
        }
    }

    public void removeFriendVersion2(Long id1,Long id2){
        Prietenie p1=findPrietenie(id1,id2);
        if(p1!=null){
            Prietenie noua=new Prietenie();
            noua.setId(p1.getId());
            noua.setStatus("rejected");
            friendship_repo.update(noua);
        }
        else{
            Prietenie p2=findPrietenie(id2,id1);
            Prietenie noua=new Prietenie();
            noua.setId(p1.getId());
            noua.setStatus("rejected");
            friendship_repo.update(noua);
        }
    }

    private List<Observer> observers=new ArrayList<>();
    @Override
    public void addObserver(Observer e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers() {
        observers.stream().forEach(x->x.update());
    }
}
