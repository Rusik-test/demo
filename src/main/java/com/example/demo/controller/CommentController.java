package com.example.demo.controller;

import com.example.demo.model.CommentEntity;
import com.example.demo.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/comment")
@CrossOrigin(origins = "*")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    @PostMapping("/addcomment")
    public CommentEntity addComment(@RequestBody CommentEntity commentEntity) {
        return commentService.saveComment(commentEntity.getPlayerName(), commentEntity.getComment());
    }
    @GetMapping("/getallcomment")
    public List<CommentEntity> getAllComment() {
        return commentService.getAllComments();
    }
}
