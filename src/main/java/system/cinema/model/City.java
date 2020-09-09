package system.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import system.cinema.utils.Helper;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "city")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<Cinema> cinemas;

    public City() {};

    public City(Unit unit) {
        this.name = unit.toString();
    }

    public City(Integer id, Unit unit)
    {
        this(unit);
        this.id = id;
    }

    public enum Unit {
        SOFIA,
        PLOVDIV,
        STARA_ZAGORA,
        SMOLYAN,
        BURGAS,
        VARNA,
        RUSE;

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
