package system.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(
    name = "hall_slot",
    uniqueConstraints =  @UniqueConstraint(
        name = "slot_per_hall",
        columnNames = {
            "hall_id",
            "row",
            "seat"
        }
    )
)
public class HallSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private Hall hall;

    @NotNull
    private Short row;

    @NotNull
    private Short seat;

    public HallSlot() {}

    public HallSlot(Hall hall, Short row, Short seat)
    {
        this.hall = hall;
        this.row = row;
        this.seat = seat;
    }

    public HallSlot(Integer id, Hall hall, Short row, Short seat)
    {
        this(hall, row, seat);
        this.id = id;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public Short getRow() {
        return row;
    }

    public void setRow(Short row) {
        this.row = row;
    }

    public Short getSeat() {
        return seat;
    }

    public void setSeat(Short seat) {
        this.seat = seat;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
