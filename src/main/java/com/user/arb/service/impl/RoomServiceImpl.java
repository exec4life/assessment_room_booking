package com.user.arb.service.impl;

import com.user.arb.exception.ArbException;
import com.user.arb.jpa.entity.Room;
import com.user.arb.jpa.repository.RoomRepository;
import com.user.arb.service.RoomService;
import com.user.arb.service.dto.RoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl extends AbstractServiceImpl<Room, RoomDTO, Long> implements RoomService {

    private RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository) {
        super(roomRepository);
        this.roomRepository = roomRepository;
    }

    @Override
    public RoomDTO create(RoomDTO roomDTO) {
        if (roomRepository.findByName(roomDTO.getName()).isPresent()) {
            throw new ArbException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("room.validation.name.conflict",
                            new Object[] { roomDTO.getName() },
                            "The room's name [{0}] is existed",
                            LocaleContextHolder.getLocale()));
        }
        return super.create(roomDTO);
    }

    @Override
    public RoomDTO update(RoomDTO roomDTO) {
        if (roomRepository.findByNameInOthers(roomDTO.getName(), roomDTO.getId()).isPresent()) {
            throw new ArbException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("room.validation.name.conflict",
                            new Object[] { roomDTO.getName() },
                            "The room's name [{0}] is existed",
                            LocaleContextHolder.getLocale()));
        }
        return super.update(roomDTO);
    }
}