package system.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "ENUM('ROLE_USER', 'ROLE_EDITOR', 'ROLE_ADMIN')", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role.Unit name;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private List<User> users;

    public Role() { }

    public Role(Role.Unit name)
    {
        this.name = name;
    }

    public Role(Integer id, Role.Unit name)
    {
        this(name);
        this.id = id;
    }

    /**
     * Whenever a value is changed/added or removed - the same needs to be done for the
     * column definition of `name` above so they are in sync.
     */
    public enum Unit
    {
        ROLE_USER, ROLE_EDITOR, ROLE_ADMIN;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name.toString();
    }

    public void setName(Role.Unit name) {
        this.name = name;
    }

    public String toString() {
        return this.getName();
    }
}
