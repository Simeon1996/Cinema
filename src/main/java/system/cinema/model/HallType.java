package system.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import system.cinema.utils.Helper;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
public class HallType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @Column(unique = true)
    private String type;

    @OneToMany(mappedBy = "type")
    @JsonIgnore
    private List<Hall> halls;

    public HallType() { }

    public HallType(HallType.Unit unit)
    {
        this.type = unit.toString();
    }

    public HallType(Integer id, HallType.Unit unit)
    {
        this(unit);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Hall> getHalls() {
        return halls;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public enum Unit
    {
        STANDARD,
        IMAX,
        FOURDX;

        @Override
        public String toString()
        {
            return Helper.normalizeStringSensitivity(super.toString(), true);
        }
    }
}
