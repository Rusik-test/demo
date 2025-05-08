package com.example.demo.service;

import com.example.demo.model.CommentEntity;

import java.util.List;

public interface CommentService {
    CommentEntity saveComment(String playerName , String comment);
    List<CommentEntity> getAllComments();
}
