package ru.perm.v.fixedexecutors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
