package xyz.lavoute.web.repositories;

import org.springframework.data.repository.CrudRepository;
import xyz.lavoute.web.models.Permission;

public interface PermissionRepository extends CrudRepository<Permission, Integer> {
}
