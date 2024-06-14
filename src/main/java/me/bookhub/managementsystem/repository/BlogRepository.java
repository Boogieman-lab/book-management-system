package me.bookhub.managementsystem.repository;

import me.bookhub.managementsystem.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {
}
