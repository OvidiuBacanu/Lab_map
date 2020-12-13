package socialnetwork.ui;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.Service;
import socialnetwork.service.ServiceException;

import java.util.Scanner;

public class UI_Admin {
    Service service;
    Scanner in=new Scanner(System.in);

    public UI_Admin(Service service) {
        this.service = service;
    }

    public void show_meniu(){
        System.out.println("1. Add utilizator");
        System.out.println("2. Remove utilizator");
        System.out.println("3. Add prietenie");
        System.out.println("4. Remove prietenie");
        System.out.println("5. Show number of communities");
        System.out.println("6. Show the most sociable community");
        System.out.println("7. Afisare utilizatori");
        System.out.println("8. Afisare prietenii");
        System.out.println("x. Exit");
    }

    public void addUtilizator(){
        String firstName,lastName;
        System.out.print("Dati nume: ");
        firstName=in.nextLine();
        System.out.print("Dati prenume: ");
        lastName=in.nextLine();
        try{
            Utilizator x=new Utilizator(firstName,lastName);
            service.addUtilizator(x);
            System.out.println("\nUtilizatorul "+firstName+" "+lastName+" a fost adaugat\n");
        }
        catch (ValidationException e){
            System.out.println(e.getMessage());
        }
        catch (ServiceException e){
            System.out.println(e.getMessage());
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void removeUtilizator(){
        Long id;
        System.out.print("Dati id: ");
        id = Long.parseLong(in.nextLine());
        try {
            service.removeUtilizator(id);
            System.out.println("\nUtilizatorul cu id-ul "+id+" a fost sters\n");
        }
        catch (ServiceException e){
            System.out.println(e.getMessage());
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void addPrietenie() {
        Long id1,id2;
        System.out.print("Dati id pentru primul utilizator: ");
        id1=Long.parseLong(in.nextLine());
        System.out.print("Dati id pentru al doilea utilizator: ");
        id2=Long.parseLong(in.nextLine());
        Tuple<Long,Long> tuple=new Tuple(id1,id2);
        Prietenie p=new Prietenie();
        p.setId(tuple);
        try{
            service.addPrietenie(p);
            System.out.println("\nPrietenie adaugata\n");
        }
        catch (ValidationException e){
            System.out.println(e.getMessage());
        }
        catch (ServiceException e){
            System.out.println(e.getMessage());
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void removePrietenie(){
        Long id1,id2;
        System.out.print("Dati id pentru primul utilizator: ");
        id1=Long.parseLong(in.nextLine());
        System.out.print("Dati id pentru al doilea utilizator: ");
        id2=Long.parseLong(in.nextLine());
        Tuple<Long,Long> tuple=new Tuple(id1,id2);
        Prietenie p=new Prietenie();
        p.setId(tuple);
        try{
            service.removePrietenie(p);
            System.out.println("\nPrietenie stearsa\n");
        }
        catch (ServiceException e){
            System.out.println(e.getMessage());
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void afisareNrComunitati(){
        try{
            Integer x=service.nrComunitati();
            System.out.println("Nr comunitati: "+x.toString());
        }
        catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    public void run(){
        while (true){
            String cmd;
            show_meniu();
            System.out.print("\nDati comanda: ");
            cmd=in.nextLine();
            if(cmd.equals("1"))
                addUtilizator();
            if(cmd.equals("2"))
                removeUtilizator();
            if(cmd.equals("3"))
                addPrietenie();
            if(cmd.equals("4"))
                removePrietenie();
            if(cmd.equals("5"))
                afisareNrComunitati();
            if(cmd.equals("6"))
                System.out.println(service.afisareComunitati());
            if(cmd.equals("7"))
                service.getAllUsers().forEach(System.out::println);
            if(cmd.equals("8"))
                service.getAllFriendships().forEach(System.out::println);
            if(cmd.equals("x"))
                break;

        }
    }
}
