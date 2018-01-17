package de.beuth.clara.claraSoftware.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.beuth.clara.claraSoftware.domain.Article;
import de.beuth.clara.claraSoftware.domain.imports.ArticleRepository;
import de.beuth.clara.claraSoftware.infrastructure.imports.ImportedArticleJpaRepository;

@Service
public class ArticleJpaRepository implements ArticleRepository {

	private final ImportedArticleJpaRepository impl;

	@Autowired
	public ArticleJpaRepository(final ImportedArticleJpaRepository impl) {
		this.impl = impl;
	}

	@Override
	public void deleteAll() {
		impl.deleteAll();
	}

	@Override
	public Article save(final Article article) {
		return impl.save(article);
	}

	@Override
	public void delete(Article articlel) {
		impl.delete(articlel);
	}

	@Override
	public Optional<Article> find(Long id) {
		return impl.findOneById(id);
	}

	@Override
	public List<Article> findAll() {
		return impl.findAllByOrderByIdDesc();
	}

}
