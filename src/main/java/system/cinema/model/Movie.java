package system.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import system.cinema.annotation.EndTimeBiggerThanStartTimeConstraint;
import system.cinema.annotation.ValidDateValueConstraint;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Entity
@EndTimeBiggerThanStartTimeConstraint
// @TODO Add constraint to forbid adding another movie with duration that collapses with another
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(unique = true)
    private String name;

    @Column(nullable = false)
    private Time startTime;

    @Column(nullable = false)
    private Time endTime;

    // @TODO Ensure everything works well with different timezones and eventual fixes..

    @Column(nullable = false)
    @ValidDateValueConstraint
    private Date startDate;

    @NotNull
    private Short rating;

    @OneToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Hall hall;

    @OneToMany(mappedBy = "movie")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<Ticket> tickets;

    @OneToOne
    @JsonIgnore
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MovieType type;

    public Movie() {}

    public Movie(String name, Time startTime, Time endTime, Date startDate, Short rating, Hall hall, MovieType type)
    {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.rating = rating;
        this.hall = hall;
        this.type = type;
    }

    public Movie(Integer id, String name, Time startTime, Time endTime, Date startDate, Short rating, Hall hall, MovieType type)
    {
        this(name, startTime, endTime, startDate, rating, hall, type);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public Short getRating() {
        return rating;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public MovieType getType() {
        return type;
    }

    public void setType(MovieType type) {
        this.type = type;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
