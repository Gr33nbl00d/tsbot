package de.greenblood.tsbot.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface AuthoritiesRepository extends JpaRepository<Authorities, String> {
    @Modifying
    @Transactional
    @Query("delete from Authorities e where username = ?1")
    void deleteAllFromUserName(String username);
}
