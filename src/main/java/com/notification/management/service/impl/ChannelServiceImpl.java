package com.notification.management.service.impl;

import com.notification.management.dto.ChannelDTO;
import com.notification.management.entity.Channel;
import com.notification.management.repository.ChannelRepository;
import com.notification.management.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;

    @Override
    @Transactional
    public ChannelDTO addChannel(ChannelDTO request) {
        Channel channel = Channel.builder()
                .type(request.getChannelType())
                .status(request.getStatus())
                .isDelete("N")
                .build();

        Channel saved = channelRepository.save(channel);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelDTO> listAllChannels() {
        return channelRepository.findAll().stream()
                .filter(c -> !"Y".equals(c.getIsDelete()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDTO getChannel(Long id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Channel not found with id: " + id));
        return mapToResponse(channel);
    }

    @Override
    @Transactional
    public void deleteChannel(Long id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Channel not found with id: " + id));
        channel.setIsDelete("Y");
        channelRepository.save(channel);
    }

    private ChannelDTO mapToResponse(Channel channel) {
        return ChannelDTO.builder()
                .channelId(channel.getId())
                .channelType(channel.getType())
                .status(channel.getStatus())
                .isDelete(channel.getIsDelete())
                .createdBy(channel.getCreatedBy())
                .updatedBy(channel.getUpdatedBy())
                .createdDate(channel.getCreatedDate())
                .updatedDate(channel.getUpdatedDate())
                .build();
    }
}
