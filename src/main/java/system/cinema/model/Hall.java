package system.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(
    name = "hall",
    uniqueConstraints =  @UniqueConstraint(
        name = "hall_unique_hall_number_per_cinema",
        columnNames = {
            "hallNumber",
            "cinema_id"
        }
    )
)
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private Short hallNumber;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private HallType type;

    @NotNull
    private Short rows;

    @NotNull
    private Short seatsPerRow;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private Cinema cinema;

    @OneToMany(mappedBy = "hall")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<HallSlot> slots;

    @OneToOne(mappedBy = "hall")
    @JsonIgnore
    private Movie movie;

    public Hall() {}

    public Hall(Short hallNumber, Short rows, Short seatsPerRow, HallType type, Cinema cinema) {
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
        this.hallNumber = hallNumber;
        this.type = type;
        this.cinema = cinema;
    }

    public Hall(Integer id, Short hallNumber, Short rows, Short seatsPerRow, HallType type, Cinema cinema)
    {
        this(hallNumber, rows, seatsPerRow, type, cinema);
        this.id = id;
    }

    public HallType getType() {
        return type;
    }

    public void setType(HallType type) {
        this.type = type;
    }

    public List<HallSlot> getSlots() {
        return slots;
    }

    public Movie getMovie() {
        return movie;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }


    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    public Short getRows() {
        return rows;
    }

    public void setRows(Short rows) {
        this.rows = rows;
    }

    public Short getSeatsPerRow() {
        return seatsPerRow;
    }

    public void setSeatsPerRow(Short seatsPerRow) {
        this.seatsPerRow = seatsPerRow;
    }

    public Short getHallNumber() {
        return hallNumber;
    }

    public void setHallNumber(Short hallNumber) {
        this.hallNumber = hallNumber;
    }
}
