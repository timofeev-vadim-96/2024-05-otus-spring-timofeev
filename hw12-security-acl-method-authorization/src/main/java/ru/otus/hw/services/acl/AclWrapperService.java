package ru.otus.hw.services.acl;

import org.springframework.security.acls.model.Permission;

public interface AclWrapperService {

    void createPermission(Object object, Permission permission);
}
