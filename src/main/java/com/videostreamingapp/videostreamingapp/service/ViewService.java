package com.videostreamingapp.videostreamingapp.service;

import com.videostreamingapp.videostreamingapp.model.Video;
import com.videostreamingapp.videostreamingapp.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ViewService {

    @Autowired
    private VideoRepository videoRepository;

    public void incrementViewCount(String videoId) {
        Optional<Video> videoOpt = videoRepository.findById(videoId);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            Long currentViews = video.getViewCount() != null ? video.getViewCount() : 0L;
            video.setViewCount(currentViews + 1);
            videoRepository.save(video);
        }
    }
}

