package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Message extends Entity<Long> {
    private Utilizator from;
    private List<Utilizator> to;
    private String message;
    private LocalDateTime data;
    private Message reply;

    public Message(Utilizator from, List<Utilizator> to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        data = LocalDateTime.now();
        reply=null;
    }

    public Message(Utilizator from, List<Utilizator> to, String message, Message reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        data = LocalDateTime.now();
        this.reply = reply;
    }

    @Override
    public String toString() {
        String rez="";
        rez+="from: "+getFrom().getFirstName()+" "+getFrom().getLastName()+"\n";
        String aux="";
        for(Utilizator u:getTo()){
            aux+=u.getFirstName()+" "+u.getLastName()+", ";
        }
        rez+="to: "+aux+"\n";
        rez+="message: "+getMessage()+"\n";
        return rez;
    }

    public Utilizator getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    public String getToAsString() {
        String rez="";
        for(Utilizator u:to){
            rez+=u.getId().toString()+",";
        }
        if(rez.endsWith(","))
            return rez.substring(0,rez.length()-1);
        return rez;
    }

    public boolean idInListTo(Long id){
        for(Utilizator u:getTo()){
            if(id==u.getId()) {
                return true;
            }
        }
        return false;
    }

    public String getFromAsString(){
        return from.getFirstName()+" "+from.getLastName();
    }

    public String getToAsString_NameVersion(){
        String rez="";
        for(Utilizator u:getTo()){
            rez+=u.getFirstName()+" "+u.getLastName()+"\n";
        }
        return rez;
    }
}
