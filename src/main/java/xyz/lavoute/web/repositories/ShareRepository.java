package xyz.lavoute.web.repositories;

import org.springframework.data.repository.CrudRepository;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.Permission;
import xyz.lavoute.web.models.Share;

import java.util.Collection;
import java.util.List;

public interface ShareRepository extends CrudRepository<Share, Integer> {
    // The field is named fileId but the type is File
    List<Share> findSharesByFileId(File file);
    void deleteAllByFileId(File file);

    // The field is named permsId but the type is Permission
    void deleteAllByPermsIdIn(Collection<Permission> permissions);
}
