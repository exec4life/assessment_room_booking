package com.user.arb.service.impl;

import com.user.arb.jpa.entity.User;
import com.user.arb.jpa.repository.UserRepository;
import com.user.arb.service.UserService;
import com.user.arb.service.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends AbstractServiceImpl<User, UserDTO, Long> implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        super(userRepository);
        this.userRepository = userRepository;
    }
}