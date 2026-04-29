package xyz.lavoute.web.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.User;

import java.util.Collection;

public interface FileRepository extends CrudRepository<File, Integer> {

    File findFileById(int fileId);

    Collection<File> findAllByParentDirIdAndUser(@Param("parentDirId") Integer parentDirId, @Param("user") User user);

    Collection<File> findAllByParentDirAndUser(@Param("ParentDir") File parentDir, @Param("user") User user);
}
