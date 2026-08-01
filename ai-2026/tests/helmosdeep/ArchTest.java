package helmosdeep;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SuiteDisplayName;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Valide les dépendances entre les paquetages.
 * */
@SuiteDisplayName("Validation de l'architecture")
class ArchTest {
	private static final String PROJECT = ArchTest.class.getPackageName();

	private static final String DOMAINS = PROJECT+".domains..";
	private static final String SUPERVISORS = PROJECT+".supervisors..";
	private static final String UTIL = PROJECT+".util..";
	private static final String VIEWS = PROJECT+".views..";
	
	@Test
	@DisplayName("Le domaine peut dépendre de util et du domaine")
	void domains_types_are_indenpendant() {
		var classes = new ClassFileImporter().importPackages(PROJECT);
		
		ArchRule packageAccessRule = classes()
			    .that().resideInAPackage(DOMAINS).and().haveSimpleNameNotContaining("Test")
			    .should().onlyAccessClassesThat()
			    .resideInAnyPackage(DOMAINS, UTIL, "java.lang..", "java.math..", "java.time..", "java.util..", "org.junit.jupiter.api..");
		
		packageAccessRule.check(classes);
	}
	
	@Test
	@DisplayName("Les classes utilitaires se suffisent à elle-même")
	void utils_types_are_indenpendant() {
		var classes = new ClassFileImporter().importPackages(PROJECT);
		
		var packageAccess = classes()
			    .that().resideInAPackage(UTIL).and().haveSimpleNameNotEndingWith("Test")
			    .should().onlyAccessClassesThat()
			    .resideInAnyPackage(UTIL, "java.lang..", "java.math..", "java.time..", "java.util..", "java.io..", "java.nio..");
		
		var allowedClassed = classes()
		    .that().resideInAPackage(UTIL)
		    .should().haveSimpleName("TxtFileReader")
		    .orShould().haveSimpleName("Contract");
		
		packageAccess.check(classes);
		allowedClassed.check(classes);
	}
	
	@Test
	@DisplayName("Les superviseurs dépendent du domaine")
	void supervisors_depend_only_on_domains_and_utils() {
		var classes = new ClassFileImporter().importPackages(PROJECT);
		
		ArchRule myRule = classes()
			    .that().resideInAPackage(SUPERVISORS).and().haveSimpleNameNotEndingWith("Test").and().haveSimpleNameNotStartingWith("Fake")
			    .should().onlyAccessClassesThat()
			    .resideInAnyPackage(DOMAINS,SUPERVISORS, UTIL ,"java.awt..", "java.lang..", "java.math..", "java.time..", "java.util..");
		
		myRule.check(classes);
	}
	
	@Test
	void views_depend_only_on_supervisors() {
		var classes = new ClassFileImporter().importPackages(PROJECT);
		
		ArchRule myRule = classes()
			    .that().resideInAPackage(VIEWS)
			    .should().onlyAccessClassesThat()
			    .resideInAnyPackage(PROJECT, VIEWS,SUPERVISORS, "ai.engine..", "java..", "javax..");
		
		myRule.check(classes);
	}
}

