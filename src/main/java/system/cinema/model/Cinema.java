package system.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import system.cinema.utils.Helper;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.*;

@Entity
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @Column(unique = true)
    private String name;

    @NotEmpty
    @Column(unique = true)
    private String address;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private Country country;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private City city;

    @JsonIgnore
    @OneToMany(mappedBy = "cinema")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Hall> halls = new ArrayList<>();

    Cinema() { }

    public Cinema(Cinema.Unit unit, City city, Country country)
    {
        this.name = unit.toString();
        this.address = unit.getAddress();
        this.city = city;
        this.country = country;
    }

    public Cinema(Integer id, Cinema.Unit unit, City city, Country country)
    {
        this(unit, city, country);
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Cinema cinema = (Cinema) o;
        return name.equals(cinema.name) &&
                address.equals(cinema.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, address);
    }

    // @TODO fix the unit so you can provide halls names explicitly
    // @TODO and change it so you can be able to specify rows and seats of a certain hall in case they have less
    // @TODO based on the construction of the hall
    public enum Unit {

        MALL_OF_SOFIA (Country.Unit.BULGARIA, City.Unit.SOFIA, "Somewhere around sofia", ImmutableMap.of(
            HallType.Unit.STANDARD, ImmutableMap.of("halls", (short) 10, "rows", (short) 10, "seats", (short) 10),
            HallType.Unit.IMAX, ImmutableMap.of("halls", (short) 1, "rows", (short) 15, "seats", (short) 20),
            HallType.Unit.FOURDX, ImmutableMap.of("halls", (short) 2, "rows", (short) 7, "seats", (short) 10)
        )),

        RING_MALL (Country.Unit.BULGARIA, City.Unit.SOFIA, "Somewhere around sofiaaa", ImmutableMap.of(
            HallType.Unit.STANDARD, ImmutableMap.of("halls", (short) 10, "rows", (short) 10, "seats", (short) 10),
            HallType.Unit.IMAX, ImmutableMap.of("halls", (short) 3, "rows", (short) 13, "seats", (short) 11)
        )),

        BULGARIA_MALL (Country.Unit.BULGARIA, City.Unit.SOFIA, "somewhere theereee", ImmutableMap.of(
            HallType.Unit.STANDARD, ImmutableMap.of("halls", (short) 10, "rows", (short) 10, "seats", (short) 10),
            HallType.Unit.IMAX, ImmutableMap.of("halls", (short) 2, "rows", (short) 12, "seats", (short) 12)
        ));

        String country, city, address;
        Map<HallType.Unit, Map<String, Short>> halls;

        Unit(Country.Unit country, City.Unit city, String address, Map<HallType.Unit, Map<String, Short>> halls)
        {
            this.country = country.toString();
            this.city = city.toString();
            this.address = address;
            this.halls = halls;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public String getCountry() {
            return country;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public Map<HallType.Unit, Map<String, Short>> getHalls()
        {
            return this.halls;
        }

        @Override
        public String toString()
        {
            return Helper.normalizeStringSensitivity(super.toString(), false);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(Cinema.Unit unit) {
        this.name = unit.toString();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(Cinema.Unit unit) {
        this.address = unit.getAddress();
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public List<Hall> getHalls() {
        return halls;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
