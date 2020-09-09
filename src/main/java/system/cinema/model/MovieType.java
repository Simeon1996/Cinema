package system.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import system.cinema.utils.Helper;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
public class MovieType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @Column(unique = true)
    private String type;

    @OneToMany(mappedBy = "type")
    @JsonIgnore
    private List<Movie> movies;

    public MovieType() { };

    public MovieType(Unit unit)
    {
        this.type = unit.toString();
    }

    public MovieType(Integer id, Unit unit)
    {
        this(unit);
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(Unit unit) {
        this.type = unit.toString();
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public enum Unit {
        IMAX_2D,
        IMAX_3D,
        STANDARD_2D,
        STANDARD_3D;

        @Override
        public String toString() {
            return Helper.normalizeStringSensitivity(super.toString(), true);
        }
    }
}
