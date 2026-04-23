package org.example.incomeandexpensebackend.services.interfaces;


import org.example.incomeandexpensebackend.dtos.user.*;
import org.example.incomeandexpensebackend.services.base_services.*;

public interface UserService extends Addable<CreateUserDto>,
        Modifiable<UpdateUserDto, Long>,
        FindAll<UserListingDto>,
        FindById<UserDetailsDto,
                Long>, Removable<Long> {
    UpdateSelfDto updateSelf(UpdateSelfDto dto);
}