package de.beuth.clara.claraSoftware.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.beuth.clara.claraSoftware.domain.ArticleService.AmountExc;
import de.beuth.clara.claraSoftware.domain.ArticleService.ArticleNameExc;
import de.beuth.clara.claraSoftware.domain.ArticleService.ArticleNotInDatabaseExc;
import de.beuth.clara.claraSoftware.domain.ArticleService.DescriptionExc;
import de.beuth.clara.claraSoftware.domain.ArticleService.NameExc;


/** Test driver for the service class {@linkplain ArticleService}
 * @author Ray Koeller
 */
@RunWith(SpringRunner.class)
// @DataJpaTest
// @AutoConfigureTestDatabase
@SpringBootTest
public class ArticleServiceTest {

	@Autowired
	private ArticleService articleService;
	final private Amount priceTag = new Amount(999);

	@Before
	public void cleanUp() {
		articleService.deleteAll();
	}

	@Test
	public void createArticleExceptions() {

		// name can not be null
		try {
			articleService.createArticle(null, "Laptop from HP", priceTag);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}

		// description can not be null
		try {
			articleService.createArticle("HP Pavillion", null, priceTag);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}

		// price can not be null
		try {
			articleService.createArticle("HP Pavillion", "Laptop from HP", null);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}

		// name correct? (name longer then allowed)
		try {
			articleService.createArticle("HP Pavillion but this name is way to long", "Laptop from HP", priceTag);
			fail("NameExc expected");
		} catch (NameExc expected) {
		}

		// description correct? (description longer then allowed)
		try {
			articleService.createArticle("HP Pavillion",
					"Laptop from HP but now is the descritpion longer then its allowed to be. "
							+ "I mean descriptions that long make no sense at all..",
					priceTag);
			fail("DescriptionExc expected");
		} catch (DescriptionExc expected) {
		}

		// price correct? (price is negative)
		try {
			articleService.createArticle("HP Pavillion", "Laptop from HP 2017", new Amount(-999));
			fail("AmountExc expected");
		} catch (AmountExc expected) {
		}

		// name already in use?
		try {
			articleService.createArticle("HP Pavillion", "Laptop from HP", priceTag);
			articleService.createArticle("HP Pavillion", "Laptop from HP - 2016", priceTag);
			fail("ArticleNameExc expected");
		} catch (ArticleNameExc expected) {
		}
	}

	@Test
	public void deleteArticleExceptions() {
		final Article article = articleService.createArticle("HP Pavillion", "Laptop from HP", priceTag);
		try {
			articleService.deleteArticle(article.getId());
			articleService.deleteArticle(article.getId()); // Trying to delete the article that has already been deleted
			fail("ArticleNotInDatabaseExc expected");
		} catch (ArticleNotInDatabaseExc expected) {
		}
	}

	@Test
	public void findArticleExceptions() {

		try {
			// negative ID not allowed
			articleService.findArticle(-3L);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}
		try {
			// ID which is not corresponding with an article
			articleService.findArticle(234567L);
			fail("ArticleNotInDatabaseExc expected");
		} catch (ArticleNotInDatabaseExc expected) {
		}
	}

	@Test
	public void editArticleExceptions() {

		final Article article = articleService.createArticle("HP Pavillion", "Laptop from HP", priceTag);
		final Long id = article.getId();

		// editing article, but name is null
		try {
			articleService.editArticle(id, null, "Laptop from HP", priceTag);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}

		// editing article, but description is null
		try {
			articleService.editArticle(id, "HP Pavillion", null, priceTag);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}

		// editing article, but amount is null
		try {
			articleService.editArticle(id, "HP Pavillion", "Laptop from HP", null);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}

		// editing article, but with an already existing articlename
		try {
			articleService.editArticle(id, "HP Pavillion", "Laptop from HP", priceTag);
			fail("ArticleNameExc expected");
		} catch (ArticleNameExc expected) {
		}

		// name too long
		try {
			articleService.editArticle(id, "too long to be allowed in clara software, sorry", "Laptop from HP", priceTag);
			fail("NameExc expected");
		} catch (NameExc expected) {
		}
		// name too short
		try {
			articleService.editArticle(id, "s", "Laptop from HP", priceTag);
			fail("NameExc expected");
		} catch (NameExc expected) {
		}
		// description too long
		try {
			articleService.editArticle(id, "this name", "Laptop from HP but now is the descritpion longer then its allowed to be. "
					+ "I mean descriptions that long make no sense at all..", priceTag);
			fail("DescriptionExc expected");
		} catch (DescriptionExc expected) {
		}

		// description too short
		try {
			articleService.editArticle(id, "this name", "s", priceTag);
			fail("DescriptionExc expected");
		} catch (DescriptionExc expected) {
		}

		// Amount is negative
		try {
			articleService.editArticle(id, "this name", "Laptop from HP .", new Amount(-999));
			fail("AmountExc expected");
		} catch (AmountExc expected) {
		}
		// trying sucessful edit
		articleService.editArticle(id, "this name", "Laptop from HP .", new Amount (80));
	}

	@Test
	public void patchArticleExceptions() {

		final Article article = articleService.createArticle("HP Pavillion", "Laptop from HP", priceTag);
		final Long id = article.getId();

		// editing article, but with description too short
		try {
			articleService.patchArticle(id, "HP Pavillion 2", "Laptop", priceTag);
			fail("DescriptionExc expected");
		} catch (DescriptionExc expected) {
		}
		// editing article, but with an already existing articlename
		try {
			articleService.patchArticle(id, "HP Pavillion", "Laptop from HP", priceTag);
			fail("ArticleNameExc expected");
		} catch (ArticleNameExc expected) {
		}

		// name too long
		try {
			articleService.patchArticle(id, "too long to be allowed in clara software, sorry", "Laptop from HP", priceTag);
			fail("NameExc expected");
		} catch (NameExc expected) {
		}
		// name too short
		try {
			articleService.patchArticle(id, "s", "Laptop from HP", priceTag);
			fail("NameExc expected");
		} catch (NameExc expected) {
		}
		// description too long
		try {
			articleService.patchArticle(id, "this name", "Laptop from HP but now is the descritpion longer then its allowed to be. "
					+ "I mean descriptions that long make no sense at all..", priceTag);
			fail("DescriptionExc expected");
		} catch (DescriptionExc expected) {
		}

		// description too short
		try {
			articleService.patchArticle(id, "this name", "s", priceTag);
			fail("DescriptionExc expected");
		} catch (DescriptionExc expected) {
		}

		// Amount is negative
		try {
			articleService.patchArticle(id, "this name", "Laptop from HP .", new Amount(-999));
			fail("AmountExc expected");
		} catch (AmountExc expected) {
		}
		
		// try successful patches
		try {
			articleService.patchArticle(id, null,null,null);
		} catch (Exception e) {
			fail("no Exception expected");
		}
	}
}
