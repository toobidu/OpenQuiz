package com.example.quizizz.service.Interface;

import com.example.quizizz.model.dto.room.InvitationResponse;
import com.example.quizizz.model.dto.room.InvitePlayerRequest;

import java.util.List;

public interface IInvitationService {
    InvitationResponse createInvitation(InvitePlayerRequest request, Long inviterId);
    InvitationResponse respondToInvitation(Long invitationId, boolean accept, Long userId);
    List<InvitationResponse> getUserInvitations(Long userId);
    void deleteInvitation(Long invitationId);
}