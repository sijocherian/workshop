/*
 * Copyright 2013-2017 the original author or authors.
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
package dev.cjo.starter.ms.persist.elasticsearch;

import dev.cjo.starter.ms.syndtarget.config.PersistenceConfig;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.util.*;

import static org.apache.commons.lang.RandomStringUtils.randomNumeric;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/*
import org.springframework.data.elasticsearch.entities.Author;
import org.springframework.data.elasticsearch.entities.Book;
import org.springframework.data.elasticsearch.entities.GirlFriend;
import org.springframework.data.elasticsearch.entities.PersonMultipleLevelNested;*/
/**
 * Credits
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Artur Konczak
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath:/repository-test-nested-object.xml")
@ContextConfiguration(classes = PersistenceConfig.class)
@TestPropertySource(properties = {
		"elasticsearch.datadir=/tmp/person-data",
		"elasticsearch.home=/tmp/es"
})
public class TryNestedObjectTests {

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;


	@Before
	public void before() {
		elasticsearchTemplate.deleteIndex(Person.class);
		elasticsearchTemplate.createIndex(Person.class);
		elasticsearchTemplate.putMapping(Person.class);
		elasticsearchTemplate.refresh(Person.class);
		/*
		elasticsearchTemplate.deleteIndex(Book.class);
		elasticsearchTemplate.createIndex(Book.class);
		elasticsearchTemplate.putMapping(Book.class);
		elasticsearchTemplate.refresh(Book.class);

		elasticsearchTemplate.deleteIndex(PersonMultipleLevelNested.class);
		elasticsearchTemplate.createIndex(PersonMultipleLevelNested.class);
		elasticsearchTemplate.putMapping(PersonMultipleLevelNested.class);
		elasticsearchTemplate.refresh(PersonMultipleLevelNested.class);*/
	}

	@Test
	public void shouldIndexInitialLevelNestedObject() {

		final List<Car> cars = new ArrayList<>();

		final Car saturn = new Car();
		saturn.setName("Saturn");
		saturn.setModel("SL");

		final Car subaru = new Car();
		subaru.setName("Subaru");
		subaru.setModel("Imprezza");

		final Car ford = new Car();
		ford.setName("Ford");
		ford.setModel("Focus");

		cars.add(saturn);
		cars.add(subaru);
		cars.add(ford);



		final Person foo = new Person();
		foo.setName("Foo");
		foo.setId("1");
		foo.setCar(cars);



		//bar
		final Car car = new Car();
		car.setName("Saturn");
		car.setModel("Imprezza");
		car.setReleaseTime(Instant.now().getEpochSecond());


		final Person bar = new Person();
		bar.setId("2");
		bar.setName("Bar");
		bar.setCar(Arrays.asList(car));

		final List<IndexQuery> indexQueries = new ArrayList<>();
		final IndexQuery indexQuery1 = new IndexQuery();
		indexQuery1.setId(foo.getId());
		indexQuery1.setObject(foo);

		final IndexQuery indexQuery2 = new IndexQuery();
		indexQuery2.setId(bar.getId());
		indexQuery2.setObject(bar);

		indexQueries.add(indexQuery1);
		indexQueries.add(indexQuery2);

		elasticsearchTemplate.putMapping(Person.class);
		elasticsearchTemplate.bulkIndex(indexQueries);
		//elasticsearchTemplate.refresh(Person.class);

		//simple test
		//final QueryBuilder builder = termQuery("name", "Bar") ;
		//final QueryBuilder builder = matchAllQuery(); // nestedQuery("car", boolQuery().must(termQuery("car.name", "saturn")) );
		//final QueryBuilder builder = boolQuery().must(termQuery("car.name", "Subaru")) ;
				//.should(termQuery("car.model", "imprezza"));

		//NESTED
		final QueryBuilder builder = nestedQuery("car", boolQuery().must(termQuery("car.name", "subaru")).must(termQuery("car.model", "imprezza")));
		//final QueryBuilder builder = nestedQuery("car", boolQuery().must(termQuery("car.name", "subaru")).should(termQuery("car.model", "imprezza")));
		System.out.println( "Query string: "+ 				builder.toString() );

		final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();
		final List<Person> persons = elasticsearchTemplate.queryForList(searchQuery, Person.class);

		System.out.println( "Return 1");
		for(Person p : persons)
			for(Car c : p.getCar())
				System.out.println( "Person "+ p.getId() + " ,Car "+c.getName() + " date "+c.getLastModDate());

		Person toUpdate =persons.get(0);
		toUpdate.getCar().get(0).setName( "Tesla1");

		final IndexQuery indexQuery3 = new IndexQuery();
		indexQuery3.setId(toUpdate.getId());
		indexQuery3.setObject(toUpdate);

		elasticsearchTemplate.index(indexQuery3);
		elasticsearchTemplate.refresh(Person.class);

		final List<Person> persons3 = elasticsearchTemplate.queryForList(searchQuery, Person.class);

		System.out.println( "Return 1");
		for(Person p : persons3)
			for(Car c : p.getCar())
				System.out.println( "Person "+ p.getId() + " ,Car "+c.getName() + " date "+c.getLastModDate());

		assertThat(persons.size(), is(1));
	}

	/*@Test
	public void shouldIndexMultipleLevelNestedObject() {
		//given
		final List<IndexQuery> indexQueries = createPerson();

		//when
		elasticsearchTemplate.putMapping(PersonMultipleLevelNested.class);
		elasticsearchTemplate.bulkIndex(indexQueries);
		elasticsearchTemplate.refresh(PersonMultipleLevelNested.class);

		//then
		final GetQuery getQuery = new GetQuery();
		getQuery.setId("1");
		final PersonMultipleLevelNested personIndexed = elasticsearchTemplate.queryForObject(getQuery, PersonMultipleLevelNested.class);
		assertThat(personIndexed, is(notNullValue()));
	}

	@Test
	public void shouldIndexMultipleLevelNestedObjectWithIncludeInParent() {
		//given
		final List<IndexQuery> indexQueries = createPerson();

		//when
		elasticsearchTemplate.putMapping(PersonMultipleLevelNested.class);
		elasticsearchTemplate.bulkIndex(indexQueries);
		// then

		final Map mapping = elasticsearchTemplate.getMapping(PersonMultipleLevelNested.class);

		assertThat(mapping, is(notNullValue()));
		final Map propertyMap = (Map) mapping.get("properties");
		assertThat(propertyMap, is(notNullValue()));
		final Map bestCarsAttributes = (Map) propertyMap.get("bestCars");
		assertThat(bestCarsAttributes.get("include_in_parent"), is(notNullValue()));
	}


	@Test
	public void shouldSearchUsingNestedQueryOnMultipleLevelNestedObject() {
		//given
		final List<IndexQuery> indexQueries = createPerson();

		//when
		elasticsearchTemplate.putMapping(PersonMultipleLevelNested.class);
		elasticsearchTemplate.bulkIndex(indexQueries);
		elasticsearchTemplate.refresh(PersonMultipleLevelNested.class);

		//then
		final BoolQueryBuilder builder = boolQuery();
		builder.must(nestedQuery("girlFriends", termQuery("girlFriends.type", "temp"), ScoreMode.None))
				.must(nestedQuery("girlFriends.cars", termQuery("girlFriends.cars.name", "Ford".toLowerCase()), ScoreMode.None));

		final SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withQuery(builder)
				.build();

		final Page<PersonMultipleLevelNested> personIndexed = elasticsearchTemplate.queryForPage(searchQuery, PersonMultipleLevelNested.class);
		assertThat(personIndexed, is(notNullValue()));
		assertThat(personIndexed.getTotalElements(), is(1L));
		assertThat(personIndexed.getContent().get(0).getId(), is("1"));
	}


	private List<IndexQuery> createPerson() {

		final PersonMultipleLevelNested person1 = new PersonMultipleLevelNested();

		person1.setId("1");
		person1.setName("name");

		final Car saturn = new Car();
		saturn.setName("Saturn");
		saturn.setModel("SL");

		final Car subaru = new Car();
		subaru.setName("Subaru");
		subaru.setModel("Imprezza");

		final Car car = new Car();
		car.setName("Saturn");
		car.setModel("Imprezza");

		final Car ford = new Car();
		ford.setName("Ford");
		ford.setModel("Focus");

		final GirlFriend permanent = new GirlFriend();
		permanent.setName("permanent");
		permanent.setType("permanent");
		permanent.setCars(Arrays.asList(saturn, subaru));

		final GirlFriend temp = new GirlFriend();
		temp.setName("temp");
		temp.setType("temp");
		temp.setCars(Arrays.asList(car, ford));

		person1.setGirlFriends(Arrays.asList(permanent, temp));

		final IndexQuery indexQuery1 = new IndexQuery();
		indexQuery1.setId(person1.getId());
		indexQuery1.setObject(person1);

		final PersonMultipleLevelNested person2 = new PersonMultipleLevelNested();

		person2.setId("2");
		person2.setName("name");

		person2.setGirlFriends(Arrays.asList(permanent));

		final IndexQuery indexQuery2 = new IndexQuery();
		indexQuery2.setId(person2.getId());
		indexQuery2.setObject(person2);

		final List<IndexQuery> indexQueries = new ArrayList<>();
		indexQueries.add(indexQuery1);
		indexQueries.add(indexQuery2);

		return indexQueries;
	}

	@Test
	public void shouldSearchBooksForPersonInitialLevelNestedType() {

		final List<Car> cars = new ArrayList<>();

		final Car saturn = new Car();
		saturn.setName("Saturn");
		saturn.setModel("SL");

		final Car subaru = new Car();
		subaru.setName("Subaru");
		subaru.setModel("Imprezza");

		final Car ford = new Car();
		ford.setName("Ford");
		ford.setModel("Focus");

		cars.add(saturn);
		cars.add(subaru);
		cars.add(ford);

		final Book java = new Book();
		java.setId("1");
		java.setName("java");
		final Author javaAuthor = new Author();
		javaAuthor.setId("1");
		javaAuthor.setName("javaAuthor");
		java.setAuthor(javaAuthor);

		final Book spring = new Book();
		spring.setId("2");
		spring.setName("spring");
		final Author springAuthor = new Author();
		springAuthor.setId("2");
		springAuthor.setName("springAuthor");
		spring.setAuthor(springAuthor);

		final Person foo = new Person();
		foo.setName("Foo");
		foo.setId("1");
		foo.setCar(cars);
		foo.setBooks(Arrays.asList(java, spring));

		final Car car = new Car();
		car.setName("Saturn");
		car.setModel("Imprezza");

		final Person bar = new Person();
		bar.setId("2");
		bar.setName("Bar");
		bar.setCar(Arrays.asList(car));

		final List<IndexQuery> indexQueries = new ArrayList<>();
		final IndexQuery indexQuery1 = new IndexQuery();
		indexQuery1.setId(foo.getId());
		indexQuery1.setObject(foo);

		final IndexQuery indexQuery2 = new IndexQuery();
		indexQuery2.setId(bar.getId());
		indexQuery2.setObject(bar);

		indexQueries.add(indexQuery1);
		indexQueries.add(indexQuery2);

		elasticsearchTemplate.putMapping(Person.class);
		elasticsearchTemplate.bulkIndex(indexQueries);
		elasticsearchTemplate.refresh(Person.class);

		final QueryBuilder builder = nestedQuery("books", boolQuery().must(termQuery("books.name", "java")), ScoreMode.None);

		final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();
		final List<Person> persons = elasticsearchTemplate.queryForList(searchQuery, Person.class);

		assertThat(persons.size(), is(1));
	}

	*//*
	DATAES-73
	 *//*
	@Test
	public void shouldIndexAndSearchMapAsNestedType() {
		//given
		final Book book1 = new Book();
		final Book book2 = new Book();

		book1.setId(randomNumeric(5));
		book1.setName("testBook1");

		book2.setId(randomNumeric(5));
		book2.setName("testBook2");

		final Map<Integer, Collection<String>> map1 = new HashMap<>();
		map1.put(1, Arrays.asList("test1", "test2"));

		final Map<Integer, Collection<String>> map2 = new HashMap<>();
		map2.put(1, Arrays.asList("test3", "test4"));

		book1.setBuckets(map1);
		book2.setBuckets(map2);

		final List<IndexQuery> indexQueries = new ArrayList<>();
		final IndexQuery indexQuery1 = new IndexQuery();
		indexQuery1.setId(book1.getId());
		indexQuery1.setObject(book1);

		final IndexQuery indexQuery2 = new IndexQuery();
		indexQuery2.setId(book2.getId());
		indexQuery2.setObject(book2);

		indexQueries.add(indexQuery1);
		indexQueries.add(indexQuery2);
		//when
		elasticsearchTemplate.bulkIndex(indexQueries);
		elasticsearchTemplate.refresh(Book.class);
		//then
		final SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withQuery(nestedQuery("buckets", termQuery("buckets.1", "test3"), ScoreMode.None))
				.build();
		final Page<Book> books = elasticsearchTemplate.queryForPage(searchQuery, Book.class);

		assertThat(books.getContent().size(), is(1));
		assertThat(books.getContent().get(0).getId(), is(book2.getId()));
	}*/
}

