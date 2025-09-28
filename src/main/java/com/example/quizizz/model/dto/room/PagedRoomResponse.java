package com.example.quizizz.model.dto.room;

import lombok.Data;
import java.util.List;

@Data
public class PagedRoomResponse {
    private List<RoomResponse> rooms;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
}