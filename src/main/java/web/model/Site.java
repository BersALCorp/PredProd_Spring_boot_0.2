package web.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "sites")
public class Site {
    @Id
    private String name;
    private String url;
    private boolean blocked;

    public Site(String name, String url, boolean blocked) {
        this.name = name;
        this.url = url;
        this.blocked = blocked;
    }
}
