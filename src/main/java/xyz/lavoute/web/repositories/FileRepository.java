package xyz.lavoute.web.repositories;

import org.springframework.data.repository.CrudRepository;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.User;

import java.util.Collection;

public interface FileRepository extends CrudRepository<File, Integer> {

    File findFileById(int fileId);

    Collection<File> findAllByParentDirAndUser(File parentDir, User user);
}
