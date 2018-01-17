package de.beuth.clara.claraSoftware.domain.imports;

import java.util.List;
import java.util.Optional;

import de.beuth.clara.claraSoftware.domain.Article;

public interface ArticleRepository {
    /**Deletes all article. Useful for test scenarions in order to start with an empty article set.*/
    void deleteAll();

    /**Gives the article a unique, higher ID and saves the article.
     * @param Article Article the article to be saved to the repository
     * @return the modified instance*/
    Article save(Article Article);

    /**Deletes the given article.
     * @param Article Article the article to be deleted from the repository
     */
    void delete(Article Article);

    /**Returns the {@link Article} object with the given id, if existing.
     * @param id Long the id of the article to be found
     * @return an Optional object, regardless whether an article was found or not
     * @throws IllegalArgumentException  id is null
     */
    Optional<Article> find(Long id);

    /**Finds all {@link Article}s and returns them ordered by descending IDs.
     * @return a List of articles
     */
    List<Article> findAll();

}
