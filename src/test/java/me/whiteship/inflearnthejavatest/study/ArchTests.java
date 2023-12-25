package me.whiteship.inflearnthejavatest.study;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class ArchTests {

    @Test
    void packageDependencyTests() {
        JavaClasses classes = new ClassFileImporter().importPackages("me.whiteship.inflearnthejavatest");

        /**
         * TODO ..domain.. 패키지에 있는 클래스는 ..study.., ..member.., ..domain에서 참조 가능.
         * TODO ..member.. 패키지에 있는 클래스는 ..study..와 ..member..에서만 참조 가능.
         * TODO (반대로) ..domain.. 패키지는 ..member.. 패키지를 참조하지 못한다.
         * TODO ..study.. 패키지에 있는 클래스는 ..study.. 에서만 참조 가능.
         * TODO 순환 참조 없어야 한다.
         */
        ArchRule domainPackageRule = classes().that().resideInAPackage("..domain..")
                .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("..study..", "..member..", "..domain..");

        domainPackageRule.check(classes);

        ArchRule memberPackageRule = noClasses().that().resideInAPackage("..domain..")
                .should().accessClassesThat().resideInAPackage("..member..");

        memberPackageRule.check(classes);

        ArchRule studyPackageRule = noClasses().that().resideOutsideOfPackage("..study..")
                .should().accessClassesThat().resideInAnyPackage("..study..");

        studyPackageRule.check(classes);

        ArchRule freeOfCycles = slices().matching("..inflearnthejavatest.(*)..").should().beFreeOfCycles();

        freeOfCycles.check(classes);
    }
}
