package xyz.lavoute.web.repositories;

import org.springframework.data.repository.CrudRepository;
import xyz.lavoute.web.models.Share;

import java.util.List;

public interface ShareRepository extends CrudRepository<Share, Integer> {
    List<Share> getSharesByFileId(int fileId);
}
