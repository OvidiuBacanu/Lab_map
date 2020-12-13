package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long>{
    private String firstName;
    private String lastName;
    private List<Utilizator> friends;

    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        friends=new ArrayList<Utilizator>();
    }
    public Utilizator(Utilizator other){
        this.firstName=other.firstName;
        this.lastName=other.lastName;
        this.setId(other.getId());
    }

    public Utilizator() { }

    public Utilizator addFriend(Utilizator utilizator){
        if(friends.contains(utilizator))
            return null;
        else
            friends.add(utilizator);
        return  utilizator;
    }

    public Utilizator removeFriend(Utilizator utilizator){
        if(friends.contains(utilizator)) {
            friends.remove(utilizator);
            return utilizator;
        }
        return null;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Utilizator> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        return "Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + listaToString()+
                '}';
    }

    public String listaToString(){
        String rez="";
        for(Utilizator u:friends){
            rez+=u.firstName+" "+u.lastName+", ";
        }
        return rez;
    }

    public boolean suntPrieteni(Utilizator utilizator) {
        for(Utilizator u:getFriends())
            if(utilizator.getId().equals(u.getId()) && utilizator.getFirstName().equals(u.getFirstName()) && utilizator.getLastName().equals(u.getLastName()))
                return true;
       return false;
    }

    public void deleteFriend(Utilizator utilizator){
        for(Utilizator u:getFriends())
            if(utilizator.getId().equals(u.getId()) && utilizator.getFirstName().equals(u.getFirstName()) && utilizator.getLastName().equals(u.getLastName())){
                friends.remove(u);
                break;
            }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}