package com.example.demo.service;

import com.example.demo.model.CommentEntity;
import com.example.demo.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class CommentServiceJPA implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceJPA(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public CommentEntity saveComment(String playerName, String comment) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setPlayerName(playerName);
        commentEntity.setComment(comment);
        commentEntity.setData(LocalDateTime.now());
        return commentRepository.save(commentEntity);

    }

    @Override
    public List<CommentEntity> getAllComments() {
        return commentRepository.findAll();
    }
}
