package system.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import system.cinema.utils.Helper;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class TicketType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @Column(unique = true)
    private String type;

    //@TODO fix the price with two digits after the decimal
    @NotNull
    @Column(precision = 3, scale = 2)
    private float price;

    @OneToMany(mappedBy = "type")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<Ticket> tickets;

    public TicketType() { }

    public TicketType(TicketType.Unit unit)
    {
        this.type = unit.toString();
        this.price = unit.getPrice();
    }

    public TicketType(Integer id, TicketType.Unit unit)
    {
        this(unit);
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public enum Unit {
        STANDARD (12.00F),
        CHILDREN (9.00F),
        STUDENTS (9.00F),
        ELDERLY (9.00F);

        private float price;

        Unit(float price)
        {
            this.price = price;
        }

        public float getPrice()
        {
            return this.price;
        }

        @Override
        public String toString() {
            return Helper.normalizeStringSensitivity(super.toString(), false);
        }

    }
}
