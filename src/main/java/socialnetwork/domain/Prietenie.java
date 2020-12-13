package socialnetwork.domain;

import socialnetwork.utils.Constants;
import java.time.LocalDateTime;

public class Prietenie extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;
    public String status;

    public Prietenie() {
        date=LocalDateTime.now();
    }

    public Prietenie(String status) {
        this.status = status;
        date=LocalDateTime.now();
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return getId().toString();
    }
}
