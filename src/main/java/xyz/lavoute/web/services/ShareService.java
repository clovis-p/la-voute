package xyz.lavoute.web.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.Permission;
import xyz.lavoute.web.models.Share;
import xyz.lavoute.web.repositories.ShareRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ShareService {
    private ShareRepository shareRepository;

    public Share saveShare(Share share) {
        return shareRepository.save(share);
    }

    public List<Share> findSharesByFile(File file) {
        return shareRepository.findSharesByFileId(file);
    }

    @Transactional
    public void deleteSharesByFile(File file) {
        shareRepository.deleteAllByFileId(file);
    }
}
