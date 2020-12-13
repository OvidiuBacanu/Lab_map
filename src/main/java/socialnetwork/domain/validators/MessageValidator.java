package socialnetwork.domain.validators;

import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;

public class MessageValidator implements Validator<Message> {
        @Override
        public void validate(Message entity) throws ValidationException {
            String exception_message="";
            if(entity.getMessage()==null || entity.getMessage().equals(""))
                exception_message+="Mesaj null!\n";
            for(Utilizator u:entity.getTo()) {
                if (u.getId() == entity.getFrom().getId()) {
                    exception_message += "Eroare mesaj(nu poti sa iti dai tie mesaj)\n";
                }
            }
            if(entity.getTo().isEmpty())
                exception_message+="Lista de destinatari invalida";
            if(exception_message!="")
                throw new ValidationException(exception_message);
        }
}

