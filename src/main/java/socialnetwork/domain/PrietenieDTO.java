package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrietenieDTO {
    private String firstName;
    private String lastName;
    private LocalDateTime date;

    public PrietenieDTO(String firstName, String lastName, LocalDateTime date) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
    }

    public PrietenieDTO(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return firstName+"|"+lastName+"|"+date.format(Constants.DATE_TIME_FORMATTER).toString();
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String DatetoString() {
        return date.format(Constants.DATE_TIME_FORMATTER).toString();
    }


}
