package xyz.lavoute.web.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.lavoute.web.models.Permission;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.PermissionRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class PermissionService {
    private PermissionRepository permissionRepository;

    public Permission savePermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    public List<Permission> getUserFilePermissions(User user) {
        return permissionRepository.findPermissionsByUser_Id(user.getId());
    }
}
