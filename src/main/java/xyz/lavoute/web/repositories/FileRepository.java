package xyz.lavoute.web.repositories;

import org.springframework.data.repository.CrudRepository;
import xyz.lavoute.web.models.File;

public interface FileRepository extends CrudRepository<File, Integer> {
}
