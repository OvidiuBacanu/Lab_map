package socialnetwork.ui;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.Service;
import socialnetwork.service.ServiceException;

import java.util.*;

public class UI_Utilizator {
    Service service;
    Scanner in = new Scanner(System.in);
    Long id;

    public UI_Utilizator(Service service) {
        this.service = service;
    }

    public void show_meniu() {
        System.out.println("1. Add friend");
        System.out.println("2. Write new message");
        System.out.println("3. Show all friends");
        System.out.println("4. Show friends in a month");
        System.out.println("5. Check friendships requests");
        System.out.println("6. Check new messages");
        System.out.println("7. Show messages with a user");
        System.out.println("x. Back");
    }

    public boolean login() {
        Long id;
        System.out.print("Dati id-ul dumneavoastra: ");
        id = Long.parseLong(in.nextLine());
        Utilizator u = service.findUtilizator(id);
        if (u == null) {
            System.out.println("User inexistent");
            return false;
        } else {
            System.out.println("\nHello " + u.getFirstName() + " " + u.getLastName() + "\n");
            this.id = id;
            return true;
        }
    }

    public void sendFriendRequest() {
        Long id;
        System.out.print("Trimiteti cerere de prietenie utilizatorului cu id-ul: ");
        id = Long.parseLong(in.nextLine());
        try {
            service.sendFriendRequest(this.id, id);
            System.out.println("\nCerere trimisa\n");
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    public void meniu_accept_rejectFriendRequest(Utilizator u) {
        while (true) {
            System.out.print("Cerere de prietenie de la: ");
            System.out.println(u.getFirstName() + " " + u.getLastName());
            System.out.println("1. Acceptati cererea de prietenie");
            System.out.println("2. Refuzati cererea de prietenie");
            System.out.print("Dati raspuns: ");
            String raspuns = in.nextLine();
            if (raspuns.equals("1")) {
                service.accept_rejectFriendRequest(u.getId(), id, "accepted");
                System.out.println("Cerere acceptata");
                break;
            }
            if (raspuns.equals("2")) {
                service.accept_rejectFriendRequest(u.getId(), id, "rejected");
                System.out.println("Cerere respinsa");
                break;
            }
        }
    }

    public void accept_rejectFriendRequest() {
        for (Prietenie p : service.getAllFriendships()){
            if (p.getId().getLeft() == id) {
                if (p.getStatus().equals("pending")) {
                    Utilizator u = service.findUtilizator(p.getId().getRight());
                    meniu_accept_rejectFriendRequest(u);
                }
            }
            else {
                if (p.getId().getRight() == id) {
                    if (p.getStatus().equals("pending")) {
                        Utilizator u = service.findUtilizator(p.getId().getLeft());
                        meniu_accept_rejectFriendRequest(u);
                    }
                }
            }
        }
    }

    public void showAllFriends(){
        Set<Prietenie> list=(Set)service.getAllFriendships();
        list.stream()
                .filter(x->x.getId().getRight()==id && x.getStatus().equals("accepted"))
                .map(x->new PrietenieDTO(service.findUtilizator(x.getId().getLeft()).getFirstName(),
                        service.findUtilizator(x.getId().getLeft()).getLastName(),
                        service.findPrietenie(id,x.getId().getLeft()).getDate()))
                .forEach(System.out::println);
        list.stream()
                .filter(x->x.getId().getLeft()==id && x.getStatus().equals("accepted"))
                .map(x->new PrietenieDTO(service.findUtilizator(x.getId().getRight()).getFirstName(),
                        service.findUtilizator(x.getId().getRight()).getLastName(),
                        service.findPrietenie(id,x.getId().getRight()).getDate()))
                .forEach(System.out::println);
    }

    public void showMonthlyFriends(){
        String luna;
        System.out.print("Dati luna: ");
        luna=in.nextLine();

        String an;
        System.out.print("Dati an: ");
        an=in.nextLine();

        Long luna1=Long.parseLong(luna);
        Integer an1=Integer.parseInt(an);

        if(luna1<1 || luna1>12)
            System.out.println("Luna trebuie sa fie intre 1 si 12");
        Set<Prietenie> list=(Set)service.getAllFriendships();
        list.stream()
                .filter(x->x.getId().getRight()==id && x.getStatus().equals("accepted"))
                .map(x->new PrietenieDTO(service.findUtilizator(x.getId().getLeft()).getFirstName(),
                        service.findUtilizator(x.getId().getLeft()).getLastName(),
                        service.findPrietenie(id,x.getId().getLeft()).getDate()))
                .filter(x->x.DatetoString().substring(5,7).equals(luna) && x.getDate().getYear()==an1)
                .forEach(System.out::println);
        list.stream()
                .filter(x->x.getId().getLeft()==id && x.getStatus().equals("accepted"))
                .map(x->new PrietenieDTO(service.findUtilizator(x.getId().getRight()).getFirstName(),
                        service.findUtilizator(x.getId().getRight()).getLastName(),
                        service.findPrietenie(id,x.getId().getRight()).getDate()))
                .filter(x->x.DatetoString().substring(5,7).equals(luna)&& x.getDate().getYear()==an1)
                .forEach(System.out::println);
    }

    public void sendNewMessage(){
        System.out.println("Tastati send dupa ce ati introdus destinatarii mesajului");
        System.out.println("Dati id-urile utilizatorilor destinatari: ");

        Utilizator from=service.findUtilizator(id);
        List<Utilizator> to=new ArrayList<>();

        List<Long> esec=new ArrayList<>();
        String linie=in.nextLine();
        List<String> atribute= Arrays.asList(linie.split(","));
        for(String atribut:atribute){
            try{
                Long id=Long.parseLong(atribut);
                Utilizator to_utilizator= service.findUtilizator(id);
                if(to_utilizator!=null)
                    to.add(to_utilizator);
                else
                    esec.add(id);
            }catch (NumberFormatException e){}
        }

        if(!esec.isEmpty())
            System.out.println("Urmatoarele id-uri introduse sunt invalide: "+esec.toString());
        else {
            String messageString;
            System.out.println("Dati mesaj: ");
            messageString = in.nextLine();
            Message message = new Message(from, to, messageString);

            try {
                service.addNewMessage(message);
                System.out.println("Mesaj trimis");
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void replyToNewMessages(){
        for(Message m:service.getAllMessages()){
            if(m.idInListTo(id)==true){
                System.out.println("Mesaj nou:");
                System.out.println(m.toString());
                System.out.println("1. Raspundeti la mesaj");
                System.out.println("2. Ignorati");
                System.out.print("Dati comanda: ");
                String raspuns = in.nextLine();
                if(raspuns.equals("1")){
                    String messageString;
                    System.out.println("Dati mesaj: ");
                    messageString = in.nextLine();
                    List<Utilizator>to=new ArrayList<>();
                    to.add(m.getFrom());
                    Utilizator from= service.findUtilizator(id);
                    Message message = new Message(from, to, messageString,m);
                    try {
                        service.addNewMessage(message);
                        System.out.println("Mesaj trimis");
                    } catch (ValidationException e) {
                        System.out.println(e.getMessage());
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
                if(raspuns.equals("2")){
                    System.out.println("Mesaj ignorat");
                }
            }
        }
    }

    public void showMessages(){
        Long id;
        System.out.print("Dati id-ul unui utilizator pentru a vedea mesajele cu acesta: ");
        id = Long.parseLong(in.nextLine());
        Utilizator coleg= service.findUtilizator(id);
        if(coleg==null){
            System.out.println("Utilizator inexistent");
        }
        else{
            List<Message> list=new ArrayList<>();
            for(Message m:service.getAllMessages()){
                if((m.getFrom().getId()==this.id && m.idInListTo(id)) || (m.getFrom().getId()==id && m.idInListTo(this.id))){
                    list.add(m);
                }
            }
            Collections.sort(list, Comparator.comparing(message -> message.getData()));
            for(Message m:list){
                System.out.println(m.toString());
            }
        }
    }


    public void run(){
        if(login()==true) {
            while (true) {
                String cmd;
                show_meniu();
                System.out.print("\nDati comanda: ");
                cmd = in.nextLine();
                if (cmd.equals("1"))
                    sendFriendRequest();
                if(cmd.equals("2"))
                    sendNewMessage();
                if (cmd.equals("3"))
                    showAllFriends();
                if(cmd.equals("4"))
                    showMonthlyFriends();
                if(cmd.equals("5"))
                    accept_rejectFriendRequest();
                if(cmd.equals("6"))
                    replyToNewMessages();
                if(cmd.equals("7"))
                    showMessages();
                if (cmd.equals("x"))
                    break;
            }
        }
    }
}
