/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.beuth.clara.claraSoftware.domain.imports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.beuth.clara.claraSoftware.domain.Article;
import de.beuth.clara.claraSoftware.domain.Amount;

/** Test driver for the Rich Domain Object {@linkplain ArticleRepository}
 * @author Can Heilgermann
 */
@RunWith(SpringRunner.class)
// @DataJpaTest
// @AutoConfigureTestDatabase
@SpringBootTest
public class ArticleRepositoryTest {

	
	@Autowired
	private CleanUpService cleanUpService;
	
	@Autowired
	private ArticleRepository articleRepo;

	@Before
	public void cleanUp() {
		cleanUpService.deleteAll();
	}
	
	@After
	public void cleanUpAgain() {
		cleanUpService.deleteAll();
	}

	@Test
	public void createArticleCheckPropertiesTest() {

		final Article gtx1080 = articleRepo
				.save(new Article("Nvidia GTX1080", "Powerful graphic card for next level gaming", new Amount(680)));

		assertNotNull(gtx1080);
		assertNotNull(gtx1080.getId());
		assertTrue(gtx1080.getId() > 0);
		Optional<Article> found = articleRepo.find(gtx1080.getId());
		assertEquals(found.get(), gtx1080);

	}

	@Test
	public void findArticleTest() {

		final Article gtx1080 = articleRepo
				.save(new Article("Nvidia GTX1080", "Powerful graphic card for next level gaming", new Amount(680)));
		final Article i7 = articleRepo
				.save(new Article("Intel Core i7", "Powerful CPU for incredible power", new Amount(340)));
		final Article nier = articleRepo.save(new Article("Nier Automata",
				"If you've ever wanted to fight evil robot clowns, now you can.", new Amount(60)));

		// Test auf ID
		assertTrue(gtx1080.getId() > 0);
		assertTrue(i7.getId() > 0);
		assertTrue(nier.getId() > 0);

		// Article finden
		final Optional<Article> foundgtx1080 = articleRepo.find(gtx1080.getId());
		final Optional<Article> foundi7 = articleRepo.find(i7.getId());
		final Optional<Article> foundnier = articleRepo.find(nier.getId());

		// Test: ist der gefundene Article richtig?
		assertEquals(foundgtx1080.get(), gtx1080);
		assertEquals(foundi7.get(), i7);
		assertEquals(foundnier.get(), nier);

	}

	@Test
	public void findAllArticlesTest() {

		final Article gtx1080 = articleRepo
				.save(new Article("Nvidia GTX1080", "Powerful graphic card for next level gaming", new Amount(680)));
		final Article i7 = articleRepo
				.save(new Article("Intel Core i7", "Powerful CPU for incredible power", new Amount(340)));
		final Article nier = articleRepo.save(new Article("Nier Automata",
				"If you've ever wanted to fight evil robot clowns, now you can.", new Amount(60)));

		List<Article> foundAll = articleRepo.findAll();
		// Liste darf nicht leer sein, 3 Article vorhanden
		assertFalse(foundAll.isEmpty());
		assertEquals(foundAll.size(), 3);

		// Liste muss die folgenden Article beinhalten
		assertTrue(foundAll.contains(gtx1080));
		assertTrue(foundAll.contains(i7));
		assertTrue(foundAll.contains(nier));

		assertEquals(foundAll.get(2), gtx1080);
		assertEquals(foundAll.get(1), i7);
		assertEquals(foundAll.get(0), nier);
	}

	@Test
	public void deleteArticleTest() {

		final Article gtx1080 = articleRepo
				.save(new Article("Nvidia GTX1080", "Powerful graphic card for next level gaming", new Amount(680)));
		final Article i7 = articleRepo
				.save(new Article("Intel Core i7", "Powerful CPU for incredible power", new Amount(340)));
		final Article nier = articleRepo.save(new Article("Nier Automata",
				"If you've ever wanted to fight evil robot clowns, now you can.", new Amount(60)));

		// Test: noch alle vorhanden
		assertEquals(articleRepo.find(gtx1080.getId()).get(), gtx1080);
		assertEquals(articleRepo.find(i7.getId()).get(), i7);
		assertEquals(articleRepo.find(nier.getId()).get(), nier);

		// Test: Loeschen eines Articles, die anderen muessen aber noch vorhanden sein
		articleRepo.delete(articleRepo.find(gtx1080.getId()).get());

		assertEquals(articleRepo.find(gtx1080.getId()), Optional.empty());
		assertEquals(articleRepo.find(i7.getId()).get(), i7);
		assertEquals(articleRepo.find(nier.getId()).get(), nier);
	}

	@Test
	public void deleteAllArticlesTest() {

		final Article gtx1080 = articleRepo
				.save(new Article("Nvidia GTX1080", "Powerful graphic card for next level gaming", new Amount(680)));
		final Article i7 = articleRepo
				.save(new Article("Intel Core i7", "Powerful CPU for incredible power", new Amount(340)));
		final Article nier = articleRepo.save(new Article("Nier Automata",
				"If you've ever wanted to fight evil robot clowns, now you can.", new Amount(60)));

		assertTrue(!(articleRepo.findAll().isEmpty())); // is not empty
		assertEquals(articleRepo.findAll().size(), 3);
		articleRepo.deleteAll();

		assertTrue(articleRepo.findAll().isEmpty()); // should be empty
		assertEquals(articleRepo.findAll().size(), 0);
		assertEquals(articleRepo.find(gtx1080.getId()), Optional.empty());
		assertEquals(articleRepo.find(i7.getId()), Optional.empty());
		assertEquals(articleRepo.find(nier.getId()), Optional.empty());

	}

}
