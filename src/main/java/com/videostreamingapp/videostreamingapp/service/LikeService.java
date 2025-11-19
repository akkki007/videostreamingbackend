package com.videostreamingapp.videostreamingapp.service;

import com.videostreamingapp.videostreamingapp.model.Like;
import com.videostreamingapp.videostreamingapp.model.Video;
import com.videostreamingapp.videostreamingapp.repository.LikeRepository;
import com.videostreamingapp.videostreamingapp.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private VideoRepository videoRepository;

    public boolean toggleLike(String videoId, String userId, String username) {
        Optional<Like> existingLike = likeRepository.findByVideoIdAndUserId(videoId, userId);
        
        if (existingLike.isPresent()) {
            // Unlike: Remove the like
            likeRepository.delete(existingLike.get());
            updateVideoLikeCount(videoId);
            return false; // Not liked anymore
        } else {
            // Like: Create new like
            Like like = new Like(videoId, userId, username);
            likeRepository.save(like);
            updateVideoLikeCount(videoId);
            return true; // Now liked
        }
    }

    public boolean isLiked(String videoId, String userId) {
        return likeRepository.existsByVideoIdAndUserId(videoId, userId);
    }

    public long getLikeCount(String videoId) {
        return likeRepository.countByVideoId(videoId);
    }

    private void updateVideoLikeCount(String videoId) {
        Optional<Video> videoOpt = videoRepository.findById(videoId);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            long likeCount = likeRepository.countByVideoId(videoId);
            video.setLikeCount(likeCount);
            videoRepository.save(video);
        }
    }
}

