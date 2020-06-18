package it.auties.hosting.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import it.auties.hosting.model.Location;
import it.auties.hosting.model.Plan;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@Table(name = "servers")
public class Server extends PanacheEntity {
    private Location location;
    private Plan plan;

    public Server(Location location, Plan plan) {
        this.location = location;
        this.plan = plan;
    }
}
