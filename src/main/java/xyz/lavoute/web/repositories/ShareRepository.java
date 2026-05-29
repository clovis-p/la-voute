package xyz.lavoute.web.repositories;

import org.springframework.data.repository.CrudRepository;
import xyz.lavoute.web.models.Share;

public interface ShareRepository extends CrudRepository<Share, Integer> {
}
