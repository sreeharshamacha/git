package com.notification.management.service;

import com.notification.management.dto.ChannelDTO;

import java.util.List;

public interface ChannelService {
    ChannelDTO addChannel(ChannelDTO request);

    List<ChannelDTO> listAllChannels();

    ChannelDTO getChannel(Long id);

    void deleteChannel(Long id);
}
