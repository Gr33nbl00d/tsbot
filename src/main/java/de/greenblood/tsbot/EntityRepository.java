package de.greenblood.tsbot;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRepository extends CrudRepository<Entity,Long>
{
}
