package com.restaurant.hnks24cntt1it210tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.UserLoginDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.UserRegisterDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.User;

public interface AuthService {
    User register(UserRegisterDTO userRegisterDTO);
    User login(UserLoginDTO userLoginDTO);
}