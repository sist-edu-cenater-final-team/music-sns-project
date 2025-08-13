package com.github.musicsnsproject.repository.jpa.community.comment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {

}
