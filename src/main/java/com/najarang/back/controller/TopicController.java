package com.najarang.back.controller;

import com.najarang.back.dto.TopicDTO;
import com.najarang.back.entity.Topic;
import com.najarang.back.model.response.CommonResult;
import com.najarang.back.model.response.ListResult;
import com.najarang.back.model.response.SingleResult;
import com.najarang.back.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/topics")
public class TopicController {

    private final TopicService topicService;

    @GetMapping()
    public ListResult<Topic> findAllTopic(final Pageable pageable) {
        return topicService.list(pageable);
    }

    @PostMapping()
    public SingleResult<Topic> save(@RequestBody TopicDTO topic) {
        return topicService.save(topic);
    }

    @PutMapping(value = "/{id}")
    public SingleResult<Topic> modify(@RequestBody TopicDTO topic, @PathVariable long id) {
        topic.setId(id);
        return topicService.modify(topic);
    }

    @DeleteMapping(value = "/{id}")
    public CommonResult delete(@PathVariable long id) {
        return topicService.delete(id);
    }
}
