package xyz.lavoute.web.repositories;

import org.springframework.data.repository.CrudRepository;
import xyz.lavoute.web.models.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends CrudRepository<Permission, Integer> {
    List<Permission> findPermissionsByUser_Id(int userId);
}
