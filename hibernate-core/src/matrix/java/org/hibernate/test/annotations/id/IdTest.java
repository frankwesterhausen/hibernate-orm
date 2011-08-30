/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.annotations.id;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.mapping.Column;

import org.junit.Test;

import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.test.annotations.id.entities.Ball;
import org.hibernate.test.annotations.id.entities.BreakDance;
import org.hibernate.test.annotations.id.entities.Computer;
import org.hibernate.test.annotations.id.entities.Department;
import org.hibernate.test.annotations.id.entities.Dog;
import org.hibernate.test.annotations.id.entities.FirTree;
import org.hibernate.test.annotations.id.entities.Footballer;
import org.hibernate.test.annotations.id.entities.FootballerPk;
import org.hibernate.test.annotations.id.entities.Furniture;
import org.hibernate.test.annotations.id.entities.GoalKeeper;
import org.hibernate.test.annotations.id.entities.Home;
import org.hibernate.test.annotations.id.entities.Monkey;
import org.hibernate.test.annotations.id.entities.Phone;
import org.hibernate.test.annotations.id.entities.Shoe;
import org.hibernate.test.annotations.id.entities.SoundSystem;
import org.hibernate.test.annotations.id.entities.Store;
import org.hibernate.test.annotations.id.entities.Tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Emmanuel Bernard
 */
@SuppressWarnings("unchecked")
public class IdTest extends BaseCoreFunctionalTestCase {
	@Test
	public void testGenericGenerator() throws Exception {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		SoundSystem system = new SoundSystem();
		system.setBrand("Genelec");
		system.setModel("T234");
		Furniture fur = new Furniture();
		s.persist(system);
		s.persist(fur);
		tx.commit();
		s.close();

		s = openSession();
		tx = s.beginTransaction();
		system = (SoundSystem) s.get(SoundSystem.class, system.getId());
		fur = (Furniture) s.get(Furniture.class, fur.getId());
		assertNotNull(system);
		assertNotNull(fur);
		s.delete(system);
		s.delete(fur);
		tx.commit();
		s.close();
	}

	/*
	 * Ensures that GenericGenerator annotations wrapped inside a
	 * GenericGenerators holder are bound correctly
	 */
	@Test
	public void testGenericGenerators() throws Exception {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		Monkey monkey = new Monkey();
		s.persist(monkey);
		s.flush();
		assertNotNull(monkey.getId());
		tx.rollback();
		s.close();
	}

	@Test
	public void testTableGenerator() throws Exception {
		Session s = openSession();
		Transaction tx = s.beginTransaction();

		Ball b = new Ball();
		Dog d = new Dog();
		Computer c = new Computer();
		s.persist( b );
		s.persist( d );
		s.persist(c);
		tx.commit();
		s.close();
		assertEquals("table id not generated", new Integer(1), b.getId());
		assertEquals("generator should not be shared", new Integer(1), d
				.getId());
		assertEquals("default value should work", new Long(1), c.getId());

		s = openSession();
		tx = s.beginTransaction();
		s.delete(s.get(Ball.class, new Integer(1)));
		s.delete(s.get(Dog.class, new Integer(1)));
		s.delete(s.get(Computer.class, new Long(1)));
		tx.commit();
		s.close();
	}

	@Test
	public void testSequenceGenerator() throws Exception {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		Shoe b = new Shoe();
		s.persist(b);
		tx.commit();
		s.close();
		assertNotNull(b.getId());

		s = openSession();
		tx = s.beginTransaction();
		s.delete(s.get(Shoe.class, b.getId()));
		tx.commit();
		s.close();
	}

	@Test
	public void testClassLevelGenerator() throws Exception {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		Store b = new Store();
		s.persist(b);
		tx.commit();
		s.close();
		assertNotNull(b.getId());

		s = openSession();
		tx = s.beginTransaction();
		s.delete(s.get(Store.class, b.getId()));
		tx.commit();
		s.close();
	}

	@Test
	public void testMethodLevelGenerator() throws Exception {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		Department b = new Department();
		s.persist(b);
		tx.commit();
		s.close();
		assertNotNull( b.getId() );

		s = openSession();
		tx = s.beginTransaction();
		s.delete(s.get(Department.class, b.getId()));
		tx.commit();
		s.close();
	}

	@Test
	public void testDefaultSequence() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		Home h = new Home();
		s.persist(h);
		tx.commit();
		s.close();
		assertNotNull(h.getId());

		s = openSession();
		tx = s.beginTransaction();
		Home reloadedHome = (Home) s.get(Home.class, h.getId());
		assertEquals(h.getId(), reloadedHome.getId());
		s.delete(reloadedHome);
		tx.commit();
		s.close();
	}

	@Test
	public void testParameterizedAuto() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		Home h = new Home();
		s.persist(h);
		tx.commit();
		s.close();
		assertNotNull(h.getId());

		s = openSession();
		tx = s.beginTransaction();
		Home reloadedHome = (Home) s.get(Home.class, h.getId());
		assertEquals( h.getId(), reloadedHome.getId() );
		s.delete(reloadedHome);
		tx.commit();
		s.close();
	}

	@Test
	public void testIdInEmbeddableSuperclass() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		FirTree chrismasTree = new FirTree();
		s.persist(chrismasTree);
		tx.commit();
		s.clear();
		tx = s.beginTransaction();
		chrismasTree = (FirTree) s.get(FirTree.class, chrismasTree.getId());
		assertNotNull(chrismasTree);
		s.delete(chrismasTree);
		tx.commit();
		s.close();
	}

	@Test
	public void testIdClass() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		Footballer fb = new Footballer("David", "Beckam", "Arsenal");
		GoalKeeper keeper = new GoalKeeper("Fabien", "Bartez", "OM");
		s.persist(fb);
		s.persist(keeper);
		tx.commit();
		s.clear();

		// lookup by id
		tx = s.beginTransaction();
		FootballerPk fpk = new FootballerPk("David", "Beckam");
		fb = (Footballer) s.get(Footballer.class, fpk);
		FootballerPk fpk2 = new FootballerPk("Fabien", "Bartez");
		keeper = (GoalKeeper) s.get(GoalKeeper.class, fpk2);
		assertNotNull(fb);
		assertNotNull(keeper);
		assertEquals("Beckam", fb.getLastname());
		assertEquals("Arsenal", fb.getClub());
		assertEquals(1, s.createQuery(
				"from Footballer f where f.firstname = 'David'").list().size());
		tx.commit();

		// reattach by merge
		tx = s.beginTransaction();
		fb.setClub("Bimbo FC");
		s.merge(fb);
		tx.commit();

		// reattach by saveOrUpdate
		tx = s.beginTransaction();
		fb.setClub("Bimbo FC SA");
		s.saveOrUpdate(fb);
		tx.commit();

		// clean up
		s.clear();
		tx = s.beginTransaction();
		fpk = new FootballerPk("David", "Beckam");
		fb = (Footballer) s.get(Footballer.class, fpk);
		assertEquals("Bimbo FC SA", fb.getClub());
		s.delete(fb);
		s.delete(keeper);
		tx.commit();
		s.close();
	}

	@Test
	public void testColumnDefinition() {
		Column idCol = (Column) configuration().getClassMapping(Ball.class.getName())
				.getIdentifierProperty().getValue().getColumnIterator().next();
		assertEquals( "ball_id", idCol.getName() );
	}

	@Test
	public void testLowAllocationSize() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		int size = 4;
		BreakDance[] bds = new BreakDance[size];
		for (int i = 0; i < size; i++) {
			bds[i] = new BreakDance();
			s.persist(bds[i]);
		}
		s.flush();
		for (int i = 0; i < size; i++) {
			assertEquals(i + 1, bds[i].id.intValue());
		}
		tx.rollback();
		s.close();
	}

	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] { Ball.class, Shoe.class, Store.class,
				Department.class, Dog.class, Computer.class, Home.class,
				Phone.class, Tree.class, FirTree.class, Footballer.class,
				SoundSystem.class, Furniture.class, GoalKeeper.class,
				BreakDance.class, Monkey.class};
	}

	@Override
	protected String[] getAnnotatedPackages() {
		return new String[] { "org.hibernate.test.annotations",
				"org.hibernate.test.annotations.id" };
	}

	@Override
	protected String[] getXmlFiles() {
		return new String[] { "org/hibernate/test/annotations/orm.xml" };
	}
}