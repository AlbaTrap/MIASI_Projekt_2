package com.example.springboot_backend.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.example.springboot_backend", importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class,
        ImportOption.DoNotIncludeArchives.class
})
public class ArchitectureTest {

    @ArchTest
    static final ArchRule layered_architecture_should_be_respected = layeredArchitecture()
            .consideringOnlyDependenciesInAnyPackage("com.example.springboot_backend..")
            .layer("API").definedBy("..api..")
            .layer("Application").definedBy("..application..")
            .layer("Domain").definedBy("..domain..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .layer("Shared").definedBy("..shared..")

            .whereLayer("API").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("API", "Infrastructure")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "API")
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .ignoreDependency(com.tngtech.archunit.base.DescribedPredicate.alwaysTrue(), resideInAnyPackage("java..", "jakarta..", "org.springframework..", "..mapper.."));

    @ArchTest
    static final ArchRule domain_should_not_access_infrastructure = com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses()
            .that().resideInAPackage("..domain..")
            .should().accessClassesThat().resideInAPackage("..infrastructure..");
}
