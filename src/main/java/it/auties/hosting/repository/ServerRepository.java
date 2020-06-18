package it.auties.hosting.repository;

import it.auties.hosting.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server, Long> {

}
