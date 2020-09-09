package system.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import system.cinema.annotation.ContactNameConstraint;
import system.cinema.annotation.ContactNumberConstraint;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(
    name = "ticket",
    uniqueConstraints =  @UniqueConstraint(
        name = "ticket_unique_email",
        columnNames = {
            "email",
        }
    )
)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ContactNameConstraint
    private String belongsTo;

    @Email
    @NotEmpty
    private String email;

    @ContactNumberConstraint
    private String phone;

    @NotNull
    private Short row;

    @NotNull
    private Short seat;

    @NotNull
    @Min(value = 16)
    private Short age;

    @JsonIgnore
    @JoinColumn(nullable = false)
    @ManyToOne
    private TicketType type;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private Movie movie;

    public Ticket() { }

    public Ticket(
        String belongsTo,
        String email,
        String phone,
        Short row,
        Short seat,
        Short age,
        TicketType type,
        Movie movie
    ) {
        this.belongsTo = belongsTo;
        this.email = email;
        this.phone = phone;
        this.row = row;
        this.seat = seat;
        this.age = age;
        this.type = type;
        this.movie = movie;
    }

    public Ticket(Integer id, String belongsTo, String email, String phone, Short row, Short seat, Short age, TicketType type, Movie movie)
    {
        this(belongsTo, email, phone, row, seat, age, type, movie);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(String belongsTo) {
        this.belongsTo = belongsTo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSeat(Short seat) {
        this.seat = seat;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Short getAge() {
        return age;
    }

    public void setAge(Short age) {
        this.age = age;
    }

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }
}
