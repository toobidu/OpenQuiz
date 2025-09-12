package com.example.quizizz.service.Interface;

import com.example.quizizz.model.dto.topic.CreateTopicRequest;
import com.example.quizizz.model.dto.topic.TopicResponse;
import com.example.quizizz.model.dto.topic.UpdateTopicRequest;

import java.util.List;

public interface ITopicService {
    TopicResponse create(CreateTopicRequest request);
    TopicResponse update(Long id, UpdateTopicRequest request);
    void delete(Long id);
    TopicResponse getById(Long id);
    List<TopicResponse> getAll();
}
