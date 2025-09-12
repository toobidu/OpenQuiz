package com.example.quizizz.service.Interface;

import com.example.quizizz.model.dto.room.InvitePlayerRequest;
import com.example.quizizz.model.dto.room.InvitationResponse;
import com.example.quizizz.model.dto.room.RoomResponse;

import java.util.List;

/**
 * Service đơn giản cho invitation sử dụng Redis thay vì database
 */
public interface IInvitationService {
    
    /**
     * Tạo lời mời (lưu trong Redis với TTL)
     */
    String createInvitation(InvitePlayerRequest request, Long inviterId);
    
    /**
     * Phản hồi lời mời
     */
    RoomResponse respondToInvitation(String invitationId, boolean accept, Long userId);
    
    /**
     * Lấy danh sách lời mời của user
     */
    List<InvitationResponse> getUserInvitations(Long userId);
}