package xyz.lavoute.web.repositories;

import org.springframework.data.repository.CrudRepository;
import xyz.lavoute.web.models.FileType;

public interface FileTypeRepository extends CrudRepository<FileType, Integer> {
}
