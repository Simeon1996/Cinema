package system.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import system.cinema.utils.Helper;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
public class Country {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @NotEmpty
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "country")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<Cinema> cinemas;

    public Country() {};

    public Country(Unit unit) {
        this.name = unit.toString();
    }

    public Country (Integer id, Unit unit)
    {
        this(unit);
        this.id = id;
    }

    public enum Unit {
        BULGARIA;

        @Override
        public String toString() {
            return Helper.normalizeStringSensitivity(super.toString(), false);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(Unit unit) {
        this.name = unit.toString();
    }
}
