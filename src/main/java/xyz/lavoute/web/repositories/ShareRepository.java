package xyz.lavoute.web.repositories;

import org.springframework.data.repository.CrudRepository;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.Share;

import java.util.List;

public interface ShareRepository extends CrudRepository<Share, Integer> {
    // The field is named fileId but the type is File
    List<Share> findSharesByFileId(File file);
}
