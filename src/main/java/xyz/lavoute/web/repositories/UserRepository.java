package xyz.lavoute.web.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import xyz.lavoute.web.models.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
