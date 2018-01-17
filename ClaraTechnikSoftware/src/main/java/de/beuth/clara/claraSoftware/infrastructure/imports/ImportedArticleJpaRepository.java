package de.beuth.clara.claraSoftware.infrastructure.imports;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.beuth.clara.claraSoftware.domain.Article;

/**
 * 
 * @author Ray Koeller
 */
public interface ImportedArticleJpaRepository extends JpaRepository<Article, Long> {

    /**Deletes all Users. Useful for test scenarions in order to start with an empty User set*/
    void deleteAll();

    Article save(Article Article);

    void delete(Article Article);
    
    Optional<Article> findOneById(Long id);
    
    List<Article> findAllByOrderByIdDesc();
}
