package org.tests.o2m.jointable;

import io.ebean.BaseTestCase;
import io.ebean.Ebean;
import org.ebeantest.LoggedSqlCollector;
import org.junit.Test;
import org.tests.o2m.jointable.inheritance.ClassA;
import org.tests.o2m.jointable.inheritance.ClassB;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestOneToManyJoinTableInheritance extends BaseTestCase {

  private JtMonkey m0 = new JtMonkey("Sim");
  private JtMonkey m1 = new JtMonkey("Tim");
  private JtMonkey m2 = new JtMonkey("Uim");

  private ClassA classA = new ClassA();
  private ClassB classB = new ClassB();

  @Test
  public void testSave() {

    classA.getMonkeys().add(m0);
    classA.getMonkeys().add(m1);
    classB.getMonkeys().add(m2);

    LoggedSqlCollector.start();

    Ebean.saveAll(Arrays.asList(classA, classB));

    List<String> sql = LoggedSqlCollector.current();

    assertThat(sql).hasSize(11);
    assertThat(sql.get(0)).contains("insert into class_super ");
    assertThat(sql.get(2)).contains("insert into monkey ");
    assertThat(sql.get(2)).contains("insert into monkey ");
    assertThat(sql.get(5)).contains("insert into class_super_monkey ");
    assertThat(sql.get(6)).contains("insert into class_super ");
    assertThat(sql.get(8)).contains("insert into monkey ");
    assertThat(sql.get(10)).contains("insert into class_super_monkey ");

    ClassA dbA = Ebean.find(ClassA.class, 1);
    ClassB dbB = Ebean.find(ClassB.class, 2);

    assertThat(dbA.getMonkeys()).hasSize(2);
    assertThat(dbB.getMonkeys()).hasSize(1);
    assertThat(dbA.getMonkeys().get(0).name).isEqualTo("Sim");
    assertThat(dbA.getMonkeys().get(1).name).isEqualTo("Tim");
    assertThat(dbB.getMonkeys().get(0).name).isEqualTo("Uim");
  }
}
