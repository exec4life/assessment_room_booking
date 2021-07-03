package com.user.arb.service.impl;

import com.user.arb.jpa.entity.Room;
import com.user.arb.jpa.repository.RoomRepository;
import com.user.arb.service.RoomService;
import com.user.arb.service.dto.RoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl extends AbstractServiceImpl<Room, RoomDTO, Long> implements RoomService {

    private RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository) {
        super(roomRepository);
        this.roomRepository = roomRepository;
    }
}